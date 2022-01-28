package com.example.service.impl;

import com.example.mapper.ArticleMapper;
import com.example.pojo.Article;
import com.example.service.ArticleService;
import org.apache.poi.poifs.crypt.dsig.services.TimeStampService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service("articleService")
public class ArticleServiceImpl implements ArticleService {

    private Logger logger = (Logger) LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired
    private ArticleMapper articleMapper;

    private java.util.Date currentDate;

    @Override
    public int publishArticle(Article article) {
        currentDate  = new java.util.Date();
        article.setUpdateTime(new Timestamp(currentDate.getTime()));
        int id = articleMapper.insertArticle(article);

        return id;
    }
}
