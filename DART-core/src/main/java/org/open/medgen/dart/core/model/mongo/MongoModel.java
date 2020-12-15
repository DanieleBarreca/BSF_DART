/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.client.model.IndexModel;
import java.io.Serializable;
import java.util.List;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.codecs.pojo.annotations.BsonIgnore;

/**
 *
 * @author dbarreca
 */
public abstract class MongoModel implements Serializable{
    
    @JsonIgnore
    @BsonIgnore
    public abstract List<IndexModel> getIndexes();
    
    @JsonIgnore
    @BsonIgnore
    public abstract String getCollectionName();
    
    @JsonIgnore
    @BsonIgnore
    public CodecRegistry getRegistry(){
        return fromRegistries(
            com.mongodb.MongoClient.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
    }
}
