package com.flow.extension_check.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUtil {
    public static String createStoreFilename(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }
    public static String extractExt(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
    public static String saveFile(String uploadDir, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String storeFilename = createStoreFilename(file.getOriginalFilename());
        Path filePath = uploadPath.resolve(storeFilename);

        file.transferTo(filePath.toFile());
        return storeFilename;
    }
}
