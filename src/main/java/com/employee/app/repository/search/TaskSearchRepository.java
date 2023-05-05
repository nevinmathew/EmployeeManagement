package com.employee.app.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.employee.app.domain.Task;
import com.employee.app.repository.TaskRepository;
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
 * Spring Data Elasticsearch repository for the {@link Task} entity.
 */
public interface TaskSearchRepository extends ElasticsearchRepository<Task, String>, TaskSearchRepositoryInternal {}

interface TaskSearchRepositoryInternal {
    Stream<Task> search(String query);

    Stream<Task> search(Query query);

    void index(Task entity);
}

class TaskSearchRepositoryInternalImpl implements TaskSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final TaskRepository repository;

    TaskSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, TaskRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Task> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Task> search(Query query) {
        return elasticsearchTemplate.search(query, Task.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Task entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
