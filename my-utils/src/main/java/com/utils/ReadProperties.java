package com.utils;




import java.io.InputStream;
import java.util.Properties;

/**
 * @Author: menfeng
 * @Date: 2019/12/31 9:18
 * @Version 1.0
 */

public class ReadProperties {

    private static Properties property = new Properties();


    public static ReadProperties ReadProperties(String path){
        try{
            //InputStream in = SysConfigUtil.class.getResourceAsStream("classpath:/"+path);
            InputStream in = ReadProperties.class.getResourceAsStream("/"+path);
            property.load(in);
            return new ReadProperties();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getString(String key) {
        return property.getProperty(key);
    }

    public Integer getInt(String key) {
        String value = getString(key);
        return null == value ? null : Integer.valueOf(value);
    }

    public Boolean getBoolean(String key) {
        String value = getString(key);
        return null == value ? null : Boolean.valueOf(value);
    }

    public Long getLong(String key){
        String value= getString(key);
        return  null == value ? null : Long.valueOf(value);
    }
}
