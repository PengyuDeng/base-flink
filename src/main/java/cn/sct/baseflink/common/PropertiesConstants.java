package cn.sct.baseflink.common;

/**
 * @author bigdata_dpy
 */
public interface PropertiesConstants {
    /**
     * flink常用配置
     */
    String REST_PORT = "rest.port";
    String STREAM_NAME = "flink.name";
    String STREAM_PARALLELISM = "flink.stream.parallelism";
    String STREAM_SOURCE_PARALLELISM = "flink.stream.source.parallelism";
    String STREAM_SINK_PARALLELISM = "flink.stream.sink.parallelism";
    String STREAM_MAX_PARALLELISM = "flink.stream.max.parallelism";
    String STREAM_CHECKPOINT_ENABLE = "flink.stream.checkpoint.enable";
    String STREAM_CHECKPOINT_DIR = "flink.stream.checkpoint.dir";
    String STREAM_CHECKPOINT_DIR_CLEANUP = "flink.stream.checkpoint.dir.cleanup";
    String STREAM_CHECKPOINT_TYPE = "flink.stream.checkpoint.type";
    String STREAM_CHECKPOINT_INTERVAL = "flink.stream.checkpoint.interval";
    String STREAM_DISABLE_OPERATOR_CHAINING = "flink.stream.disableOperatorChaining";

    /**
     * 文件源配置
     */
    String SOURCE_FILE = "source.file";
    String SOURCE_DIR = "source.dir";
    /**
     * kafka源配置
     */
    String SOURCE_KAFKA_BROKERS = "source.kafka.brokers";
    String SOURCE_KAFKA_GROUP_ID = "source.kafka.groupId";
    String SOURCE_KAFKA_CONSUMER_TOPIC = "source.kafka.consumer.topic";
    String SOURCE_KAFKA_AUTO_OFFSET_RESET_CONFIG = "source.kafka.consumer.auto.offset.reset";
    String SOURCE_KAFKA_ISOLATION_LEVEL_CONFIG = "source.kafka.consumer.isolation.level.config";
    String SOURCE_KAFKA_COMMIT_OFFSETS_ON_CHECKPOINT = "source.kafka.consumer.commit.offsets.on.checkpoint";
    /**
     * kafka生产者配置信息
     */
    String SINK_KAFKA_BROKERS = "sink.kafka.brokers";
    String SINK_KAFKA_PRODUCER_TOPIC = "sink.kafka.producer.topic";
    String SINK_KAFKA_PRODUCER_TRANSACTION_TIMEOUT = "sink.kafka.producer.transaction.timeout.ms";
    /**
     * sink数据库相关
     */
    String SINK_JDBC_URL = "sink.jdbc.url";
    String SINK_JDBC_TABLE_NAME = "sink.jdbc.table.name";
}
