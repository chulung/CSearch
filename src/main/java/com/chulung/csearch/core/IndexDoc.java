package com.chulung.csearch.core;

import com.chulung.csearch.config.CSearchConfig;
import com.hankcs.lucene.HanLPAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by chulung on 2016/11/7.
 */

@Component
public class IndexDoc {

    @Autowired
    private CSearchConfig cSearchConfig;

    public void init() throws IOException {
        Analyzer analyzer=new HanLPAnalyzer();
        IndexWriterConfig indexWriterConfig=new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        FSDirectory fsDirectory=FSDirectory.open(Paths.get(this.cSearchConfig.getIndexStorePath()));
        IndexWriter writer=new IndexWriter(fsDirectory,indexWriterConfig);
        Document doc=new Document();
        doc.add(new StringField("id","1", Field.Store.YES));
        doc.add(new TextField("text","java8出来后，特意了解它的新特性lambda表达式，由此头一次听说了函数式编程这个词，听起来挺高深的样子。也曾各种搜索去了解它的来龙去脉。甚至买了一本书《函数式编程思想》，并在部门内进行了一次讨论。此时，首先需要回答的问题便是,函数式编程：那是什么东西？", Field.Store.NO));
        writer.addDocument(doc);
        writer.close();
    }
}
