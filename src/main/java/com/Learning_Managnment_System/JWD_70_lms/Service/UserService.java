package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.Admin_Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminUserBean;

@Service
public class UserService {

    @Autowired
    private Admin_Repository adminRepository;

  
    // 1. Get All Active Users (Deleted မလုပ်ရသေးတာတွေ)
    public List<AdminUserBean> getAllUsers() {
        return adminRepository.getAllUsers();
    }

    // 2. Search Users
    public List<AdminUserBean> searchUsers(String keyword) {
        return adminRepository.searchUsers(keyword);
    }

    // 3. Get User By ID
    public AdminUserBean getUserById(int userId) {
        return adminRepository.getUserById(userId);
    }

    // 4. Add User (Password Hash လုပ်ပြီးမှ သိမ်းမည်)
    public boolean createUser(AdminUserBean user) {
        // Email ရှိပြီးသားလား စစ်ဆေးခြင်း
        if (adminRepository.emailExists(user.getEmail())) {
            return false;
        }
       
        
        int result = adminRepository.addUser(user);
        return result > 0;
    }

    // 5. Update User
    public boolean updateUser(AdminUserBean user) {
        // အခြား User မှာ ဒီ Email ရှိပြီးသားလား စစ်ဆေးခြင်း
        if (adminRepository.emailExistsForOtherUser(user.getEmail(), user.getUser_id())) {
            return false;
        }
        int result = adminRepository.updateUser(user);
        return result > 0;
    }

    // 6. Suspend / Activate Toggle (Status ပြောင်းရန်)
    public void toggleSuspend(int userId) {
        AdminUserBean user = adminRepository.getUserById(userId);
        if (user != null) {
            String newStatus = "ACTIVE".equals(user.getStatus()) ? "SUSPENDED" : "ACTIVE";
            adminRepository.updateStatus(userId, newStatus);
        }
    }

    // 7. Soft Delete User (Trash ထဲရွှေ့မည်)
    public void softDeleteUser(int userId) {
        adminRepository.deleteUser(userId);
    }

    // 8. Restore User (Trash ထဲကနေ ပြန်ယူမည်)
    public void restoreUser(int userId) {
        adminRepository.restoreUser(userId);
    }

    // 9. Get Deleted Users (Trash ထဲက စာရင်းကြည့်ရန်)
    public List<AdminUserBean> getDeletedUsers() {
        return adminRepository.getDeletedUsers();
    }
    
 // 10. Dashboard အတွက် Active ဖြစ်ပြီး Deleted မဖြစ်သေးသော ကျောင်းသားဦးရေ တွက်ချက်ရန်
    public long countActiveStudents() {
        List<AdminUserBean> allUsers = adminRepository.getAllUsers();
        if (allUsers == null) {
            return 0;
        }
        return allUsers.stream()
                .filter(u -> "ACTIVE".equals(u.getStatus()) && !u.isDeleted())
                .count();
    }
    
    
}