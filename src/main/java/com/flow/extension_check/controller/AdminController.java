package com.flow.extension_check.controller;


import com.flow.extension_check.constants.Constants;
import com.flow.extension_check.dto.Extension;
import com.flow.extension_check.enums.FixedExtension;
import com.flow.extension_check.service.ExtensionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final ExtensionService extensionService;
    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("fixedExtensions", FixedExtension.values());
        List<Extension> blockedExtensionList = extensionService.list(Extension.builder().build());
        List<String> fixedBlockedList = blockedExtensionList.stream()
                .map(Extension::getExt)
                .filter(FixedExtension::isFixedExt)
                .toList();
        List<String> customBlockedList = blockedExtensionList.stream()
                .map(Extension::getExt)
                .filter(ext -> !FixedExtension.isFixedExt(ext))
                .toList();
        model.addAttribute("MAX_CUSTOM_EXT_SIZE", Constants.MAX_CUSTOM_EXT_SIZE);
        model.addAttribute("fixedBlockedList", fixedBlockedList);
        model.addAttribute("customBlockedList", customBlockedList);
        return "admin/index";
    }
    @GetMapping("/fragment/extension-btn")
    public String getExtensionBtn(@RequestParam("ext") String ext, Model model) {
        model.addAttribute("ext", ext);
        return "fragments/extension-btn :: extensionBtnFragment";
    }
}
