package com.challenge.api.service;

import com.challenge.api.model.CreateEmployeeRequest;
import com.challenge.api.model.Employee;
import java.util.List;
import java.util.UUID;

/**
 * Service layer interface for Employee-related operations.
 */
public interface EmployeeService {

    List<Employee> getAll();

    Employee getByUuid(UUID uuid);

    Employee create(CreateEmployeeRequest request);
}
