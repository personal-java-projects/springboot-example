package com.example.mapper;

import com.example.pojo.FilePO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FileMapper {
    FilePO selectFileByMD5(String md5);

    FilePO selectFileById(int id);

    void insertFile(FilePO filePO);

    void updateFile(FilePO filePO);
}
