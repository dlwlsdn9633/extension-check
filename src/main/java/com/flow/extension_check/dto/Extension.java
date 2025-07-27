package com.flow.extension_check.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Extension {
    private int id;
    private String ext;
    private LocalDateTime registerDate;
}
