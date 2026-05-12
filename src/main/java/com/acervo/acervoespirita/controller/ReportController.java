package com.acervo.acervoespirita.controller;

import com.acervo.acervoespirita.model.User;
import com.acervo.acervoespirita.service.SessionService;
import com.acervo.acervoespirita.service.report.ReportService;
import com.acervo.acervoespirita.service.report.dto.OverdueReportDTO;
import com.acervo.acervoespirita.service.report.dto.UserLoanHistoryDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.time.LocalDate;
import com.acervo.acervoespirita.service.report.dto.LeastBorrowedBooksDTO;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReportController {

    private final SessionService sessionService;
    private final ReportService reportService;


    @GetMapping("/reports")
    public String reportsPage(HttpSession session,Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser =sessionService.getLoggedUser(session);
        model.addAttribute("loggedUser",loggedUser);

        return "reports/index";
    }

    @GetMapping("/reports/overdue")
    public String overdueReport(HttpSession session,Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);
        List<OverdueReportDTO> overdueBooks = reportService.findOverdueBooks();
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("overdueBooks",overdueBooks);

        return "reports/overdue";
    }

    @GetMapping("/reports/overdue/pdf")
    public void overdueReportPdf(HttpSession session,HttpServletResponse response) throws Exception {

        if (!sessionService.isLogged(session)) {
            response.sendRedirect("/");
            return;
        }

        List<OverdueReportDTO> overdueBooks = reportService.findOverdueBooks();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=livros-atrasados.pdf");
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document,response.getOutputStream());

        document.open();

        //Titulo do PDF
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD,18);
        Paragraph title = new Paragraph("RELATÓRIO DE LIVROS ATRASADOS",titleFont);
        title.setSpacingAfter(15);
        document.add(title);

        // Data
        Paragraph date = new Paragraph("Gerado em: " + LocalDate.now());
        date.setSpacingAfter(15);
        document.add(date);

        // Sem dados
        if (overdueBooks.isEmpty()) {
            document.add(new Paragraph("Nenhum livro atrasado encontrado."));
            document.close();
            return;
        }

        // Tabela
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.addCell("Código");
        table.addCell("Livro");
        table.addCell("Usuário");
        table.addCell("Telefone");
        table.addCell("Email");
        table.addCell("Vencimento");
        table.addCell("Dias");

        for (OverdueReportDTO item : overdueBooks) {
            table.addCell(item.getCopyCode());
            table.addCell(item.getBookTitle());
            table.addCell(item.getUserName());
            table.addCell(item.getPhone());
            table.addCell(item.getEmail());
            table.addCell(String.valueOf(item.getDueDate()));
            table.addCell(String.valueOf(item.getOverdueDays()));
        }

        document.add(table);
        document.close();
    }



    @GetMapping("/reports/user-history")
    public String userHistoryReport(@RequestParam(required = false) String search,HttpSession session,Model model) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);
        List<UserLoanHistoryDTO> history = reportService.findUserLoanHistory(search);

        model.addAttribute("loggedUser",loggedUser);
        model.addAttribute("history",history);
        model.addAttribute("search",search);

        return "reports/user-history";
    }

    @GetMapping("/reports/least-borrowed")
    public String leastBorrowedReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session,
            Model model
    ) {

        if (!sessionService.isLogged(session)) {
            return "redirect:/";
        }

        User loggedUser = sessionService.getLoggedUser(session);
        List<LeastBorrowedBooksDTO> report = new java.util.ArrayList<>();

        if (startDate != null && endDate != null && !startDate.isBlank() && !endDate.isBlank()) {
            report = reportService.findLeastBorrowedBooks(LocalDate.parse(startDate),LocalDate.parse(endDate));
        }

        model.addAttribute("loggedUser",loggedUser);
        model.addAttribute("report",report);
        model.addAttribute("startDate",startDate);
        model.addAttribute("endDate",endDate);

        return "reports/least-borrowed";
    }

    @GetMapping("/reports/least-borrowed/pdf")
    public void leastBorrowedPdf(@RequestParam String startDate,@RequestParam String endDate,HttpSession session,HttpServletResponse response) throws Exception {

        if (!sessionService.isLogged(session)) {
            response.sendRedirect("/");
            return;
        }

        List<LeastBorrowedBooksDTO> report = reportService.findLeastBorrowedBooks(LocalDate.parse(startDate),LocalDate.parse(endDate));
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=livros-menos-emprestados.pdf");
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document,response.getOutputStream());

        document.open();

        // Titulo

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD,18);
        Paragraph title = new Paragraph("Relatório de Livros Menos Emprestados",titleFont);
        title.setSpacingAfter(10);
        document.add(title);

        // Período
        document.add(new Paragraph("Período: "
                                + startDate
                                + " até "
                                + endDate
                                )
        );

        document.add(new Paragraph(" "));

        // Sem dados

        if (report.isEmpty()) {
            document.add(new Paragraph("Nenhum resultado encontrado."));
            document.close();

            return;
        }

        // Tabela

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        table.addCell("Livro");
        table.addCell("Autor");
        table.addCell("Total");

        for (LeastBorrowedBooksDTO item : report) {
            table.addCell(item.getTitle());
            table.addCell(item.getAuthor());
            table.addCell(String.valueOf(item.getTotalLoans()));
        }

        document.add(table);
        document.close();
    }
}