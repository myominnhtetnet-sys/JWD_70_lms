/*
 * package com.Learning_Managnment_System.JWD_70_lms.Controller;
 * 
 * import java.util.List; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.http.HttpStatus; import
 * org.springframework.http.ResponseEntity; import
 * org.springframework.stereotype.Controller; import
 * org.springframework.web.bind.annotation.*;
 * 
 * import com.Learning_Managnment_System.JWD_70_lms.Service.AnnouncementService;
 * import com.Learning_Managnment_System.JWD_70_lms.Service.BatchService; import
 * com.Learning_Managnment_System.JWD_70_lms.model.AnnouncementBean; import
 * com.Learning_Managnment_System.JWD_70_lms.model.BatchBean;
 * 
 * @Controller
 * 
 * @CrossOrigin(origins = "*") public class AnnouncementController {
 * 
 * private final AnnouncementService announcementService;
 * 
 * @Autowired private BatchService batchService;
 * 
 * public AnnouncementController(AnnouncementService announcementService) {
 * this.announcementService = announcementService; }
 * 
 * // 1. HTML View Page ပြသရန် (URL: /admin/announcements)
 * 
 * @GetMapping("/admin/announcements") public String showAnnouncementsPage() {
 * return "Announcements_Management"; //
 * src/main/resources/templates/admin/announcements.html }
 * 
 * // ========================================== // REST API ENDPOINTS (JSON
 * Data Return) // ==========================================
 * 
 * @GetMapping("/api/announcements")
 * 
 * @ResponseBody public ResponseEntity<List<AnnouncementBean>> getAll() { return
 * ResponseEntity.ok(announcementService.getAllAnnouncements()); }
 * 
 * @GetMapping("/api/announcements/{id}")
 * 
 * @ResponseBody public ResponseEntity<AnnouncementBean> getById(@PathVariable
 * Long id) { AnnouncementBean announcement =
 * announcementService.getAnnouncementById(id); if (announcement != null) {
 * return ResponseEntity.ok(announcement); } return
 * ResponseEntity.notFound().build(); }
 * 
 * @PostMapping("/api/announcements")
 * 
 * @ResponseBody public ResponseEntity<String> create(@RequestBody
 * AnnouncementBean announcement) { boolean isCreated =
 * announcementService.createAnnouncement(announcement); if (isCreated) { return
 * ResponseEntity.status(HttpStatus.CREATED).body("Saved successfully."); }
 * return ResponseEntity.badRequest().body("Failed to save."); }
 * 
 * @PutMapping("/api/announcements/{id}")
 * 
 * @ResponseBody public ResponseEntity<String> update(@PathVariable Long
 * id, @RequestBody AnnouncementBean announcement) { boolean isUpdated =
 * announcementService.updateAnnouncement(id, announcement); if (isUpdated) {
 * return ResponseEntity.ok("Updated successfully."); } return
 * ResponseEntity.notFound().build(); }
 * 
 * @DeleteMapping("/api/announcements/{id}")
 * 
 * @ResponseBody public ResponseEntity<String> delete(@PathVariable Long id) {
 * boolean isDeleted = announcementService.deleteAnnouncement(id); if
 * (isDeleted) { return ResponseEntity.ok("Deleted successfully."); } return
 * ResponseEntity.notFound().build(); }
 * 
 * @GetMapping("/api/announcements/batches")
 * 
 * @ResponseBody public List<BatchBean> getActiveBatchesApi() { return
 * batchService.getAllActiveBatches(); }
 * 
 * @GetMapping("/api/announcements/latest")
 * 
 * @ResponseBody public ResponseEntity<List<AnnouncementBean>>
 * getLatestAnnouncements() { List<AnnouncementBean> latestList =
 * announcementService.getLatestAnnouncements(2); return
 * ResponseEntity.ok(latestList); } }
 */