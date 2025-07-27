package com.flow.extension_check.enums;

public enum ExtensionType {
    TOTAL(0),
    FIXED(1),
    CUSTOM(2);
    private final int code;
    ExtensionType(int code) {
        this.code = code;
    }
    public int getCode() {
        return this.code;
    }
    public static ExtensionType fromCode(int code) {
        for (ExtensionType ele : values()) {
            if (ele.getCode() == code) {
                return ele;
            }
        }
        return null;
    }
}
