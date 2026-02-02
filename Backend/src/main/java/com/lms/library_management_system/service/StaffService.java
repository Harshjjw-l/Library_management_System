package com.lms.library_management_system.service;

import com.lms.library_management_system.model.Staff;
import com.lms.library_management_system.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    public Staff addStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public List<Staff> findAllStaff() {
        return staffRepository.findAll();
    }
    
    public Staff findStaffById(String sIdNo) {
        return staffRepository.findById(sIdNo)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + sIdNo));
    }

    // You can add update and delete methods here...
}