package com.example.mapper;

import com.example.pojo.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper {
    int insertArticle(Article article);
}
