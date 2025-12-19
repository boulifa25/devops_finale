package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.repositories.DepartmentRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class DepartmentServiceTest {


    @Mock
    DepartmentRepository departmentRepository;

    @InjectMocks
    DepartmentService departmentService;

    @Test
    void getAllDepartments_shouldReturnList() {
        when(departmentRepository.findAll())
                .thenReturn(List.of(new Department(), new Department()));

        List<Department> result = departmentService.getAllDepartments();

        assertEquals(2, result.size());
        verify(departmentRepository).findAll();
    }

    @Test
    void getDepartmentById_shouldReturnDepartment() {
        Department dep = new Department();
        dep.setIdDepartment(1L);

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(dep));

        Department result = departmentService.getDepartmentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getIdDepartment());
        verify(departmentRepository).findById(1L);
    }

    @Test
    void saveDepartment_shouldSaveAndReturn() {
        Department dep = new Department();
        dep.setName("Info");

        when(departmentRepository.save(dep)).thenReturn(dep);

        Department result = departmentService.saveDepartment(dep);

        assertEquals("Info", result.getName());
        verify(departmentRepository).save(dep);
    }

    @Test
    void deleteDepartment_shouldCallRepository() {
        departmentService.deleteDepartment(5L);
        verify(departmentRepository).deleteById(5L);
    }
    
}
