package com.example.mapper;

import com.example.pojo.FilePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {
    FilePO selectFileByMD5(String md5);

    void insertFile(FilePO filePO);
}
