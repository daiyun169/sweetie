package com.dr.sweetie.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qewli12
 * 2018/12/19 17:14
 */
public class DataType {


    public static final Map<String, String> dataType = new HashMap<String, String>();

    static {
        dataType.put("tinyint", "Integer");
        dataType.put("smallint", "Integer");
        dataType.put("mediumint", "Integer");
        dataType.put("int", "Integer");
        dataType.put("integer", "Integer");
        dataType.put("bigint", "Long");
        dataType.put("float", "Float");
        dataType.put("double", "Double");
        dataType.put("decimal", "BigDecimal");
        dataType.put("bit", "Boolean");
        dataType.put("char", "String");
        dataType.put("varchar", "String");
        dataType.put("tinytext", "String");
        dataType.put("text", "String");
        dataType.put("mediumtext", "String");
        dataType.put("longtext", "String");
        dataType.put("date", "Date");
        dataType.put("datetime", "Date");
        dataType.put("timestamp", "Date");
    }

    /**
     * 数据库类型映射 java 类名
     *
     * @return
     */
    public static Map<String, String> getDataType() {
        return dataType;
    }

}
