package com.flow.extension_check.enums;

public enum FixedExtension {
    BAT("bat"),
    CMD("cmd"),
    COM("com"),
    EXE("exe"),
    SCR("scr"),
    JS("js");
    private final String label;
    FixedExtension(String label) {
        this.label = label;
    }
    public String getLabel() {
        return this.label;
    }
    public static boolean isFixedExt(String ext) {
        for (FixedExtension ele : values()) {
            if (ele.getLabel().equals(ext)) {
                return true;
            }
        }
        return false;
    }
}
