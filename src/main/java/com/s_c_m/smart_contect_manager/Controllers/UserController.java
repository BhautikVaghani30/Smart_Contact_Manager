package com.s_c_m.smart_contect_manager.Controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    // contact repository
    @Autowired
    private ContactRepository contactRepository;
    
    // BCryptPasswordEncoder password 
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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

    // -------------------------------------------------Process
    // contacts------------------------------------------------------------
    // Process contacts
    @PostMapping("/process_contacts")
    public String processContacts(
            @ModelAttribute Contact contact,
            @RequestParam MultipartFile file,
            Principal principal,
            HttpSession session) {

        try {
            String name = principal.getName(); // here getName means user Email
            User user = this.userRepository.getUserByUserName(name); // this method fetch user row based on
                                                                     // username(email)

            if (file.isEmpty()) {
                contact.setImage("default.webp");

            } else {
                // this line save the image name in database
                contact.setImage(file.getOriginalFilename());

                // this line give me classpath file object
                File savFile = new ClassPathResource("static/img").getFile();

                // used to construct a File Path object
                Path path = Paths.get(savFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                System.out.println(path);

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                session.setAttribute("message",
                        new Message("Your Contact Successfully Saved in your contact list!! ", "alert-success"));
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

    // -------------------------------------------------Process
    // contacts------------------------------------------------------------
    // view contacts

    @GetMapping("/view_contacts/{page}")
    public String viewContacts(
            Model m,
            @PathVariable("page") Integer page,
            Principal principal) {
        String name = principal.getName();
        User user = this.userRepository.getUserByUserName(name);
        Pageable pageable = PageRequest.of(page, 5);
        Page<Contact> contact = this.contactRepository.findContactByUser(user.getId(), pageable);
        m.addAttribute("contacts", contact);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPage", contact.getTotalPages());
        return "user/view_contacts";
    }

    // -------------------------------------------------contacts
    // details------------------------------------------------------------
    // contacts details
    @GetMapping("/{cid}/contact")
    public String contactDetails(@PathVariable("cid") Integer cid, Model m, Principal principal) {

        System.out.println("cid is : ----> " + cid);

        Optional<Contact> OptionalContact = this.contactRepository.findById(cid);

        if (OptionalContact.isPresent()) {
            Contact contact = OptionalContact.get();

            String username = principal.getName();
            User user = this.userRepository.getUserByUserName(username);

            if (user.getId() == contact.getUser().getId()) {
                m.addAttribute("contact", contact);
            } else {
                m.addAttribute("wrong", true);
            }
        } else {
            m.addAttribute("contactNotFound", true);
        }
        return "user/contact_detail";
    }

    // -------------------------------------------------Delete
    // contacts------------------------------------------------------------
    // Delete contacts
    @GetMapping("/delete/{cid}")
    public String getMethodName(@PathVariable("cid") Integer cId, Principal principal, Model m, HttpSession session)
            throws IOException {
        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        Contact contact = contactOptional.get();

        String image = contact.getImage();
        File savFile = new ClassPathResource("static/img").getFile();
        Path path = Paths.get(savFile.getAbsolutePath() + File.separator + image);
        Files.delete(path);

        if (contactOptional.isPresent()) {

            String username = principal.getName();

            User user = this.userRepository.getUserByUserName(username);

            if (user.getId() == contact.getUser().getId()) {

                this.contactRepository.delete(contact);

            } else {

                m.addAttribute("wrong", true);

            }

        } else {

            m.addAttribute("contactNotFound", true);

        }

        session.setAttribute("msg", new Message("Contact deleted Successfully...", "success"));
        return "redirect:/user/view_contacts/0";
    }

    // -------------------------------------------------update
    // contacts process------------------------------------------------------------
    // update contacts process
    @PostMapping("/update-contact/{cid}")
    public String getMethodName(@PathVariable("cid") int cid, Model model) {
        model.addAttribute("title", "Update-contact");
        Contact contact = this.contactRepository.findById(cid).get();
        model.addAttribute("contact", contact);
        return "user/update";
    }

    // -------------------------------------------------update
    // contacts ------------------------------------------------------------
    // update contacts
    @PostMapping("/process-update")
    public String postMethodName(
            @ModelAttribute Contact contact,
            @RequestParam("file") MultipartFile file,
            Principal principal,
            HttpSession session) {
        try {

            Contact oldContact = this.contactRepository.findById(contact.getcId()).get();

            if (file.isEmpty()) {

                contact.setImage(oldContact.getImage());

            } else {
                File deleFile = new ClassPathResource("static/img").getFile();
                File file1 = new File(deleFile, oldContact.getImage());
                file1.delete();

                File savFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(savFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());

                session.setAttribute("message",
                        new Message("Your Contact Successfully Updated in your contcat list!! ", "alert-success"));
            }

            User user = this.userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            this.contactRepository.save(contact);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(contact.getcId());
        System.out.println(contact.getName());
        return "redirect:/user/" + contact.getcId() + "/contact";
    }

    // -------------------------------------------------contacts profile
    // ------------------------------------------------------------
    // profile page process
    @GetMapping("/profile")
    public String youProfile(Model m, Principal principal) {
        User user = this.userRepository.getUserByUserName(principal.getName());
        int userid = user.getId();
        List<Contact> contactList = this.contactRepository.findByuser_id(userid);
        int size = contactList.size();
        m.addAttribute("size", size);
        System.out.println(size);
        m.addAttribute("title", "profile page");
        return "user/profile";
    }

    // -------------------------------------------------contacts setting
    // ------------------------------------------------------------
    // setting page process
    @GetMapping("/setting")
    public String setting(Model m) {
        m.addAttribute("title", "Change Password");
        return "user/setting";
    }

    // -------------------------------------------------change Password
    // ------------------------------------------------------------
    // change Password
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("old-password") String oldPassword,
            @RequestParam("new-password") String newPassword,
            @RequestParam("re-new-password") String rePassword,
            Principal principal,
            HttpSession session) {
        String userName = principal.getName();
        User currntUser = this.userRepository.getUserByUserName(userName);
        currntUser.getPassword();
        if (this.bCryptPasswordEncoder.matches(oldPassword, currntUser.getPassword())) {
            // change the password
            if (newPassword.equals(rePassword)) {
                currntUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
                this.userRepository.save(currntUser);
            } else {
                session.setAttribute("message",
                        new Message("New password and re-Entered password should be the same.!!", "alert-danger"));
            }

        } else {
            session.setAttribute("message",
                    new Message("Old Password Wrong Please Enter right Password", "alert-danger"));

        }

        return "redirect:/user/index";
    }

}
