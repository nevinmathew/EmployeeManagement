package com.employee.app.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.employee.app.domain.Department;
import com.employee.app.repository.DepartmentRepository;
import com.employee.app.repository.search.DepartmentSearchRepository;
import com.employee.app.service.DepartmentService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Department}.
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentRepository departmentRepository;

    private final DepartmentSearchRepository departmentSearchRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, DepartmentSearchRepository departmentSearchRepository) {
        this.departmentRepository = departmentRepository;
        this.departmentSearchRepository = departmentSearchRepository;
    }

    @Override
    public Department save(Department department) {
        log.debug("Request to save Department : {}", department);
        Department result = departmentRepository.save(department);
        departmentSearchRepository.index(result);
        return result;
    }

    @Override
    public Department update(Department department) {
        log.debug("Request to update Department : {}", department);
        Department result = departmentRepository.save(department);
        departmentSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Department> partialUpdate(Department department) {
        log.debug("Request to partially update Department : {}", department);

        return departmentRepository
            .findById(department.getId())
            .map(existingDepartment -> {
                if (department.getDepartmentName() != null) {
                    existingDepartment.setDepartmentName(department.getDepartmentName());
                }

                return existingDepartment;
            })
            .map(departmentRepository::save)
            .map(savedDepartment -> {
                departmentSearchRepository.save(savedDepartment);

                return savedDepartment;
            });
    }

    @Override
    public List<Department> findAll() {
        log.debug("Request to get all Departments");
        return departmentRepository.findAll();
    }

    @Override
    public Optional<Department> findOne(String id) {
        log.debug("Request to get Department : {}", id);
        return departmentRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete Department : {}", id);
        departmentRepository.deleteById(id);
        departmentSearchRepository.deleteById(id);
    }

    @Override
    public List<Department> search(String query) {
        log.debug("Request to search Departments for query {}", query);
        return StreamSupport.stream(departmentSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
