/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.controller.bed;

import org.open.medgen.dart.core.model.mongo.bed.BedEntry;
import java.util.List;
import org.bson.types.ObjectId;

/**
 *
 * @author dbarreca
 */
public class ParsedBed {
    
    private final List<BedEntry> entries;
    private final String md5;
    private final ObjectId mongoID;
    
    public ParsedBed( List<BedEntry> entries, String md5, ObjectId mongoID) {
        this.entries = entries;
        this.md5 = md5;
        this.mongoID = mongoID;
    }

    public List<BedEntry> getEntries() {
        return entries;
    }

    public String getMd5() {
        return md5;
    }

    public ObjectId getMongoID() {
        return mongoID;
    }
    
    
    
    
    
}
