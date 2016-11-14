package com.chulung.csearch.core;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chulung on 2016/11/10.
 */
public class CSearchDocument {
    public static final String ID = "id";
    public static  final  String TITLE="title";
    public  static  final  String CONTEXT="context";
    /**
     * 唯一ID，创建前会根据id删除已有的
     */
    private String id;
    /**
     * 文档标题
     */
    private String title;
    /**
     * 文档内容
     */
    private String context;

    public CSearchDocument(String id,String title,String context){
        if (id==null) throw new IllegalArgumentException("id can't be null!");
        if (title==null) throw new IllegalArgumentException("title can't be null!");
        if (context==null) throw new IllegalArgumentException("context can't be null!");
        this.id=id;
        this.title=title;
        this.context=context;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CSearchDocument{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}
