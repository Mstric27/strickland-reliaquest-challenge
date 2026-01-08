package com.challenge.api.service;

import com.challenge.api.model.CreateEmployeeRequest;
import com.challenge.api.model.Employee;
import com.challenge.api.model.EmployeeModel;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * In-memory implementation of the EmployeeService interface.
 */
@Service
public class InMemoryEmployeeService implements EmployeeService {

    /**
     * Data structure to hold Employees in memory.
     */
    private final Map<UUID, Employee> employees = new ConcurrentHashMap<>();

    public InMemoryEmployeeService() {
        seed();
    }

    @Override
    public List<Employee> getAll() {
        return new ArrayList<>(employees.values());
    }

    @Override
    public Employee getByUuid(UUID uuid) {
        return employees.get(uuid);
    }

    /**
     * Sets the Employee's UUID, full name, and contract hire date if not provided.
     */
    @Override
    public Employee create(CreateEmployeeRequest request) {
        validateCreateRequest(request);

        EmployeeModel employee = new EmployeeModel();
        employee.setUuid(UUID.randomUUID());
        employee.setFirstName(request.getFirstName().trim());
        employee.setLastName(request.getLastName().trim());
        employee.setFullName(employee.getFirstName() + " " + employee.getLastName());
        employee.setAge(request.getAge());
        employee.setSalary(request.getSalary());
        employee.setJobTitle(request.getJobTitle());
        employee.setEmail(request.getEmail().trim());
        employee.setContractTerminationDate(request.getContractTerminationDate());
        employee.setContractHireDate(
                request.getContractHireDate() != null ? request.getContractHireDate() : Instant.now());

        employees.put(employee.getUuid(), employee);
        return employee;
    }

    /**
     * Validates that all required fields are present and non-null.
     * contractTerminationDate is optional.
     */
    private void validateCreateRequest(CreateEmployeeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required.");
        }
        if (!StringUtils.hasText(request.getFirstName())) {
            throw new IllegalArgumentException("firstName is required.");
        }
        if (!StringUtils.hasText(request.getLastName())) {
            throw new IllegalArgumentException("lastName is required.");
        }
        if (!StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException("email is required.");
        }
        if (!StringUtils.hasText(request.getJobTitle())) {
            throw new IllegalArgumentException("jobTitle is required.");
        }
        if (request.getAge() == null) {
            throw new IllegalArgumentException("age is required.");
        }
        if (request.getSalary() == null || request.getSalary() < 0) {
            throw new IllegalArgumentException("salary must be >= 0.");
        }
    }

    /**
     * Generate mock Employees.
     */
    private void seed() {
        CreateEmployeeRequest a = new CreateEmployeeRequest();
        a.setFirstName("Jordan");
        a.setLastName("Lee");
        a.setEmail("jordan.lee@company.com");
        a.setJobTitle("Software Engineer I");
        a.setAge(24);
        a.setSalary(85000);
        create(a);

        CreateEmployeeRequest b = new CreateEmployeeRequest();
        b.setFirstName("Avery");
        b.setLastName("Patel");
        b.setEmail("avery.patel@company.com");
        b.setJobTitle("QA Engineer");
        b.setAge(27);
        b.setSalary(78000);
        create(b);
    }
}
