package com.flow.extension_check.mapper;

import com.flow.extension_check.dto.AttacheFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AttacheFileMapper {
    List<AttacheFile> list();
    void insert(AttacheFile attacheFile);
    String getStoredFilename(String originalFilename);
}
