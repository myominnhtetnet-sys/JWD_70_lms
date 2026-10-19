/*
 * package com.Learning_Managnment_System.JWD_70_lms.Controller;
 * 
 * import java.util.List; import java.util.Map;
 * 
 * import org.springframework.http.ResponseEntity; import
 * org.springframework.web.bind.annotation.DeleteMapping; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.PathVariable; import
 * org.springframework.web.bind.annotation.PostMapping; import
 * org.springframework.web.bind.annotation.PutMapping; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * org.springframework.web.bind.annotation.RequestPart; import
 * org.springframework.web.bind.annotation.RestController; import
 * org.springframework.web.multipart.MultipartFile;
 * 
 * import com.Learning_Managnment_System.JWD_70_lms.Repository.CourseRepository;
 * import com.Learning_Managnment_System.JWD_70_lms.Service.CategoryService;
 * import
 * com.Learning_Managnment_System.JWD_70_lms.Service.CourseFileUploadService;
 * import com.Learning_Managnment_System.JWD_70_lms.model.CategoryBean; import
 * com.Learning_Managnment_System.JWD_70_lms.model.CourseBean; import
 * com.Learning_Managnment_System.JWD_70_lms.model.LoginBean;
 * 
 * import jakarta.servlet.http.HttpSession;
 * 
 *//**
	 * Course_Management.html ရဲ့ JS က ခေါ်တဲ့ REST API များ GET
	 * /admin/courses/api/list GET /admin/courses/api/categories GET
	 * /admin/courses/api/{id} POST /admin/courses/api/create (multipart:
	 * course=JSON, image=file) PUT /admin/courses/api/update (multipart:
	 * course=JSON, image=file) DELETE /admin/courses/api/delete/{id}
	 *//*
		 * @RestController
		 * 
		 * @RequestMapping("/admin/courses/api") public class CourseApiController {
		 * 
		 * private final CourseRepository courseRepository; private final
		 * CategoryService categoryService; private final CourseFileUploadService
		 * fileUploadService;
		 * 
		 * public CourseApiController(CourseRepository courseRepository, CategoryService
		 * categoryService, CourseFileUploadService fileUploadService) {
		 * this.courseRepository = courseRepository; this.categoryService =
		 * categoryService; this.fileUploadService = fileUploadService; }
		 * 
		 * // 1. Course စာရင်း အပြည့်အစုံ (DRAFT ပါ ပါဝင်)
		 * 
		 * @GetMapping("/list") public List<CourseBean> list() { return
		 * courseRepository.findAll(); }
		 * 
		 * // 2. Category dropdown အတွက်
		 * 
		 * @GetMapping("/categories") public List<CategoryBean> categories() { return
		 * categoryService.getAllCategories(); }
		 * 
		 * // 3. Course တစ်ခု (view / edit modal)
		 * 
		 * @GetMapping("/{id}") public ResponseEntity<CourseBean>
		 * detail(@PathVariable("id") Integer id) { CourseBean course =
		 * courseRepository.findById(id); if (course == null) { return
		 * ResponseEntity.notFound().build(); } return ResponseEntity.ok(course); }
		 * 
		 * // 4. အသစ်ဖန်တီး
		 * 
		 * @PostMapping("/create") public ResponseEntity<Map<String, Object>> create(
		 * 
		 * @RequestPart("course") CourseBean course,
		 * 
		 * @RequestPart(value = "image", required = false) MultipartFile image,
		 * HttpSession session) throws Exception {
		 * 
		 * if (course.getTitle() == null || course.getTitle().trim().isEmpty()) { return
		 * ResponseEntity.badRequest().body(Map.of("message",
		 * "Course title is required.")); }
		 * 
		 * // Thumbnail file သိမ်း (2MB + image type validation က service ထဲမှာ
		 * ရှိပြီးသား) if (image != null && !image.isEmpty()) {
		 * course.setThumbnail(fileUploadService.saveFile(image)); }
		 * 
		 * // Form မှာ description (long) မပါလို့ short description ကို fallback ထည့် if
		 * (course.getDescription() == null || course.getDescription().isBlank()) {
		 * course.setDescription(course.getShort_description()); }
		 * 
		 * LoginBean login = (LoginBean) session.getAttribute("currentUser"); if (login
		 * != null) { course.setCreated_by(login.getUser_id()); }
		 * 
		 * courseRepository.save(course); return ResponseEntity.ok(Map.of("success",
		 * true, "message", "Course created.")); }
		 * 
		 * // 5. ပြင်ဆင်
		 * 
		 * @PutMapping("/update") public ResponseEntity<Map<String, Object>> update(
		 * 
		 * @RequestPart("course") CourseBean course,
		 * 
		 * @RequestPart(value = "image", required = false) MultipartFile image) throws
		 * Exception {
		 * 
		 * if (course.getCourse_id() == null) { return
		 * ResponseEntity.badRequest().body(Map.of("message",
		 * "course_id is required.")); }
		 * 
		 * CourseBean existing = courseRepository.findById(course.getCourse_id()); if
		 * (existing == null) { return ResponseEntity.notFound().build(); }
		 * 
		 * // ပုံအသစ် မပါလာရင် thumbnail ကို null ထားရမယ် — // repository က
		 * thumbnail==null ဆိုရင် အဲ့ column ကို မထိဘဲ အဟောင်း ကျန်တယ် if (image != null
		 * && !image.isEmpty()) {
		 * course.setThumbnail(fileUploadService.saveFile(image)); }
		 * 
		 * // description မပို့လို့ null ဖြစ်ရင် အဟောင်း ပျက်မသွားအောင် if
		 * (course.getDescription() == null || course.getDescription().isBlank()) {
		 * course.setDescription(existing.getDescription()); }
		 * 
		 * courseRepository.update(course); return ResponseEntity.ok(Map.of("success",
		 * true, "message", "Course updated.")); }
		 * 
		 * // 6. ဖျက် (soft delete)
		 * 
		 * @DeleteMapping("/delete/{id}") public ResponseEntity<Map<String, Object>>
		 * delete(@PathVariable("id") Integer id) { courseRepository.softDelete(id);
		 * return ResponseEntity.ok(Map.of("success", true, "message",
		 * "Course deleted.")); } }
		 */