package com.example.demo.service.dto;

/**
 * Created by dashuai on 2018/1/9.
 */
public class SearchDto {
    private String key;  //搜索的字段

    private String value;  //搜索的值

    private String index;  //索引

    private String type;   //类型

    private String insertType;  //插入类型（普通插入还是bulk插入）

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInsertType() {
        return insertType;
    }

    public void setInsertType(String insertType) {
        this.insertType = insertType;
    }
}
