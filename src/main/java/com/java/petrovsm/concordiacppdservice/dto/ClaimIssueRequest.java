package com.java.petrovsm.concordiacppdservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimIssueRequest {
    private List<String> candidateEmails;
    private String managerEmail;
    private Long templateId;
}
