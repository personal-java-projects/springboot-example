package com.example.vto.vo2Po;

import com.example.pojo.Role;
import com.example.vto.vo.AddRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface Role2PO {
    @Mappings({
            @Mapping(source = "roleName", target = "roleName")
    })
    Role addRole2PO(AddRole addRole);
}