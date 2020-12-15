/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.service.mongo.service;

import org.open.medgen.dart.core.model.mongo.bed.BedEntry;
import org.open.medgen.dart.core.service.mongo.provider.AsyncCollectionFactory;
import com.mongodb.async.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.result.DeleteResult;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.bson.types.ObjectId;

/**
 *
 * @author dbarreca
 */
@Stateless
public class AsyncBEDService {

    @Inject
    private AsyncCollectionFactory collectionFactory;

    private MongoCollection<BedEntry> bedCollection;

    private final static Logger LOGGER = Logger.getLogger(AsyncBEDService.class.getName());

    @PostConstruct
    private void startup() {
        bedCollection = collectionFactory.getCollection(BedEntry.class);

        LOGGER.info("Initializing BedService...");
    }

    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutting down BedService...");

    }

    public void saveBEDEntry(List<BedEntry> entries, Runnable onSuccess, Consumer<Throwable> onError) {

        bedCollection.insertMany(entries, (Void theVoid, Throwable t) -> {
            if (t != null) {
                onError.accept(t);
            } else {
                onSuccess.run();
            }
        });
    }
    
    public void removeBEDEntries(ObjectId id, Runnable onSuccess, Consumer<Throwable> onError) {

        bedCollection.deleteMany(eq(BedEntry.BED_FILE,id), (DeleteResult result, Throwable t) -> {
            if (t != null) {
                onError.accept(t);
            } else {
                LOGGER.info("Deleted "+result.getDeletedCount()+" entries with bed file id "+id.toHexString());
                onSuccess.run();
            }
        });
    }
}
