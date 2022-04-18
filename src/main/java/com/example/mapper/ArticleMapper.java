package com.example.mapper;

import com.example.pojo.Article;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ArticleMapper {
    int insertArticle(Article article);
}
