package com.flow.extension_check.service;

import com.flow.extension_check.dto.Extension;
import com.flow.extension_check.mapper.ExtensionMapper;
import com.flow.extension_check.util.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExtensionService {
    private final ExtensionMapper extensionMapper;
    public boolean isBlocked(String extension) {
        if (Function.isNull(extension).isEmpty()) {
            return true;
        }
        List<Extension> blockedList = extensionMapper.list(Extension.builder().build());
        return blockedList.stream()
                .anyMatch(blockedExtension -> blockedExtension.getExt().equalsIgnoreCase(extension));
    }
    @Transactional
    public Extension insert(Extension extension) {
        int ret = extensionMapper.insert(extension);
        if (ret <= 0) {
            return null;
        }
        extension.setRegisterDate(LocalDateTime.now());
        return extension;
    }
    @Transactional
    public boolean delete(String ext) {
        return extensionMapper.delete(ext) > 0;
    }

    public List<Extension> list(Extension extension) {
        return extensionMapper.list(extension);
    }
}
