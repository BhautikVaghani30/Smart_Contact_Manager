package com.s_c_m.smart_contect_manager.Dao;


import org.springframework.data.domain.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.s_c_m.smart_contect_manager.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
    
    @Query("from Contact as c where c.user.id =:userId")
    // currantpage - pageabl
    // contact - page - 5
    public Page<Contact> findContactByUser(@Param("userId") int userId,Pageable pageable);
    
}
