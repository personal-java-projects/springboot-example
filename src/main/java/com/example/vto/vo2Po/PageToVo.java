package com.example.vto.vo2Po;

import com.example.vto.dto.PageDto;
import com.example.vto.vo.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PageToVo {
    @Mappings({
            @Mapping(source = "pageIndex", target = "pageIndex"),
            @Mapping(source = "pageSize", target = "pageSize")
    })
    PageDto pageDto(Page page);
}
