package com.employee.app.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.employee.app.domain.Department;
import com.employee.app.repository.DepartmentRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Department} entity.
 */
public interface DepartmentSearchRepository extends ElasticsearchRepository<Department, String>, DepartmentSearchRepositoryInternal {}

interface DepartmentSearchRepositoryInternal {
    Stream<Department> search(String query);

    Stream<Department> search(Query query);

    void index(Department entity);
}

class DepartmentSearchRepositoryInternalImpl implements DepartmentSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final DepartmentRepository repository;

    DepartmentSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, DepartmentRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Department> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Department> search(Query query) {
        return elasticsearchTemplate.search(query, Department.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Department entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
