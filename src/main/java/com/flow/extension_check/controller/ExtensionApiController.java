package com.flow.extension_check.controller;

import com.flow.extension_check.constants.Constants;
import com.flow.extension_check.dto.Extension;
import com.flow.extension_check.dto.request.ExtensionRequestDto;
import com.flow.extension_check.dto.response.ExtensionResponse;
import com.flow.extension_check.enums.ExtensionType;
import com.flow.extension_check.enums.FixedExtension;
import com.flow.extension_check.service.ExtensionService;
import com.flow.extension_check.util.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api")
public class ExtensionApiController {
    private static final int MAX_EXT_LEN = 20;

    private final ExtensionService extensionService;
    @PostMapping("")
    public ResponseEntity<ExtensionResponse> setExt(@RequestBody ExtensionRequestDto requestDto) {
        if (Function.isNull(requestDto.getExt()).isEmpty()) {
            return ResponseEntity.badRequest().body(ExtensionResponse.fail("Extension이 비어있습니다."));
        }
        if (Function.isNull(requestDto.getExt()).length() > MAX_EXT_LEN) {
            return ResponseEntity.badRequest().body(ExtensionResponse.fail("Extension 길이가 20을 초과하지 말아야 합니다."));
        }
        ExtensionType type = ExtensionType.fromCode(requestDto.getCustomType());
        return switch (Objects.requireNonNull(type)) {
            case TOTAL -> handleTotalExtension(requestDto);
            case FIXED -> handleFixedExtension(requestDto);
            case CUSTOM -> handleCustomExtension(requestDto);
            default -> ResponseEntity.badRequest().body(ExtensionResponse.fail("존재하지 않는 타입입니다."));
        };
    }
    @DeleteMapping("")
    public ResponseEntity<ExtensionResponse> deleteExt(@RequestBody ExtensionRequestDto requestDto) {
        if (!extensionService.delete(requestDto.getExt())) {
            return ResponseEntity.internalServerError().body(ExtensionResponse.fail("확장자 삭제 실패"));
        }
        return ResponseEntity.ok(ExtensionResponse.success(null));
    }
    private ResponseEntity<ExtensionResponse> handleTotalExtension(ExtensionRequestDto requestDto) {
        if (!extensionService.isBlocked(requestDto.getExt())) {
            return ResponseEntity.ok(ExtensionResponse.success(null));
        }
        return ResponseEntity.badRequest().body(ExtensionResponse.fail("유효하지 않는 확장자입니다."));
    }
    private ResponseEntity<ExtensionResponse> handleFixedExtension(ExtensionRequestDto requestDto) {
        if (!extensionService.isBlocked(requestDto.getExt())) {
            Extension inserted = extensionService.insert(Extension.builder()
                    .ext(requestDto.getExt())
                    .build());
            if (inserted == null) {
                return ResponseEntity.internalServerError().body(ExtensionResponse.fail("확장자 추가 실패"));
            }
            return ResponseEntity.ok(ExtensionResponse.success(inserted));
        }
        if (!extensionService.delete(requestDto.getExt())) {
            return ResponseEntity.internalServerError().body(ExtensionResponse.fail("확장자 삭제 실패"));
        }
        return ResponseEntity.ok(ExtensionResponse.success(null));
    }
    private ResponseEntity<ExtensionResponse> handleCustomExtension(ExtensionRequestDto requestDto) {
        // TODO: Custom Extension이 200개가 넘는지 확인하기
        List<Extension> customExtensionList = extensionService.list(Extension.builder().build())
                .stream().filter(ext -> !FixedExtension.isFixedExt(ext.getExt())).toList();

        if (customExtensionList.size() >= Constants.MAX_CUSTOM_EXT_SIZE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExtensionResponse.fail("확장자 등록 개수는 최대 " + Constants.MAX_CUSTOM_EXT_SIZE + "개까지 가능합니다."));
        }
        // TODO: fixed extension에 이미 존재하는 extension인데, 해당 extension을 추가하려는지 확인하기
        if (FixedExtension.isFixedExt(requestDto.getExt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExtensionResponse.fail("고정 확장자를 입력하실 수 없습니다."));
        }
        // TODO: 중복된 CUSTOM EXTENSION이 있는지 확인하기
        if (extensionService.isBlocked(requestDto.getExt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExtensionResponse.fail("이미 중복된 커스텀 확장자가 존재합니다."));
        }
        Extension extension = extensionService.insert(Extension.builder()
                .ext(requestDto.getExt())
                .build());
        if (extension == null) {
            return ResponseEntity.internalServerError().body(ExtensionResponse.fail("커스텀 확장자 추가 실패"));
        }
        return ResponseEntity.ok(ExtensionResponse.success(extension));
    }
}
