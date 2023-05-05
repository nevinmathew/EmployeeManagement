package com.employee.app.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.employee.app.domain.Country;
import com.employee.app.repository.CountryRepository;
import com.employee.app.repository.search.CountrySearchRepository;
import com.employee.app.service.CountryService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Country}.
 */
@Service
public class CountryServiceImpl implements CountryService {

    private final Logger log = LoggerFactory.getLogger(CountryServiceImpl.class);

    private final CountryRepository countryRepository;

    private final CountrySearchRepository countrySearchRepository;

    public CountryServiceImpl(CountryRepository countryRepository, CountrySearchRepository countrySearchRepository) {
        this.countryRepository = countryRepository;
        this.countrySearchRepository = countrySearchRepository;
    }

    @Override
    public Country save(Country country) {
        log.debug("Request to save Country : {}", country);
        Country result = countryRepository.save(country);
        countrySearchRepository.index(result);
        return result;
    }

    @Override
    public Country update(Country country) {
        log.debug("Request to update Country : {}", country);
        Country result = countryRepository.save(country);
        countrySearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Country> partialUpdate(Country country) {
        log.debug("Request to partially update Country : {}", country);

        return countryRepository
            .findById(country.getId())
            .map(existingCountry -> {
                if (country.getCountryName() != null) {
                    existingCountry.setCountryName(country.getCountryName());
                }

                return existingCountry;
            })
            .map(countryRepository::save)
            .map(savedCountry -> {
                countrySearchRepository.save(savedCountry);

                return savedCountry;
            });
    }

    @Override
    public List<Country> findAll() {
        log.debug("Request to get all Countries");
        return countryRepository.findAll();
    }

    @Override
    public Optional<Country> findOne(String id) {
        log.debug("Request to get Country : {}", id);
        return countryRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete Country : {}", id);
        countryRepository.deleteById(id);
        countrySearchRepository.deleteById(id);
    }

    @Override
    public List<Country> search(String query) {
        log.debug("Request to search Countries for query {}", query);
        return StreamSupport.stream(countrySearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
