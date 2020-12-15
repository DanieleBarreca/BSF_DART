/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.mongo.bed;

import org.open.medgen.dart.core.model.mongo.MongoModel;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.Indexes;
import java.util.Arrays;
import java.util.List;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;


/**
 *
 * @author dbarreca
 */
/*@Indexes({
    @Index(fields={@Field(value="bedFile")}),
    @Index(fields={@Field(value="lookupKey")})
})*/
public class BedEntry extends MongoModel {
    private static final long serialVersionUID = 1L;
     
    private final static String COLLECTION = "BedEntry";
    
    public final static String BED_FILE = "bedFile";
    public final static String CHROM = "chrom";
    public final static String LOOKUP_KEY = "lookupKey";
    public final static String START = "start";
    public final static String END = "end";

    private final static List<IndexModel> indexes = Arrays.asList(
            new IndexModel(Indexes.ascending(LOOKUP_KEY)), 
            new IndexModel(Indexes.ascending(BED_FILE))
    );
    
    @BsonId
    private ObjectId bedFileId;
    
    @BsonProperty(CHROM)
    private String chrom;
    
    @BsonProperty(START)
    private int start;
    
    @BsonProperty(END)
    private int end;
    
    @BsonProperty(LOOKUP_KEY)
    private String lookupKey;

    public BedEntry(){}
    
    public BedEntry(ObjectId bedFileId, String chrom, int start, int end, String bucket) {
        this.bedFileId = bedFileId;
        this.chrom = chrom;
        this.start = start;
        this.end = end;
        this.lookupKey = bedFileId.toHexString() + "-" + chrom + "-"+bucket;
    }

    public String getChrom() {
        return chrom;
    }

    public void setChrom(String chrom) {
        this.chrom = chrom;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public ObjectId getBedFile() {
        return bedFileId;
    }

    public void setBedFile(ObjectId bedFile) {
        this.bedFileId = bedFile;
    }
    
    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }

    @Override
    @BsonIgnore
    public List<IndexModel> getIndexes() {
        return indexes;
    }

    @Override
    @BsonIgnore
    public String getCollectionName() {
        return COLLECTION;
    }
    
}
