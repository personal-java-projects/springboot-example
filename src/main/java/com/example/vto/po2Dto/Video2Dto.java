package com.example.vto.po2Dto;

import com.example.pojo.Video;
import com.example.vto.dto.VideoDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface Video2Dto {
    VideoDto video2VideoDto(Video video);
}
