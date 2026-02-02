package com.lms.library_management_system.service;

import com.lms.library_management_system.model.Department;
import com.lms.library_management_system.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Department addDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public List<Department> findAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department findDepartmentById(String deptNo) {
        return departmentRepository.findById(deptNo)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + deptNo));
    }

    public Department updateDepartment(String deptNo, Department departmentDetails) {
        Department existingDepartment = findDepartmentById(deptNo); // Reuse findById to check existence
        existingDepartment.setName(departmentDetails.getName());
        existingDepartment.setHod(departmentDetails.getHod());
        existingDepartment.setPhone(departmentDetails.getPhone());
        existingDepartment.setEmailId(departmentDetails.getEmailId());
        return departmentRepository.save(existingDepartment);
    }

    public void deleteDepartment(String deptNo) {
        if (!departmentRepository.existsById(deptNo)) {
            throw new RuntimeException("Department not found with id: " + deptNo);
        }
        departmentRepository.deleteById(deptNo);
    }
}