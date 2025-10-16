package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.model.BookStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service responsible for generating reports.
 * Follows Single Responsibility Principle - handles only report generation.
 */
@Service
public class ReportService {

    private final BookManagementService bookManagementService;
    private final MemberService memberService;

    public ReportService(BookManagementService bookManagementService, MemberService memberService) {
        this.bookManagementService = bookManagementService;
        this.memberService = memberService;
    }

    /**
     * Generate overdue books report
     */
    public String generateOverdueBooksReport() {
        List<Book> overdueBooks = bookManagementService.findOverdueBooks();
        
        StringBuilder report = new StringBuilder("OVERDUE BOOKS REPORT\n");
        report.append("====================\n");
        
        if (overdueBooks.isEmpty()) {
            report.append("No overdue books found.\n");
        } else {
            for (Book book : overdueBooks) {
                report.append(String.format("%s by %s - Due: %s - Checked out by: %s\n",
                        book.getTitle(), book.getAuthor(), book.getDueDate(), book.getCheckedOutBy()));
            }
        }
        
        report.append(String.format("\nTotal overdue books: %d\n", overdueBooks.size()));
        return report.toString();
    }

    /**
     * Generate available books report
     */
    public String generateAvailableBooksReport() {
        long availableCount = bookManagementService.countByStatus(BookStatus.AVAILABLE);
        long totalBooks = bookManagementService.findAll().size();
        
        StringBuilder report = new StringBuilder("AVAILABLE BOOKS REPORT\n");
        report.append("=====================\n");
        report.append(String.format("Available books: %d\n", availableCount));
        report.append(String.format("Total books: %d\n", totalBooks));
        report.append(String.format("Checkout rate: %.1f%%\n", 
                totalBooks > 0 ? ((double)(totalBooks - availableCount) / totalBooks) * 100 : 0.0));
        
        return report.toString();
    }

    /**
     * Generate members report
     */
    public String generateMembersReport() {
        long totalMembers = memberService.count();
        long membersWithBooks = memberService.findMembersWithCheckedOutBooks().size();
        
        StringBuilder report = new StringBuilder("MEMBERS REPORT\n");
        report.append("==============\n");
        report.append(String.format("Total members: %d\n", totalMembers));
        report.append(String.format("Members with checked out books: %d\n", membersWithBooks));
        report.append(String.format("Active member rate: %.1f%%\n", 
                totalMembers > 0 ? ((double)membersWithBooks / totalMembers) * 100 : 0.0));
        
        return report.toString();
    }

    /**
     * Generate library summary report
     */
    public String generateLibrarySummaryReport() {
        long totalBooks = bookManagementService.findAll().size();
        long availableBooks = bookManagementService.countByStatus(BookStatus.AVAILABLE);
        long checkedOutBooks = bookManagementService.countByStatus(BookStatus.CHECKED_OUT);
        long overdueBooks = bookManagementService.findOverdueBooks().size();
        long totalMembers = memberService.count();
        
        StringBuilder report = new StringBuilder("LIBRARY SUMMARY REPORT\n");
        report.append("======================\n");
        report.append(String.format("Total books: %d\n", totalBooks));
        report.append(String.format("Available books: %d\n", availableBooks));
        report.append(String.format("Checked out books: %d\n", checkedOutBooks));
        report.append(String.format("Overdue books: %d\n", overdueBooks));
        report.append(String.format("Total members: %d\n", totalMembers));
        report.append(String.format("Report generated on: %s\n", LocalDate.now()));
        
        return report.toString();
    }

    /**
     * Generate report by type
     */
    public String generateReport(String reportType) {
        return switch (reportType.toLowerCase()) {
            case "overdue" -> generateOverdueBooksReport();
            case "available" -> generateAvailableBooksReport();
            case "members" -> generateMembersReport();
            case "summary" -> generateLibrarySummaryReport();
            default -> throw new IllegalArgumentException("Invalid report type: " + reportType);
        };
    }
}
