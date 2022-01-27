package com.example.controller;

import com.example.pojo.Article;
import com.example.service.ArticleService;
import com.example.util.ResponseResult;
import com.example.vo.PublishArticle;
import com.example.voToPo.Article2PO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private Article2PO article2PO;

    @PostMapping("/publishArticle")
    public ResponseResult publishArticle (@RequestBody PublishArticle publishArticle) {
        Article article = article2PO.publishArticle2PO(publishArticle);

        articleService.publishArticle(article);

        return null;
    }
}
