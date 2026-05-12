package com.acervo.acervoespirita.service.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class OverdueReportDTO {

    private Long loanId;

    private String copyCode;

    private String bookTitle;

    private String userName;

    private String phone;

    private String email;

    private LocalDate dueDate;

    private long overdueDays;

}