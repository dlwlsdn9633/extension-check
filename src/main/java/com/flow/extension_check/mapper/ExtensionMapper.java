package com.flow.extension_check.mapper;

import com.flow.extension_check.dto.Extension;
import com.flow.extension_check.dto.request.ExtensionRequestDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface ExtensionMapper {
    List<Extension> list(Extension extension);
    int insert(Extension ext);
    int delete(String ext);
    int count(String ext);
}
