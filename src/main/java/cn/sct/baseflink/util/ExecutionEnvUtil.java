package cn.sct.baseflink.util;

import cn.sct.baseflink.common.ProjectConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;


/**
 * @author dengpengyu
 */
@Slf4j
public class ExecutionEnvUtil {
    private static ExecutionEnvUtil executionEnvUtil;
    private final ParameterTool parameterTool;

    private ExecutionEnvUtil(final String[] args) {
        this.parameterTool = createParameterTool(args);
    }

    public static ExecutionEnvUtil getInstance(final String[] args) {
        if (executionEnvUtil == null) {
            executionEnvUtil = new ExecutionEnvUtil(args);
        }
        return executionEnvUtil;
    }

    /**
     * ParameterTool全局参数
     * mergeWith()的会覆盖前面的
     * 暂时没用，不通过命令行传参
     *
     * @param args main 方法的参数
     * @return args配置、配置文件配置、系统配置的参数集合
     */
    private ParameterTool createParameterTool(final String[] args) {
        try {
            ParameterTool parameterTool = ParameterTool
                    .fromArgs(args);
            String propertiesFilePath = parameterTool.get(ProjectConstants.PROPERTIES_PATH);

            return parameterTool
                    .mergeWith(ParameterTool.fromPropertiesFile(propertiesFilePath))
                    .mergeWith(ParameterTool.fromSystemProperties());
        } catch (Exception e) {
            log.error("获取ParameterTool全局参数异常");
        }
        return ParameterTool.fromSystemProperties();

    }

    public ParameterTool getParameterTool() {
        return parameterTool;
    }
}