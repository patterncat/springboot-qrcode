package cn.patterncat.qrcode.core.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by patterncat on 2017-10-27.
 */
public class ValidationUtil {

    public static void checkNotBlank(String value,String msg){
        if(StringUtils.isBlank(value)){
            throw new IllegalArgumentException(msg);
        }
    }

    public static void checkExpressionTrue(boolean value,String msg){
        if(!value){
            throw new IllegalArgumentException(msg);
        }
    }
}
