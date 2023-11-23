# base-flink



## 怎么开始

1. 你需要在pom文件里面引用本模块

2. 你需要在你main方法的Class类继承BaseFlink

3. 你需要在main方法里面新建你的类并且使用start方法，在start方法里面传入args；

   如下所示

```
public class MyClass extends BaseFlink {
    public static void main(String[] args) {
        new MyClass().start(args);
    }
}
```

## 你可以通过JDBC查询数据库并生成流

注意，这仅仅是一次JDBC查询，通常是在查询不会变动的维度数据才会使用到。

如果你想监控表的变化并且实时更新，请使用[Flink CDC](https://ververica.github.io/flink-cdc-connectors/)

```java
DataStreamSource<Row> devInput = streamEnv.createInput(getStreamFromJdbcUrl(parameterTool.get(MYSQL_DIM_URL), "root", "root", "select ID,Name from xx", MYSQL_DRIVER, new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)));

```

## 你可以方便的使用JDBCSink进行输出数据库

```
FlinkSinkUtil.writeToDatabase(otherStream, IntervelTable.class, CLICKHOUSE_DRIVER, parameterTool.get(SINK_JDBC_URL));
```

但你需要把你的Entity加上必要的注解，如下图

```java
@TableName("alarm")
public class Alarm {
    @Column("alarm_seq")
    private String alarmSeq;

    @Column(value = "alarm_id")
    private String alarmId;
}
```

## 如何使用flink的配置管理

我们在run方法里提供了parameterTool属性你可以使用parameterTool的方法获取你写在配置文件里面的内容。

## 怎么编写配置文件

配置文件遵循properties文件规范

我们提供了一些配置文件的参考

```java
package cn.sct.common;

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
```

## 怎么编写启动脚本

```bash
#!/bin/bash
source /etc/profile.d/my.sh
jar=你的flinkjar包
flink=$FLINK_HOME/bin/flink
app=你的flink启动类路径

echo "============================== start  =============================="
${flink} run -m flinkJobManager地址 \
             -Djobmanager.memory.process.size=2g \
             -Dtaskmanager.memory.process.size=2g \
             -Dtaskmanager.numberOfTaskSlots=1 \
             -d -c "${app}" "${jar}" \
             --properties_path 配置文件路径
```

## 注意事项

需要注意的是：

​	flink程序是运行在flink集群中的，为了避免依赖冲突，此项目所有的依赖都标记为<scope>provided</scope>，所以不必担心依赖冲突。

​	你在本地环境编写你的flink代码时，请自行拉取依赖。
