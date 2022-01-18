package com.example.voToPo;

import com.example.pojo.FilePO;
import com.example.vo.ComposeFile;
import com.example.vo.MultipartUpload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface FileVoToPo {
    @Mappings({
            @Mapping(source = "chunkCount", target = "chunkCount"),
            @Mapping(source = "fileMd5", target = "fileMd5")
    })
    FilePO multipart(MultipartUpload multipartUpload);

    FilePO composeFile(ComposeFile composeFile);
}
