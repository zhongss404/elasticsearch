package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by dashuai on 2018/1/9.
 */
@ConfigurationProperties(value = "es")
@Component
public class ConfigProperties {
    private String master;

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }
}
