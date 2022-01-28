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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private Article2PO article2PO;

    private Map<String, Object> resultMap = new HashMap<>();

    @PostMapping("/publishArticle")
    public ResponseResult publishArticle (@RequestBody PublishArticle publishArticle) {
        Article article = article2PO.publishArticle2PO(publishArticle);

        int articleId = articleService.publishArticle(article);

        resultMap.put("articleId", articleId);

        return ResponseResult.ok().data(resultMap);
    }
}
