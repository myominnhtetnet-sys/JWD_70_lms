package com.Learning_Managnment_System.JWD_70_lms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

//import com.Learning_Managnment_System.JWD_70_lms.model.StudentBean;

@Controller
@RequestMapping("/teacher")
public class PageController {
	
	@GetMapping("/")
	public String show() {		
		return "index";		
	}
	@GetMapping("/question-list")
    public String questionList() {
        return "question-list";
	}
//	
//	@GetMapping("/register")
//    public String showRegisterPage(Model model) {
//		model.addAttribute("student", new StudentBean());
//        return "register"; // register.html ကို ညွှန်းပါမည်
//    }
//	
//	@PostMapping("/register")
//    public String saveStudent(@ModelAttribute("student") StudentBean student) {
//        // ဒီနေရာမှာ Database ထဲ သို့ Data သိမ်းသည့် logic ရေးနိုင်ပါတယ်
//        // ဥပမာ - studentService.save(student);
//
//        return "redirect:/register?success"; // သို့မဟုတ် "index"
//    }

}
