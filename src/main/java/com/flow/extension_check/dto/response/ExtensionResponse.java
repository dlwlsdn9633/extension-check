package com.flow.extension_check.dto.response;

import com.flow.extension_check.dto.Extension;
import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class ExtensionResponse {
    private boolean success;
    private String message;
    private Extension extension;
    public static ExtensionResponse success(Extension data) {
        return new ExtensionResponse(true, "OK", data);
    }
    public static ExtensionResponse fail(String message) {
        return new ExtensionResponse(false, message, null);
    }
}
