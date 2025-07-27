package com.flow.extension_check.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AttacheFile {
    private int id;
    private String originalFilename;
    private String storedFilename;
    private LocalDateTime registerDate;
}
