package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class CertificateDownloadController {

    @Autowired
      JdbcTemplate jdbcTemplate;

    @GetMapping("/student/awards/download/{id}")
    public ResponseEntity<Resource> streamCertificatePdf(@PathVariable("id") int certificateId) {
        try {
            String sql = "SELECT file_path FROM learning_db.certificates WHERE certificate_id = ? LIMIT 1";
            String databasePath = jdbcTemplate.queryForObject(sql, String.class, certificateId);

            if (databasePath == null) {
                return ResponseEntity.notFound().build();
            }

            Path physicalLocation = Paths.get(databasePath).toAbsolutePath().normalize();
            Resource pdfResource = new UrlResource(physicalLocation.toUri());

            if (!pdfResource.exists() || !pdfResource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"Diploma-Ref-" + certificateId + ".pdf\"")
                    .body(pdfResource);

        } catch (Exception e) {
            System.err.println("CRITICAL -> Error executing certificate streaming payload resource maps: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
