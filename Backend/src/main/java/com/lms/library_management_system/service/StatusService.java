package com.lms.library_management_system.service;

import com.lms.library_management_system.model.Status;
import com.lms.library_management_system.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    // Note: This is just basic CRUD for now. The real "issue book" logic will be more complex.
    public Status addStatus(Status status) {
        return statusRepository.save(status);
    }

    public List<Status> findAllStatuses() {
        return statusRepository.findAll();
    }
    
    // You can add findById, update, delete methods here...
}