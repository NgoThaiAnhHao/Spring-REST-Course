package com.luv2code.springboot.employees.dao;

import com.luv2code.springboot.employees.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeDAOImpl implements EmployeeDAO {
    private final EntityManager entityManager;

    @Autowired
    public EmployeeDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Employee> findAll() {
        // Create query
        TypedQuery<Employee> theQuery = entityManager.createQuery("from Employee", Employee.class);

        // Execute query and get result list
        List<Employee> employees = theQuery.getResultList();

        // Return the results
        return employees;
    }

    @Override
    public Employee findById(long id) {
        // Find employee
        Employee employee = entityManager.find(Employee.class, id);

        // Return employee
        return employee;
    }

    @Override
    public Employee save(Employee newEmployee) {
        // Save or update the employee
        // If id == 0 => save, else update
        Employee employee = entityManager.merge(newEmployee);

        // Return result
        return employee;
    }

    @Override
    public void deleteById(long id) {
        // Find employee
        Employee employee = entityManager.find(Employee.class, id);

        // Delete employee
        entityManager.remove(employee);
    }
}
