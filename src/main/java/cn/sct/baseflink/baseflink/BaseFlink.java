package cn.sct.baseflink.baseflink;

import cn.sct.baseflink.util.ExecutionEnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.net.URI;
import java.net.URISyntaxException;

import static cn.sct.baseflink.common.PropertiesConstants.*;


/**
 * @author dengpengyu
 */
@Slf4j
public abstract class BaseFlink {
    protected ParameterTool parameterTool = null;

    /**
     * run方法主要是实现流式处理逻辑
     *
     * @param streamEnv 传递一个流的环境
     * @param tableEnv  传递一个表的环境
     */
    protected abstract void run(StreamExecutionEnvironment streamEnv,
                                StreamTableEnvironment tableEnv);

    protected void start(final String[] args) {
        parameterTool = ExecutionEnvUtil.getInstance(args).getParameterTool();

        //flink环境构建
        Configuration conf = new Configuration();
        if (parameterTool.has(REST_PORT)) {
            conf.setInteger(REST_PORT, parameterTool.getInt(REST_PORT));
        }
        StreamExecutionEnvironment streamEnv = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(streamEnv);
        //设置全局变量
        streamEnv.getConfig().setGlobalJobParameters(parameterTool);
        //设置flink并行度
        //获取配置文件众最大并行度，如果没用配置最大并行度就取默认的并行度*7，如果也没用配置默认并行度，取默认值为1，那么最大并行度最终值为7
        streamEnv.setMaxParallelism(parameterTool.getInt(STREAM_MAX_PARALLELISM, parameterTool.getInt(STREAM_PARALLELISM, 1) * 7));
        streamEnv.setParallelism(parameterTool.getInt(STREAM_PARALLELISM, 1));
        //算子链断开
        if (parameterTool.getBoolean(STREAM_DISABLE_OPERATOR_CHAINING, false)) {
            streamEnv.disableOperatorChaining();
        }
        //设置checkpoint
        if (parameterTool.getBoolean(STREAM_CHECKPOINT_ENABLE, true)) {
            streamEnv.enableCheckpointing(parameterTool.getInt(STREAM_CHECKPOINT_INTERVAL), CheckpointingMode.EXACTLY_ONCE);
            //设置状态后端
            try {
                /*
                支持file://  和   hdfs:// 需要注意依赖问题
                */
                streamEnv.getCheckpointConfig().setCheckpointStorage(new FileSystemCheckpointStorage(new URI(parameterTool.get(STREAM_CHECKPOINT_DIR))));
            } catch (URISyntaxException e) {
                log.error("配置状态后端路径出错");
            }
            //取消作业时保留检查点配置：请注意，在这种情况下，您必须在取消后手动清理检查点状态。
            if (parameterTool.getBoolean(STREAM_CHECKPOINT_DIR_CLEANUP)) {//true删除
                streamEnv.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);
            } else {
                streamEnv.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
            }
        }

        run(streamEnv, tableEnv);

        try {
            streamEnv.execute(parameterTool.get(STREAM_NAME));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("execute 执行出错");
        }

    }
}
