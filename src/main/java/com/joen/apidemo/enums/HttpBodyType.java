package com.joen.apidemo.enums;

/**
 * @Description
 * @Author Joen
 * @Date 2021-01-04 10:47:53
 */
public enum HttpBodyType {
    NONE("不传参数",0),
    FORM_DATA("表单传参",1),
    @Deprecated
    X_WWW_FORM_URLENCODED("",2),
    RAW("字节流传参",3),
    @Deprecated
    BINARY("",4),
    @Deprecated
    GRAph_QL("",5);

    private String details;
    private int code;

    HttpBodyType(String details, int code) {
        this.details = details;
        this.code = code;
    }

    public String getDetails() {
        return details;
    }

    public int getCode() {
        return code;
    }
}
