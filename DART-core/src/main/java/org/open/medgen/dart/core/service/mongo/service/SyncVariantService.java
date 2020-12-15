/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.service;

import org.open.medgen.dart.core.controller.annotation.Annotator;
import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.service.mongo.provider.SyncCollectionFactory;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.MongoCollection;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import org.bson.types.ObjectId;

/**
 *
 * @author dbarreca
 */
@Stateless
public class SyncVariantService {

    private final static Logger LOGGER = Logger.getLogger(SyncVariantService.class.getName());

    @Inject
    SyncCollectionFactory collectionFactory;

    MongoCollection<VariantModel> variantCollection;

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing SyncVariantService...");
        variantCollection = collectionFactory.getCollection(VariantModel.class);
    }
    
    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutting down AsyncVariantService...");

    }
   
    public void saveVariants(List<VariantModel> variants) {        
        variantCollection.insertMany(variants);
    }

    public Iterator<VariantModel> getVariantsIterator(List<String> variantResults, Annotator variantAnnotator) {
        final Integer count =variantResults.size();

        return new Iterator<VariantModel>() {
            Integer pageSize = 1000;
            Integer nextElement = 0;

            Iterator<VariantModel> currentResultIterator;

            {
                this.updateIterator();
            }

            @Override
            public boolean hasNext() {
                return (currentResultIterator.hasNext() || nextElement < count);
            }

            @Override
            public VariantModel next() {
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
                List<String> variantsToRetrieve = variantResults.subList(nextElement, last);

                currentResultIterator = getVariants(variantsToRetrieve,variantAnnotator).iterator();
                nextElement = nextElement + pageSize;
            }

        };
    }

    private List<VariantModel> getVariants(List<String> variantsToRetrieve, Annotator variantAnnotator) {
        List<VariantModel> result = new LinkedList<>();
        
        variantCollection.find(in(MongoUtils.ID, variantsToRetrieve.stream().map( ObjectId::new).collect(Collectors.toList()))).forEach(
                (Consumer<VariantModel>) variantModel -> {
                    if (variantAnnotator!=null){
                        variantAnnotator.annotate(variantModel);
                    }
                    result.add(variantModel);
                }
        );

        result.sort(Comparator.comparingInt((VariantModel v) -> variantsToRetrieve.indexOf(v.getId())));

        return result;
    }

    public VariantModel getVariant(String variantToRetrieve) {

        VariantModel result = variantCollection.find(eq(MongoUtils.ID,new ObjectId(variantToRetrieve))).first();

        return result;
    }

}
