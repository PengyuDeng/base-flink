package cn.sct.baseflink.util;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

import static cn.sct.baseflink.common.PropertiesConstants.*;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

/**
 * @author bigdata_dpy
 */
public class FlinkSourceUtil {


    public static FlinkKafkaConsumer<String> getKafkaSource(String topic, ParameterTool parameterTool) {

        Properties props = new Properties();
        props.setProperty(BOOTSTRAP_SERVERS_CONFIG, parameterTool.get(SOURCE_KAFKA_BROKERS));
        props.setProperty(GROUP_ID_CONFIG, parameterTool.get(SOURCE_KAFKA_GROUP_ID));
        props.setProperty(AUTO_OFFSET_RESET_CONFIG, parameterTool.get(SOURCE_KAFKA_AUTO_OFFSET_RESET_CONFIG));
        props.setProperty(ISOLATION_LEVEL_CONFIG, parameterTool.get(SOURCE_KAFKA_ISOLATION_LEVEL_CONFIG));
      //  props.setProperty(ENABLE_AUTO_COMMIT_CONFIG,"false");
        FlinkKafkaConsumer<String> flinkKafkaConsumer = new FlinkKafkaConsumer<>(
                topic,
                new SimpleStringSchema(),
                props
        );

            flinkKafkaConsumer.setCommitOffsetsOnCheckpoints(true);
            flinkKafkaConsumer.setStartFromGroupOffsets();

        return flinkKafkaConsumer;
    }
}
