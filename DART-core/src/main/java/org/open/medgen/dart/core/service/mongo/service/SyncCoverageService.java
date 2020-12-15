/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.service;


import com.mongodb.client.MongoCollection;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.open.medgen.dart.core.model.mongo.aggregations.CoverageResult;
import org.open.medgen.dart.core.model.mongo.coverage.CoverageEntry;
import org.open.medgen.dart.core.service.mongo.provider.SyncCollectionFactory;
import static com.mongodb.client.model.Filters.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;

/**
 *
 * @author dbarreca
 */
@Stateless
public class SyncCoverageService {

    @Inject
    private SyncCollectionFactory collectionFactory;

    private MongoCollection<CoverageEntry> coverageCollection;

    private final static Logger LOGGER = Logger.getLogger(SyncCoverageService.class.getName());

    @PostConstruct
    private void startup() {
        coverageCollection = collectionFactory.getCollection(CoverageEntry.class);

        LOGGER.info("Initializing SyncCoverageService...");
    }

    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutting down SyncCoverageService...");

    }

    public void saveCoverageEntries(List<CoverageEntry> entries) {
        coverageCollection.insertMany(entries);
    }


    public void removeCoverageEntries(ObjectId id) {
        coverageCollection.deleteMany(eq(CoverageEntry.COVERAGE_FILE,id));
    }

    public Iterator<CoverageResult> getCoverageIterator(List<String> coverageResults, String geneFilter, String statusFilter) {
        final Integer count =coverageResults.size();

        return new Iterator<CoverageResult>() {
            Integer pageSize = 1000;
            Integer nextElement = 0;

            Iterator<CoverageResult> currentResultIterator;

            {
                this.updateIterator();
            }

            @Override
            public boolean hasNext() {
                return (currentResultIterator.hasNext() || nextElement < count);
            }

            @Override
            public CoverageResult next() {
                if (currentResultIterator.hasNext()) {
                    return currentResultIterator.next();
                } else if (this.hasNext()) {
                    updateIterator();
                    return this.next();
                } else {
                    return null;
                }
            }

            private void updateIterator() {
                Integer last = Math.min(nextElement + pageSize, count);
                List<String> elementsToRetrieve = coverageResults.subList(nextElement, last);
                currentResultIterator = getEntries(elementsToRetrieve, geneFilter, statusFilter).iterator();
                nextElement = nextElement + pageSize;
            }

        };
    }

    private List<CoverageResult> getEntries(List<String> elementsToRetrieve, String geneFilter, String statusFilter) {

        List<CoverageResult> result = new LinkedList<>();
        coverageCollection.aggregate(getAggregationSteps(elementsToRetrieve, geneFilter, statusFilter), CoverageResult.class).forEach(
                (Consumer<CoverageResult>) result::add
        );
        
        result.sort(Comparator.comparingInt((CoverageResult v) -> elementsToRetrieve.indexOf(v.getId())));

        return result;
    }

    private List<Bson> getAggregationSteps(List<String> elementsToRetrieve, String geneFilter, String statusFilter) {


        List<Bson> aggregations = new LinkedList();
        
        List<Bson> matchConditions = new LinkedList<>();
        matchConditions.add(in(MongoUtils.ID,  elementsToRetrieve.stream().map( ObjectId::new).collect(Collectors.toList())));
        if (geneFilter!=null && !geneFilter.isEmpty()){
            matchConditions.add(regex(CoverageEntry.GENE_NAMES, geneFilter, "i"));
        }
        if (statusFilter!=null && !statusFilter.isEmpty()){
            matchConditions.add(regex(CoverageEntry.MAPPING_STATUS, statusFilter, "i"));
        }
        
        Bson query = and(matchConditions);
        aggregations.add(match(query));


        aggregations.add(project(fields(include(CoverageEntry.CHROM, CoverageEntry.START, CoverageEntry.END, CoverageEntry.GENE_NAMES, CoverageEntry.MAPPING_STATUS))));

        return aggregations;
    }

}
