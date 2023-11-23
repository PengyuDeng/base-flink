package cn.sct.baseflink.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;


/**
 * @author daijianghong
 */
public class JsonUtil {

    /**
     * 判断是否是json
     */
    public static boolean isJsonValidate(String json){
        try {
            JSON.parse(json);
            return true;
        }catch (JSONException e){
            return false;
        }
    }
}
