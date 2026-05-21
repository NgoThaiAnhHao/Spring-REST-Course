package com.luv2code.springboot.employees.service;

import com.luv2code.springboot.employees.dao.EmployeeDAO;
import com.luv2code.springboot.employees.dto.EmployeeDto;
import com.luv2code.springboot.employees.entity.Employee;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeDAO employeeDAO;

    public EmployeeServiceImpl(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    @Override
    public List<Employee> findAll() {
        return employeeDAO.findAll();
    }

    @Override
    public Employee findById(long id) {
        return employeeDAO.findById(id);
    }

    @Override
    @Transactional
    public Employee save(EmployeeDto employeeDto) {
        Employee employee = convertToEmployee(0, employeeDto);
        return employeeDAO.save(employee);
    }

    @Override
    @Transactional
    public Employee update(long id, EmployeeDto employeeDto) {
        Employee employee = convertToEmployee(id, employeeDto);
        return employeeDAO.save(employee);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        employeeDAO.deleteById(id);
    }

    @Override
    public Employee convertToEmployee(long id, EmployeeDto employeeDto) {
        return new Employee(
                id,
                employeeDto.getFirstName(),
                employeeDto.getLastName(),
                employeeDto.getEmail()
        );
    }
}
