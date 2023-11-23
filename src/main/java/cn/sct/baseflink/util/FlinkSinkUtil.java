package cn.sct.baseflink.util;

import cn.sct.baseflink.annotation.Column;
import cn.sct.baseflink.annotation.NoSink;
import cn.sct.baseflink.annotation.Table;
import cn.sct.baseflink.common.ProjectConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import static cn.sct.baseflink.common.PropertiesConstants.*;


/**
 * @author bigdata_dpy
 */
public class FlinkSinkUtil {


    public static <T> void writeToKafka(DataStream<T> stream, String topic, ParameterTool parameterTool) {
        stream
                .map(JSON::toJSONString)
                .addSink(getKafkaSink(topic, parameterTool))
                .name(ProjectConstants.TOPIC + ":" + topic)
                .setParallelism(parameterTool.getInt(STREAM_SINK_PARALLELISM))
        ;
    }

    public static <T> void writeToDatabase(DataStream<T> stream, Class<T> tClass, String driver, String url) {
        stream
                .addSink(getJdbcSink(tClass, driver, url))
                .name(ProjectConstants.TABLE + ":" + tClass.getAnnotation(Table.class).name())
        ;
    }


    public static <T> void writeToDatabase(DataStream<T> stream, Class<T> tClass, String driver, String url, ParameterTool parameterTool) {
        stream
                .addSink(getJdbcSink(tClass, driver, url))
                .setParallelism(parameterTool.getInt(STREAM_SINK_PARALLELISM))
                .name(ProjectConstants.TABLE + ":" + tClass.getAnnotation(Table.class).name())
        ;
    }


    private static FlinkKafkaProducer<String> getKafkaSink(String topic, ParameterTool parameterTool) {
        Properties props = new Properties();
        //sink的地址
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, parameterTool.get(SINK_KAFKA_BROKERS));
        props.setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, parameterTool.get(SINK_KAFKA_PRODUCER_TRANSACTION_TIMEOUT));
        //   props.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");
        //   props.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        return new FlinkKafkaProducer<String>("noUseTopic",
                new KafkaSerializationSchema<String>() {
                    @Override
                    public ProducerRecord<byte[], byte[]> serialize(String value, @Nullable Long timestamp) {
                        return new ProducerRecord<byte[], byte[]>(topic,
                                null,
                                JSONObject.parseObject(value).getLongValue(ProjectConstants.TIMESTAMP),
                                null,
                                value.getBytes(StandardCharsets.UTF_8));
                    }
                },
                props,
                FlinkKafkaProducer.Semantic.EXACTLY_ONCE
        );
    }

    private static <T> SinkFunction<T> getJdbcSink(Class<T> tClass, String driver, String url) {
        //拼接字段
        StringBuilder sql = new StringBuilder();
        String tableName = tClass.getAnnotation(Table.class).name();
        sql.append("insert into ").append(tableName).append("(");
        Field[] fields = tClass.getDeclaredFields();
        StringBuilder cs = new StringBuilder();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            NoSink noSink = field.getAnnotation(NoSink.class);
            Column column = field.getAnnotation(Column.class);
            // 获取注解: 如果是null表示没有这个注解
            if (noSink == null) {
                if (i < fields.length - 1) {
                    cs.append(column.name()).append(",");
                } else {
                    cs.append(column.name());
                }
            }
        }
        // 有了noSink之后, 如果最后一个字段加上NoSink, sql会多一个逗号, 去掉逗号
        cs = new StringBuilder(cs.toString().replaceAll(",$", ""));
        sql.append(cs).append(")values(");
        // 拼接占位符
        sql.append(cs.toString().replaceAll("[^,]+", "?")).append(")");
        return JdbcSink.sink(sql.toString(),
                new JdbcStatementBuilder<T>() {
                    @SneakyThrows
                    @Override
                    public void accept(PreparedStatement ps,  // sql中有占位符,
                                       T t) throws SQLException {
                        /*
                        给站位符号赋值
                         */
                        Class<?> tClass = t.getClass();
                        Field[] fields = tClass.getDeclaredFields();

                        for (int i = 0, position = 1; i < fields.length; i++) {
                            Field field = fields[i];
                            NoSink noSink = field.getAnnotation(NoSink.class);
                            if (noSink == null) {
                                // 允许访问私有属性
                                field.setAccessible(true);
                                Object v = field.get(t);
                                ps.setObject(position++, v);
                            }
                        }
                    }
                },
                new JdbcExecutionOptions.Builder()
                        // 默认是0, 表示不会按照时间向外写出
                        .withBatchIntervalMs(2000)
                        // 默认是5000
                        .withBatchSize(1024)
                        // 最大重试数量默认是3
                        .withMaxRetries(3)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withDriverName(driver)
                        .withUrl(url)
                        .build()
        );
    }
}
