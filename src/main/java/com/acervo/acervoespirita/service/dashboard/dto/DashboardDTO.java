package com.acervo.acervoespirita.service.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardDTO {

    // ACERVO
    private long totalBooks;
    private long totalCopies;
    private long availableCopies;
    private long loanedCopies;

    // EMPRÉSTIMOS
    private long activeLoans;
    private long overdueLoans;

    // USUÁRIOS
    private long totalUsers;
    private long totalAdmins;
    private long totalStaff;
    private long totalFrequentUsers;

}