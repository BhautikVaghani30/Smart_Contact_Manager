package com.s_c_m.smart_contect_manager.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
//  org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.s_c_m.smart_contect_manager.Dao.UserRepository;
import com.s_c_m.smart_contect_manager.entities.User;
import com.s_c_m.smart_contect_manager.helper.DuplicateEmailException;
import com.s_c_m.smart_contect_manager.helper.Message;

import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "home-smart contet manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "about-smart contet manager");
        return "about";
    }

    // @RequestMapping("/signup")

    // public String signup(Model model){
    // model.addAttribute("title","signup-smart contet manager");
    // return "signup";
    // }
    @RequestMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "signup-smart contet manager");
        model.addAttribute("user", new User());

        return "signup";
    }

    @RequestMapping(value = "/do_ragister", method = RequestMethod.POST)
    public String registerUser(
          
            @ModelAttribute("user") User user,
            @RequestParam("re-pwd") String pwd,
            @RequestParam(value = "agreeterm", defaultValue = "false") boolean agreeterm,
           
            Model model,
            HttpSession session) {

        try {
           

            if (user == null) {
                return "signup";
            }

            // Handle the password
            if (!pwd.equals(user.getPassword())) {
                // System.out.println("you have to Enter same Password");
                model.addAttribute("pwd", pwd);
                throw new Exception("you have to Enter same Password");
            } else {
                model.addAttribute("pwd", user.getPassword());
            }

            // Check for email address already exist or not
            if (userRepository.findByEmail(user.getEmail()) != null) {
                System.out.println(userRepository.findByEmail(user.getEmail()));
                throw new DuplicateEmailException("Email address already exists");
            }

            //  Handle the agreement of terms and condition
            if (!agreeterm) {
                model.addAttribute("checked", false);
                System.out.println("You have not agreed to the terms and conditions");
                throw new Exception("You have not agreed to the terms and conditions");
            } else {
                model.addAttribute("checked", true);
            }

            //  if (result.hasErrors()) {
            //     System.out.println(result.toString());
            //     model.addAttribute("user", user);
            //     return "signup";
            // }


            // Set some default value of User field
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));


            // Save User in database
            userRepository.save(user);

            // clear all model atribute value
            ((org.springframework.ui.ModelMap) model).clear();

            // Send Empty User object in frontend
            model.addAttribute("user", new User());

            // if complete All above proccess than Send Success message to Frontend
            // session.setAttribute("message", new Message("SuccessFully Registerd !! ", "alert-success"));
            return "redirect:/signin?message=SuccessFully Registerd please signin !!";

        } catch (Exception e) {
            // e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Somthing want wrong!! " + e.getMessage(), "alert-danger"));
            return "signup";
        }

    }


    
    @RequestMapping("/signin")
    public String login(Model model) {
        model.addAttribute("title", "login-smart contact manager");
        return "login";
    }

}
