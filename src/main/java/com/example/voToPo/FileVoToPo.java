package com.example.voToPo;

import com.example.pojo.FilePO;
import com.example.vo.ComposeFile;
import com.example.vo.MultipartUpload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileVoToPo {
    FilePO multipart(MultipartUpload multipartUpload);

    FilePO composeFile(ComposeFile composeFile);
}
