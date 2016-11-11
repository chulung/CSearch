package com.chulung.csearch.core;

import com.chulung.csearch.config.CSearchConfig;
import com.hankcs.lucene.HanLPAnalyzer;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static com.chulung.csearch.core.CSearchDocument.CONTEXT;
import static com.chulung.csearch.core.CSearchDocument.ID;
import static com.chulung.csearch.core.CSearchDocument.TITLE;

/**
 * Created by chulung on 2016/11/7.
 */

@Component
public class CSearchIndex implements InitializingBean {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private Directory fsDirectory;
    @Autowired
    private CSearchConfig cSearchConfig;

    /**
     * 使用HanLP中文分词
     */
    private Analyzer analyzer = new HanLPAnalyzer();
    private DirectoryReader reader;
    private IndexWriter indexWriter;

    /**
     * Creates a new index or overwrites an existing one.
     *
     * @param doc
     *
     */
    public boolean createIndex(CSearchDocument doc) {
        IndexWriter writer = null;
        try {
            writer = this.getIndexWriter();
            logger.info("tryDelete id={}",doc.getId());
            writer.deleteDocuments(new Term(ID,doc.getId()));
            Document document = new Document();
            document.add(new StringField(ID, doc.getId(), Field.Store.YES));
            document.add(new TextField(TITLE, doc.getTitle(), Field.Store.YES));
            document.add(new TextField(CONTEXT, doc.getContext(), Field.Store.YES));
            writer.addDocument(document);
            logger.info("createIndex id={}",doc.getId());
        } catch (IOException e) {
            try {
                writer.rollback();
            } catch (IOException e1) {
            }
            return false;
        } finally {
            if (writer != null)
                try {
                    writer.commit();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
        return true;
    }

    /**
     * 清除所有索引
     */
    public void clearAll() {
        try {

            IndexWriter writer = this.getIndexWriter();
            writer.deleteAll();
            writer.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<CSearchDocument> search(String queryStr) throws ParseException, IOException, InvalidTokenOffsetsException {
        if (StringUtils.isBlank(queryStr)) {
            return Collections.EMPTY_LIST;
        }
//        //设置标题中关键字权重更高
//        Map<String, Float> boosts = new HashMap<String, Float>();
//        boosts.put(TITLE, 1.5f);
//        boosts.put(CONTEXT, 1.0f);
        //搜索标题和内容两个字段
        String[] fields = {TITLE, CONTEXT};
        QueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);
        Query query = queryParser.parse(queryStr);
        IndexSearcher searcher = new IndexSearcher(getReader());
        TopDocs topDocs = searcher.search(query, 10);
        //设置高亮格式
        Highlighter highlighter = new Highlighter(this.cSearchConfig.getHighLighterFormatter(), new QueryScorer(query));
        List<CSearchDocument> result = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            //这里的.replaceAll("\\s*", "")是必须的，\r\n这样的空白字符会导致高亮标签错位
            String context = highlighter.getBestFragment(analyzer, CONTEXT, doc.get(CONTEXT).replaceAll("\\s*", ""));
            String title = highlighter.getBestFragment(analyzer, TITLE, doc.get(TITLE).replaceAll("\\s*", ""));
            result.add(new CSearchDocument(doc.get(CSearchDocument.ID), title, context));
        }
        return result;
    }

    /**
     * reader 返回当前reader 如果文档有更新则新打开一个
     * @return
     * @throws IOException
     */
    private DirectoryReader getReader() throws IOException {
        if (reader==null){
            this.reader=DirectoryReader.open(fsDirectory);
        }
        DirectoryReader newReader = DirectoryReader.openIfChanged((DirectoryReader)reader,  getIndexWriter(), false);//reader.reopen();      // 读入新增加的增量索引内容，满足实时索引需求
        if (newReader != null) {
            reader.close();
            reader = newReader;
        }
        return reader;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        fsDirectory = FSDirectory.open(Paths.get(this.cSearchConfig.getIndexStorePath()));
        indexWriter = new IndexWriter(fsDirectory, new IndexWriterConfig(analyzer));
    }

    private IndexWriter getIndexWriter() throws IOException {
        return indexWriter;
    }
}
