package com.acervo.acervoespirita.service.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserLoanHistoryDTO {

    private String userName;
    private String bookTitle;
    private String copyCode;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private String status;

}