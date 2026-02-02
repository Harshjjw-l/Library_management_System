package com.lms.library_management_system.service;

import com.lms.library_management_system.model.Location;
import com.lms.library_management_system.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public Location addLocation(Location location) {
        return locationRepository.save(location);
    }

    public List<Location> findAllLocations() {
        return locationRepository.findAll();
    }

    public Location findLocationById(Integer id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
    }
    
    // You can add update and delete methods here...
}