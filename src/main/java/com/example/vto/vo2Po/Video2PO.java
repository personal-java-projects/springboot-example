package com.example.vto.vo2Po;

import com.example.pojo.Video;
import com.example.vto.vo.PublishVideo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface Video2PO {
    @Mappings({
            @Mapping(target = "publishTime", expression = "java(com.example.util.DateFormatUtil.parse(publishVideo.getPublishTime(), \"yyyy-MM-dd HH:mm:ss\"))")
    })
    Video publishVideo2Video(PublishVideo publishVideo);
}
