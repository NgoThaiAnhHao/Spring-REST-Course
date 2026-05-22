package com.luv2code.springboot.employees.service;

import com.luv2code.springboot.employees.dao.EmployeeRepository;
import com.luv2code.springboot.employees.dto.EmployeeDto;
import com.luv2code.springboot.employees.entity.Employee;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findById(long id) {
        return employeeRepository
                .findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Can't found employee id - " + id)
                );
    }

    @Override
    @Transactional
    public Employee save(EmployeeDto employeeDto) {
        Employee employee = convertToEmployee(0, employeeDto);
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public Employee update(long id, EmployeeDto employeeDto) {
        Employee employee = convertToEmployee(id, employeeDto);
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        employeeRepository.deleteById(id);
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
