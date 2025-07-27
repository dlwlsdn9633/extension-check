package com.flow.extension_check.controller;

import com.flow.extension_check.service.FileService;
import com.flow.extension_check.util.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final FileService fileService;
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("list", fileService.list());
        return "index";
    }
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filename) throws FileNotFoundException {
        Resource resource = fileService.downloadFile(filename);
        if (resource == null) {
            throw new FileNotFoundException();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    @PostMapping("/upload")
    public String upload(
            @RequestParam("filename")MultipartFile[] files,
            RedirectAttributes redirectAttributes
    ) {
        if (files == null || files.length == 0) {
            redirectAttributes.addFlashAttribute("message", "파일을 업로드 해주세요.");
            return "redirect:/";
        }
        for (MultipartFile file : files) {
            if (Function.isNull(file.getOriginalFilename()).isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "빈 파일이 존재합니다.");
                return "redirect:/";
            }
        }

        Map<String, Integer> result = fileService.uploadFiles(files);
        redirectAttributes.addFlashAttribute("success", result.get("success"));
        redirectAttributes.addFlashAttribute("fail", result.get("fail"));
        return "redirect:/";
    }
}
