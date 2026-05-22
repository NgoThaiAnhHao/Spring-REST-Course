package com.luv2code.springboot.employees.controller;

import com.luv2code.springboot.employees.dto.EmployeeDto;
import com.luv2code.springboot.employees.entity.Employee;
import com.luv2code.springboot.employees.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee REST API Endpoints", description = "Operations related to employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // ========================================== GET ALL ==========================================
    @Operation(summary = "Get all employees", description = "Retrieve a list of all employees")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public List<Employee> findAll() {
        return employeeService.findAll();
    }
    // ========================================== END GET ALL ==========================================

    // ========================================== GET BY ID ==========================================
    @Operation(summary = "Fetch single employee", description = "Get a single employee from database")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Employee findById(@PathVariable @Min(value = 1) long id) {
        return employeeService.findById(id);
    }
    // ========================================== GET BY ID ==========================================

    // ========================================== SAVE ==========================================
    @Operation(summary = "Create a new employee", description = "Add a new employee to database")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public Employee saveEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        return employeeService.save(employeeDto);
    }
    // ========================================== END SAVE ==========================================

    // ========================================== UPDATE EMPLOYEE ==========================================
    @Operation(summary = "Update an employee", description = "Update an employee to database")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public Employee updateEmployee(@Valid @RequestBody EmployeeDto employeeDto, @PathVariable @Min(value = 1) long id) {
        return employeeService.update(id, employeeDto);
    }
    // ========================================== END UPDATE EMPLOYEE ==========================================

    // ========================================== DELETE BY ID ==========================================
    @Operation(summary = "Delete an employee", description = "Delete an employee to database")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable @Min(value = 1) long id) {
        employeeService.deleteById(id);
    }
    // ========================================== END DELETE BY ID ==========================================
}
