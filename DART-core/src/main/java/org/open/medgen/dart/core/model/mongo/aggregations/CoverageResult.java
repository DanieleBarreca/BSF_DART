package org.open.medgen.dart.core.model.mongo.aggregations;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.open.medgen.dart.core.controller.utils.VCFFieldsUtils;
import org.open.medgen.dart.core.model.mongo.coverage.CoverageEntry;

import java.io.Serializable;
import java.util.Set;

public class CoverageResult implements Serializable {

    @BsonId
    private ObjectId id;

    @BsonProperty(CoverageEntry.CHROM)
    private String chrom;

    @BsonProperty(CoverageEntry.START)
    private int start;

    @BsonProperty(CoverageEntry.END)
    private int end;

    @BsonProperty(CoverageEntry.GENE_NAMES)
    private Set<String> genes;

    @BsonProperty(CoverageEntry.MAPPING_STATUS)
    private CoverageEntry.MappingStatus mappingStatus;


    public ObjectId getId() {
        return id;
    }

    @JsonProperty("REF_ID")
    @BsonIgnore
    public String getDBIdString() {
        if (id==null) return null;
        return id.toHexString();
    }

    public CoverageResult() {
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public Set<String> getGenes() {
        return genes;
    }

    public void setGenes(Set<String> genes) {
        this.genes = genes;
    }

    public CoverageEntry.MappingStatus getMappingStatus() {
        return mappingStatus;
    }

    public void setMappingStatus(CoverageEntry.MappingStatus mappingStatus) {
        this.mappingStatus = mappingStatus;
    }
}
