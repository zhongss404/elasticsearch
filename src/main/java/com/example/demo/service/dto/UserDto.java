package com.example.demo.service.dto;

import com.example.demo.domain.User;

/**
 * Created by dashuai on 2018/1/9.
 */
public class UserDto extends User {
    private String index;

    private String type;

    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
