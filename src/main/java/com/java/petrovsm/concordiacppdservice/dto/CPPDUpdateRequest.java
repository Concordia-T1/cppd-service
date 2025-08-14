package com.java.petrovsm.concordiacppdservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CPPDUpdateRequest {
    private String version;
    private String content;
    private boolean active;
}
