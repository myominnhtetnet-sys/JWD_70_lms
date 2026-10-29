
package com.Learning_Managnment_System.JWD_70_lms.Service;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.Learning_Managnment_System.JWD_70_lms.Repository.ReportRespority;
import com.Learning_Managnment_System.JWD_70_lms.model.ReportBean;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ReportService {

    private static final Path UPLOAD_DIR =
            Paths.get("uploads", "reports")
                    .toAbsolutePath()
                    .normalize();

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("pdf", "csv", "xlsx", "xls", "doc", "docx");

    private static final Set<String> ALLOWED_CATEGORIES =
            Set.of("ACADEMIC", "FINANCIAL", "ATTENDANCE");

    private static final Set<String> ALLOWED_FORMATS =
            Set.of("PDF", "CSV");

    private final ReportRespority reportRepository;

    public ReportService(ReportRespority reportRepository) {
        this.reportRepository = reportRepository;
    }

    public java.util.List<ReportBean> getAllReports() {
        return reportRepository.findAll();
    }

    public Path getUploadDirectory() {
        return UPLOAD_DIR;
    }

    @Transactional
    public void processReportUpload(
            String reportTitle,
            String category,
            String format,
            MultipartFile file) throws IOException {

        if (reportTitle == null || reportTitle.isBlank()) {
            throw new IllegalArgumentException("Report title is required.");
        }

        reportTitle = reportTitle.trim();

        if (reportTitle.length() > 150) {
            throw new IllegalArgumentException(
                    "Report title cannot exceed 150 characters.");
        }

        if (category == null ||
                !ALLOWED_CATEGORIES.contains(category)) {
            throw new IllegalArgumentException("Invalid report category.");
        }

        if (format == null ||
                !ALLOWED_FORMATS.contains(format)) {
            throw new IllegalArgumentException("Invalid report format.");
        }

        String storedFileName = null;
        String originalFileName = null;
        long fileSize = 0;
        Path savedPath = null;

        if (file != null && !file.isEmpty()) {

            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException(
                        "Maximum upload size is 10 MB.");
            }

            String submittedName = file.getOriginalFilename();

            if (submittedName == null || submittedName.isBlank()) {
                throw new IllegalArgumentException("Invalid filename.");
            }

            originalFileName = Paths.get(submittedName)
                    .getFileName()
                    .toString();

            int dotIndex = originalFileName.lastIndexOf('.');

            if (dotIndex <= 0 ||
                    dotIndex == originalFileName.length() - 1) {
                throw new IllegalArgumentException(
                        "The uploaded file must have an extension.");
            }

            String extension = originalFileName
                    .substring(dotIndex + 1)
                    .toLowerCase(Locale.ROOT);

            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new IllegalArgumentException(
                        "Unsupported file type.");
            }

            // Ensure the uploaded document extension matches the format.
            if (format.equals("PDF") && !extension.equals("pdf")) {
                throw new IllegalArgumentException(
                        "Please upload a PDF file for PDF format.");
            }

            if (format.equals("CSV") && !extension.equals("csv")) {
                throw new IllegalArgumentException(
                        "Please upload a CSV file for CSV format.");
            }

            Files.createDirectories(UPLOAD_DIR);

            storedFileName = UUID.randomUUID() + "." + extension;
            savedPath = UPLOAD_DIR.resolve(storedFileName).normalize();

            if (!savedPath.startsWith(UPLOAD_DIR)) {
                throw new SecurityException("Invalid upload path.");
            }

            try (InputStream input = file.getInputStream()) {
                Files.copy(input, savedPath);
            }

            fileSize = Files.size(savedPath);
        }

        try {
            ReportBean report = new ReportBean();
            report.setReportTitle(reportTitle);
            report.setCategory(category);
            report.setFormat(format);
            report.setFileName(storedFileName);
            report.setOriginalFileName(originalFileName);
            report.setFileSize(fileSize);

            reportRepository.save(report);

        } catch (RuntimeException e) {
            if (savedPath != null) {
                try {
                    Files.deleteIfExists(savedPath);
                } catch (IOException cleanupException) {
                    e.addSuppressed(cleanupException);
                }
            }
            throw e;
        }
    }
}
