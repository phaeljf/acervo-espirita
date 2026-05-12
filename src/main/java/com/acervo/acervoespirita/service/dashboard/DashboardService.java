package com.acervo.acervoespirita.service.dashboard;

import com.acervo.acervoespirita.model.enums.BookCopyStatus;
import com.acervo.acervoespirita.model.enums.LoanStatus;
import com.acervo.acervoespirita.model.enums.UserRole;
import com.acervo.acervoespirita.repository.BookCopyRepository;
import com.acervo.acervoespirita.repository.BookRepository;
import com.acervo.acervoespirita.repository.LoanRepository;
import com.acervo.acervoespirita.repository.UserRepository;
import com.acervo.acervoespirita.service.dashboard.dto.DashboardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    public DashboardDTO getDashboardData() {

        // Acervo
        long totalBooks = bookRepository.count();
        long totalCopies = bookCopyRepository.count();
        long availableCopies = bookCopyRepository.findByStatus(BookCopyStatus.AVAILABLE).size();
        long loanedCopies = bookCopyRepository.findByStatus(BookCopyStatus.LOANED).size();

        // Empréstimo
        long activeLoans = loanRepository.findByStatus(LoanStatus.OPEN).size();
        long overdueLoans = loanRepository.findByStatus(LoanStatus.OVERDUE).size();

        // Usuário
        long totalUsers = userRepository.count();
        long totalAdmins = userRepository.findByRole(UserRole.ADMIN).size();
        long totalStaff = userRepository.findByRole(UserRole.STAFF).size();
        long totalFrequentUsers = userRepository.findByRole(UserRole.USER).size();

        return new DashboardDTO(
                totalBooks,
                totalCopies,
                availableCopies,
                loanedCopies,
                activeLoans,
                overdueLoans,
                totalUsers,
                totalAdmins,
                totalStaff,
                totalFrequentUsers
        );
    }
}