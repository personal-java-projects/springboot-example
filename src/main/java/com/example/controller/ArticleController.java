package com.example.controller;

import com.example.pojo.Article;
import com.example.service.ArticleService;
import com.example.util.ResponseResult;
import com.example.vto.vo.PublishArticle;
import com.example.vto.vo2Po.Article2PO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "文章")
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private Article2PO article2PO;

    private Map<String, Object> resultMap = new HashMap<>();

    @ApiOperation(value = "发布文章")
    @PostMapping("/publishArticle")
    public ResponseResult publishArticle (@RequestBody PublishArticle publishArticle) {
        Article article = article2PO.publishArticle2PO(publishArticle);

        int articleId = articleService.publishArticle(article);

        resultMap.put("articleId", articleId);

        return ResponseResult.ok().data(resultMap);
    }
}
