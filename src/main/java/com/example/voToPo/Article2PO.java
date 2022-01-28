package com.example.voToPo;

import com.example.pojo.Article;
import com.example.vo.PublishArticle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface Article2PO {
    @Mappings({
            @Mapping(target = "publishTime", expression = "java(com.example.util.DateFormatUtil.parse(publishArticle.getPublishTime(), \"yyyy-MM-dd HH:mm:ss\"))")
    })
    Article publishArticle2PO(PublishArticle publishArticle);
}
