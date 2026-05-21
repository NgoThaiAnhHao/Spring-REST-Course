package com.luv2code.springboot.employees.service;

import com.luv2code.springboot.employees.dto.EmployeeDto;
import com.luv2code.springboot.employees.entity.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> findAll();

    Employee findById(long id);

    Employee save(EmployeeDto employeeDto);

    Employee update(long id, EmployeeDto employeeDto);

    void deleteById(long id);

    Employee convertToEmployee(long id, EmployeeDto employeeDto);
}
