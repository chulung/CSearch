package com.chulung.csearch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by chulung on 2016/11/7.
 */
@Component
public class CSearchConfig {
    @Value("${csearch.indexStorePath}")
    private String indexStorePath;

    public String getIndexStorePath() {
        return indexStorePath;
    }

    public void setIndexStorePath(String indexStorePath) {
        this.indexStorePath = indexStorePath;
    }
}
