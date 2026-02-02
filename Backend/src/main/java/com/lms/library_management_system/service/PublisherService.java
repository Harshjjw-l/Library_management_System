package com.lms.library_management_system.service;

import com.lms.library_management_system.model.Publisher;
import com.lms.library_management_system.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;

    public Publisher addPublisher(Publisher publisher) {
        return publisherRepository.save(publisher);
    }

    public List<Publisher> findAllPublishers() {
        return publisherRepository.findAll();
    }

    public Publisher findPublisherById(String pubId) {
        return publisherRepository.findById(pubId)
                .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + pubId));
    }
    
    // You can add update and delete methods here...
}