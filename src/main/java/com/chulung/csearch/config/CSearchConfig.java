package com.chulung.csearch.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by chulung on 2016/11/7.
 */
@Component
public class CSearchConfig implements InitializingBean {
    /**
     * 索引存储目录
     */
    @Value("${csearch.indexStorePath}")
    private String indexStorePath;

    @Value("${csearch.highlighter.opening}")
    private String highlighterOpening;

    @Value("${csearch.highlighter.closing}")
    private String highlighterClosing;

    private Formatter highLighterFormatter;

    public String getIndexStorePath() {
        return indexStorePath;
    }

    public void setIndexStorePath(String indexStorePath) {
        this.indexStorePath = indexStorePath;
    }

    public String getHighlighterOpening() {
        return highlighterOpening;
    }

    public void setHighlighterOpening(String highlighterOpening) {
        this.highlighterOpening = highlighterOpening;
    }

    public String getHighlighterClosing() {
        return highlighterClosing;
    }

    public void setHighlighterClosing(String highlighterClosing) {
        this.highlighterClosing = highlighterClosing;
    }

    public Formatter getHighLighterFormatter() {
        return highLighterFormatter;
    }

    public void setHighLighterFormatter(Formatter highLighterFormatter) {
        this.highLighterFormatter = highLighterFormatter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isBlank(indexStorePath)) throw  new IllegalArgumentException("indexStorePath can't be empty");
        if (StringUtils.isBlank(this.highlighterOpening)||StringUtils.isBlank(this.highlighterClosing)){
            this.highLighterFormatter=new SimpleHTMLFormatter();
        }else {
            this.highLighterFormatter=new SimpleHTMLFormatter(this.highlighterOpening,this.highlighterClosing);
        }
    }
}
