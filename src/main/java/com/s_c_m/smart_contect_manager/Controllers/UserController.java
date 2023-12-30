package com.s_c_m.smart_contect_manager.Controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.s_c_m.smart_contect_manager.Dao.ContactRepository;
import com.s_c_m.smart_contect_manager.Dao.UserRepository;
import com.s_c_m.smart_contect_manager.entities.Contact;
import com.s_c_m.smart_contect_manager.entities.User;
import com.s_c_m.smart_contect_manager.helper.Message;


import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    // extended UserRepository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    // -------------------------------------------------------------------------------------------------------------
    // method for adding addCommonData to Response
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String name = principal.getName();
        User user = this.userRepository.getUserByUserName(name);
        // System.out.println(user);
        model.addAttribute("user", user);
    }
    // -------------------------------------------------------------------------------------------------------------

    // DashBoard Home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "DashBoard");
        return "user/user_dashboard";
    }

    // -------------------------------------------------------------------------------------------------------------

    // open ad form handler
    @GetMapping("/add-contact")
    public String openAddContectForm(Model model) {
        model.addAttribute("title", "Add Contacts");
        model.addAttribute("contant", "this is Contact page ");
        model.addAttribute("contact", new Contact());
        return "user/add_contact_form";
    }

    // -------------------------------------------------Process contacts------------------------------------------------------------
    // Process contacts
    @PostMapping("/process_contacts")
    public String processContacts(
            @ModelAttribute Contact contact,
            @RequestParam MultipartFile file,
            Principal principal,
            HttpSession session) {

                
        try {
            String name = principal.getName(); // here getName means user Email
            User user = this.userRepository.getUserByUserName(name); // this method fetch user row based on username(email)



            if (file.isEmpty()) {
              contact.setphoto("default.webp");
                
                
            }else{
                // this line save the image name in database
                contact.setphoto(file.getOriginalFilename());

                // this line give me classpath file object
                File savFile = new ClassPathResource("static/img").getFile();

                // used to construct a File Path object
                java.nio.file.Path path = Paths.get(savFile.getAbsolutePath()+File.separator+file.getOriginalFilename());

                /*
                 * This line of code is essentially performing a file copy operation, taking the content from *  * the input stream of one file (file) and copying it to another file specified by the path. 
                 * The REPLACE_EXISTING option ensures that if a file already exists at the destination, it * *  * will be replaced with the new content.
                */
                Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
                session.setAttribute("message", new Message("Your Contact Successfully Saved in your contcat list!! ","alert-success"));
                System.out.println("img uploaded");
            }
            
            contact.setUser(user);
            
            user.getContacts().add(contact);

            this.userRepository.save(user);
            
        } catch (Exception e) {
            session.setAttribute("message", new Message("Somthing want wrong!! " + e.getMessage(), "alert-danger"));
        }
        

        return "user/add_contact_form";
    }


    
    // -------------------------------------------------Process contacts------------------------------------------------------------
    // view contacts

    @GetMapping("/view-contacts/{page}")
    public String viewContacts(
        Model m,
        @PathVariable("page") Integer page,
        Principal principal){
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);
            Pageable pageable = PageRequest.of(page, 4);
            Page<Contact> contact = this.contactRepository.findContactByUser(user.getId(),pageable);
            m.addAttribute("contacts", contact);
            m.addAttribute("currentPage", page);
            m.addAttribute("totalPage", contact.getTotalPages());
        return "user/view_Contacts";
    }


     // -------------------------------------------------Process contacts------------------------------------------------------------
    // view contacts
   @GetMapping("/remove-contact/{cid}")
public String removeContact(@PathVariable int cid) {
        this.contactRepository.deleteById(cid);
        return "user/view_Contacts";
    }
    
}
