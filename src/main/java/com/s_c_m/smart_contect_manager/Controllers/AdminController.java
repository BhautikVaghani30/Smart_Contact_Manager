package com.s_c_m.smart_contect_manager.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @RequestMapping("/index")
    public String adminDashboard(){
        return "admin/admin_dashboard";
    }
}
