package com.flow.extension_check.service;

import com.flow.extension_check.dto.AttacheFile;
import com.flow.extension_check.mapper.AttacheFileMapper;
import com.flow.extension_check.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    @Value("${file.upload-dir}")
    private String uploadPath;

    private final ExtensionService extensionService;
    private final AttacheFileMapper attacheFileMapper;
    @Transactional
    public Map<String, Integer> uploadFiles(MultipartFile[] files) {
        Map<String, Integer> result = new HashMap<>();
        result.putIfAbsent("success", 0);
        result.putIfAbsent("fail", 0);

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String ext = FileUtil.extractExt(originalFilename);
            if (extensionService.isBlocked(ext)) {
                result.put("fail", result.get("fail") + 1);
                continue;
            }
            try {
                String storedFilename = FileUtil.saveFile(uploadPath, file);
                attacheFileMapper.insert(AttacheFile.builder()
                                .originalFilename(originalFilename)
                                .storedFilename(storedFilename).build());
                result.put("success", result.get("success") + 1);
            } catch (IOException e) {
                log.error(e.getMessage());
                result.put("fail", result.get("fail") + 1);
            }
        }
        return result;
    }
    public Resource downloadFile(String filename) {
        try {
            String storedFilename = attacheFileMapper.getStoredFilename(filename);
            Path filePath = Paths.get(uploadPath).resolve(storedFilename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return null;
            }
            return resource;
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return null;
        }
    }
    public List<AttacheFile> list() {
        return attacheFileMapper.list();
    }
}
