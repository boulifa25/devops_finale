package tn.esprit.studentmanagement.controllers;

import org.junit.jupiter.api.Test;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.services.IDepartmentService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepartmentControllerTest {

    @Test
    void getAllDepartments_shouldReturnListFromService() {
        IDepartmentService service = mock(IDepartmentService.class);
        List<Department> departments = List.of(new Department(), new Department());
        when(service.getAllDepartments()).thenReturn(departments);

        DepartmentController controller = new DepartmentController(service);

        // 🔥 IMPORTANT: méthode correcte = getAllDepartments()
        List<Department> result = controller.getAllDepartments();

        assertEquals(2, result.size());
        assertSame(departments, result);
        verify(service).getAllDepartments();
    }

    @Test
    void getDepartment_shouldReturnDepartmentFromService() {
        IDepartmentService service = mock(IDepartmentService.class);
        Department dep = new Department();
        when(service.getDepartmentById(1L)).thenReturn(dep);

        DepartmentController controller = new DepartmentController(service);

        Department result = controller.getDepartment(1L);

        assertSame(dep, result);
        verify(service).getDepartmentById(1L);
    }

    @Test
    void createDepartment_shouldDelegateToService() {
        IDepartmentService service = mock(IDepartmentService.class);
        Department toSave = new Department();
        when(service.saveDepartment(toSave)).thenReturn(toSave);

        DepartmentController controller = new DepartmentController(service);

        Department result = controller.createDepartment(toSave);

        assertSame(toSave, result);
        verify(service).saveDepartment(toSave);
    }

    @Test
    void updateDepartment_shouldDelegateToService() {
        IDepartmentService service = mock(IDepartmentService.class);
        Department updated = new Department();
        when(service.saveDepartment(updated)).thenReturn(updated);

        DepartmentController controller = new DepartmentController(service);

        Department result = controller.updateDepartment(updated);

        assertSame(updated, result);
        verify(service).saveDepartment(updated);
    }

    @Test
    void deleteDepartment_shouldCallService() {
        IDepartmentService service = mock(IDepartmentService.class);
        DepartmentController controller = new DepartmentController(service);

        controller.deleteDepartment(1L);

        verify(service).deleteDepartment(1L);
    }
}
