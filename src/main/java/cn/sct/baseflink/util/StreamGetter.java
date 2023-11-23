package cn.sct.baseflink.util;


import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.connector.jdbc.JdbcInputFormat;


/**
 * @author bigdata_dpy
 */
public class StreamGetter {
    public static JdbcInputFormat getStreamFromJdbcUrl(String jdbcUrl, String username, String password, String sql, String driver, RowTypeInfo rowTypeInfo) {
        return JdbcInputFormat
                .buildJdbcInputFormat()
                .setDrivername(driver)
                .setDBUrl(jdbcUrl)
                .setUsername(username)
                // 用户名
                .setPassword(password)
                // 登录密码
                .setQuery(sql)
                // 需要执行的SQL语句
                .setRowTypeInfo(rowTypeInfo)
                .finish();
    }
}
