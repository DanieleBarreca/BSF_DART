package org.open.medgen.dart.core.model.mongo.coverage;

import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.Indexes;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.open.medgen.dart.core.model.mongo.MongoModel;
import org.open.medgen.dart.core.model.mongo.variant.Zygosity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CoverageEntry extends MongoModel {

    public enum MappingStatus {
        LOW_COVERAGE,
        NO_COVERAGE,
        POOR_MAPPING_QUALITY;

        public static MappingStatus decode(String mappingStatus){
            for (MappingStatus mappingStatusCat: MappingStatus.values()){
                if (mappingStatusCat.toString().equalsIgnoreCase(mappingStatus)){
                    return mappingStatusCat;
                }
            }

            return null;
        }

        public static List<String> getValues(){
            List<String> result = new LinkedList<>();

            for (Zygosity label: Zygosity.values()){
                result.add(label.toString());
            }

            return result;
        }

    }

    private static final long serialVersionUID = 1L;

    private final static String COLLECTION = "CoverageEntry";

    public final static String COVERAGE_FILE = "coverageFile";

    public final static String CHROM = "chrom";
    public final static String BUCKET = "bucket";
    public final static String START = "start";
    public final static String END = "end";
    public final static String MAPPING_STATUS = "mapping_status";
    public final static String GENE_IDS = "gene_ids";
    public final static String GENE_NAMES = "gene_names";
    public final static String TRANSCRIPT_IDS = "transcript_ids";
    public final static String EXON_IDS = "exon_ids";
    public final static String PROBE_NAME = "probe_name";

    private final static List<IndexModel> indexes = Arrays.asList(
            new IndexModel(Indexes.ascending(COVERAGE_FILE)),
            new IndexModel(Indexes.ascending(GENE_IDS))
    );

    @BsonId
    private ObjectId id;
    
    private ObjectId coverageFileId;

    @BsonProperty(CHROM)
    private String chrom;

    @BsonProperty(START)
    private int start;

    @BsonProperty(END)
    private int end;

    @BsonProperty(BUCKET)
    private String bucket;

    @BsonProperty(MAPPING_STATUS)
    private MappingStatus mappingStatus;

    @BsonProperty(GENE_IDS)
    private List<String> geneIds;

    @BsonProperty(GENE_NAMES)
    private List<String> geneNames;

    @BsonProperty(TRANSCRIPT_IDS)
    private List<String> transcriptIds;

    @BsonProperty(EXON_IDS)
    private List<String> exonIds;

    @BsonProperty(PROBE_NAME)
    private String probeName=null;

    public CoverageEntry(){}

    public CoverageEntry(ObjectId coverageFileId, String chrom, int start, int end, String mappingStatus, String bucket) {
        this.coverageFileId = coverageFileId;
        this.chrom = chrom;
        this.start = start;
        this.end = end;
        this.mappingStatus = MappingStatus.decode(mappingStatus);
        this.bucket = chrom + "-"+bucket;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getCoverageFile() {
        return coverageFileId;
    }

    public void setCoverageFile(ObjectId coverageFileId) {
        this.coverageFileId = coverageFileId;
    }

    public String getChrom() {
        return chrom;
    }

    public int getStart() {
        return start;
    }


    public int getEnd() {
        return end;
    }

    public String getBucket() {return bucket;}

    public MappingStatus getMappingStatus() {
        return mappingStatus;
    }



    public List<String> getGeneIds() {
        return geneIds;
    }

    public void setGeneIds(List<String> geneIds) {
        this.geneIds = geneIds;
    }

    public List<String> getGeneNames() {
        return geneNames;
    }

    public void setGeneNames(List<String> geneNames) {
        this.geneNames = geneNames;
    }

    public List<String> getTranscriptIds() {
        return transcriptIds;
    }

    public void setTranscriptIds(List<String> transcriptIds) {
        this.transcriptIds = transcriptIds;
    }

    public List<String> getExonIds() {
        return exonIds;
    }

    public void setExonIds(List<String> exonIds) {
        this.exonIds = exonIds;
    }

    public String getProbeName() {
        return probeName;
    }

    public void setProbeName(String probeName) {
        this.probeName = probeName;
    }

    @Override
    public List<IndexModel> getIndexes() {
        return indexes;
    }

    @Override
    public String getCollectionName() {
        return COLLECTION;
    }
}
