package com.acervo.acervoespirita.service.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardDTO {

    private long totalBooks;
    private long totalCopies;
    private long availableCopies;
    private long loanedCopies;
    private long activeLoans;
    private long overdueLoans;
    private long totalUsers;
    private long totalAdmins;
    private long totalStaff;
    private long totalFrequentUsers;

}