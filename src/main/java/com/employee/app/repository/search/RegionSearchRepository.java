package com.employee.app.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.employee.app.domain.Region;
import com.employee.app.repository.RegionRepository;
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
 * Spring Data Elasticsearch repository for the {@link Region} entity.
 */
public interface RegionSearchRepository extends ElasticsearchRepository<Region, String>, RegionSearchRepositoryInternal {}

interface RegionSearchRepositoryInternal {
    Stream<Region> search(String query);

    Stream<Region> search(Query query);

    void index(Region entity);
}

class RegionSearchRepositoryInternalImpl implements RegionSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final RegionRepository repository;

    RegionSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, RegionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Region> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Region> search(Query query) {
        return elasticsearchTemplate.search(query, Region.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Region entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
