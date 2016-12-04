package com.chulung.csearch.core;

import com.chulung.csearch.config.CSearchConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by chulung on 2016/11/10.
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:csearch.xml")
public class CSearchTest {
    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CSearchImpl cSearchIndex;

    @Autowired
    private CSearchConfig cSearchConfig;

    @Test
    public void testCSearch() throws Exception {
        //清除
        this.cSearchIndex.clearAll();
        //剔除html标签
        String context = TestUtil.readToString("src/test/resources/test.html").replaceAll("</?[^<]+>", "");
        String id = "1";
        String title = "什么是"+"函数"+"式编程";
        String titleHighLight="什么是"+cSearchConfig.getHighlighterOpening()+"函数"+cSearchConfig.getHighlighterClosing()+"式编程";
        CSearchDocument doc = new CSearchDocument(id, title, context);
        //创建索引
        cSearchIndex.createIndex(doc);
        cSearchIndex.createIndex(doc);
        String key = "函数";
        //搜索
        List<CSearchDocument> result=cSearchIndex.search(key);
        assertEquals(1,result.size());
        result.forEach(cSearchDocument -> {
            assertEquals(id,cSearchDocument.getId());
            logger.info("搜索结果：{}",cSearchDocument);
            assertTrue(cSearchDocument.getTitle()+"\n"+titleHighLight,cSearchDocument.getTitle().equals(titleHighLight));
            //包含高亮的key
            String highLightKey = cSearchConfig.getHighlighterOpening() + key + cSearchConfig.getHighlighterClosing();
            assertTrue(cSearchDocument.getContent().contains(highLightKey));
        });

        //修改id 当做新文档
        doc.setId("2");
        doc.setTitle(doc.getTitle());
        cSearchIndex.createIndex(doc);
        //此时结果为2篇
        result= cSearchIndex.search(key);
        result.forEach(System.out::println);
        assertEquals(2, result.size());
        //清除
        this.cSearchIndex.clearAll();
        //再次查询结果为空
        List<CSearchDocument> list = cSearchIndex.search(key);
        assertTrue(list.isEmpty());
    }



}