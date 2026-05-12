package com.acervo.acervoespirita.service.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LeastBorrowedBooksDTO {

    private String title;

    private String author;

    private long totalLoans;

}