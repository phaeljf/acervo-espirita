package com.acervo.acervoespirita.service.report;

import com.acervo.acervoespirita.model.Loan;
import com.acervo.acervoespirita.model.LoanItem;
import com.acervo.acervoespirita.model.enums.LoanStatus;
import com.acervo.acervoespirita.repository.LoanRepository;
import com.acervo.acervoespirita.service.report.dto.LeastBorrowedBooksDTO;
import com.acervo.acervoespirita.service.report.dto.OverdueReportDTO;
import com.acervo.acervoespirita.service.report.dto.UserLoanHistoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final LoanRepository loanRepository;

    // Livros atrasados
    public List<OverdueReportDTO> findOverdueBooks() {

        List<Loan> overdueLoans =
                loanRepository.findByStatus(LoanStatus.OVERDUE);

        List<OverdueReportDTO> report = new ArrayList<>();

        for (Loan loan : overdueLoans) {

            for (LoanItem item : loan.getItems()) {

                if (item.isReturned()) {
                    continue;
                }

                long overdueDays =
                        ChronoUnit.DAYS.between(
                                loan.getDueDate(),
                                LocalDate.now()
                        );

                report.add(
                        new OverdueReportDTO(
                                loan.getId(),
                                item.getBookCopy().getCode(),
                                item.getBookCopy().getBook().getTitle(),
                                loan.getUser().getName(),
                                loan.getUser().getPhone(),
                                loan.getUser().getEmail(),
                                loan.getDueDate(),
                                overdueDays
                        )
                );
            }
        }

        return report;
    }

    // historico de empréstimo por usuário
    public List<UserLoanHistoryDTO> findUserLoanHistory(String search) {

        List<UserLoanHistoryDTO> report = new ArrayList<>();

        if (search == null || search.isBlank()) {
            return report;
        }

        List<Loan> loans = loanRepository.findByUser_NameContainingIgnoreCase(search);

        loans.sort(Comparator.comparing(Loan::getLoanDate).reversed());

        for (Loan loan : loans) {
            for (LoanItem item : loan.getItems()) {
                String status =
                        item.isReturned() ? "Devolvido" : "Em aberto";
                report.add(
                        new UserLoanHistoryDTO(
                                loan.getUser().getName(),
                                item.getBookCopy().getBook().getTitle(),
                                item.getBookCopy().getCode(),
                                loan.getLoanDate().atZone(ZoneId.systemDefault()).toLocalDate(),
                                item.getReturnDate(),
                                status
                        )
                );
            }
        }

        return report;
    }

    // Livrosm enos emprestados
    public List<LeastBorrowedBooksDTO> findLeastBorrowedBooks(
            LocalDate startDate,
            LocalDate endDate
    ) {

        List<Loan> loans = loanRepository.findAll();
        Map<String, Long> counter = new HashMap<>();
        Map<String, String> authors = new HashMap<>();

        for (Loan loan : loans) {

            LocalDate loanDate =loan.getLoanDate().atZone(ZoneId.systemDefault()).toLocalDate();

            if (loanDate.isBefore(startDate)
                    || loanDate.isAfter(endDate)) {
                continue;
            }

            for (LoanItem item : loan.getItems()) {
                String title =item.getBookCopy().getBook().getTitle();
                String author =item.getBookCopy().getBook().getAuthor();
                counter.put(title,counter.getOrDefault(title, 0L) + 1);
                authors.put(title,author);
            }
        }
        List<LeastBorrowedBooksDTO> report =
                new ArrayList<>();

        for (Map.Entry<String, Long> entry : counter.entrySet()) {
            report.add(new LeastBorrowedBooksDTO(entry.getKey(),authors.get(entry.getKey()),entry.getValue()));
        }
        report.sort(Comparator.comparing(LeastBorrowedBooksDTO::getTotalLoans));

        return report;
    }
}