package com.example.voToPo;

import com.example.dto.PageDto;
import com.example.vo.Page;
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
