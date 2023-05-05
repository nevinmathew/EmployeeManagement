package com.employee.app.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.employee.app.domain.Location;
import com.employee.app.repository.LocationRepository;
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
 * Spring Data Elasticsearch repository for the {@link Location} entity.
 */
public interface LocationSearchRepository extends ElasticsearchRepository<Location, String>, LocationSearchRepositoryInternal {}

interface LocationSearchRepositoryInternal {
    Stream<Location> search(String query);

    Stream<Location> search(Query query);

    void index(Location entity);
}

class LocationSearchRepositoryInternalImpl implements LocationSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final LocationRepository repository;

    LocationSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, LocationRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Location> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Location> search(Query query) {
        return elasticsearchTemplate.search(query, Location.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Location entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
