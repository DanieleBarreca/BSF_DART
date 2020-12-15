/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.mongo.variant;

import org.open.medgen.dart.core.model.mongo.MongoModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.Indexes;

import java.util.*;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;


/**
 *
 * @author dbarreca
 */
public class VariantModel extends MongoModel {
    
    private static final long serialVersionUID = 1L;
 
    private final static String COLLECTION = "VariantModel";
    
    public final static String VCF_ID = "VCF_FILE";
    public final static String CHROM = "CHROM";
    public final static String POS = "POS";
    public final static String IDS = "IDS";
    public final static String QUAL = "QUAL";
    public final static String FILTER = "FILTER";
    public final static String INFO = "INFO";
    public final static String SAMPLE = "SAMPLE";
    public final static String REF_ALLELE= "REF_ALLELE";
    public final static String ALT_ALLELE= "ALLELE";
    public final static String TRANSCRIPT = "TRANSCRIPT";
    public final static String TRANSCRIPT_FEATURE = TRANSCRIPT+".CSQ:Feature";
    public final static String TRANSCRIPT_PROTEIN_START = TRANSCRIPT+".CSQ:Protein_position_start";
    public final static String TRANSCRIPT_IMPACT = TRANSCRIPT+".CSQ:IMPACT";
    public final static String SAMPLE_NAME = SAMPLE+"." +SampleModel.SAMPLE_NAME;
    public final static String ZYGOSITY = SAMPLE+"." +SampleModel.SAMPLE_GENOTYPE+"."+GenotypeModel.ZYGOSITY;

    public final static String VARIANT_ID = "VARIANT_ID";
    public final static String OTHER_SAMPLES = "OTHER_SAMPLES";
    
    public final static String GENE_FIELD_NAME = "INTERNAL:GENE";
    public final static String GENE_FIELD = TRANSCRIPT + "." + GENE_FIELD_NAME;
    
    public final static String GENOMIC_CHANGE_FIELD_NAME = "INTERNAL:HGVSg";
    public final static String GENOMIC_CHANGE_FIELD = TRANSCRIPT + "." + GENOMIC_CHANGE_FIELD_NAME;
    
    public final static String CODING_CHANGE_FIELD_NAME = "INTERNAL:HGVSc";
    public final static String CODING_CHANGE_FIELD = TRANSCRIPT + "." + CODING_CHANGE_FIELD_NAME;
    
    public final static String ANNOTATIONS = "ANNOTATIONS";
    
    private final static List<IndexModel> indexes = Arrays.asList(
        new IndexModel(Indexes.ascending(VCF_ID)),
        new IndexModel(Indexes.ascending(SAMPLE_NAME, VCF_ID, CHROM, POS )), 
        new IndexModel(Indexes.ascending(TRANSCRIPT_IMPACT, SAMPLE_NAME, VCF_ID, CHROM, POS )), 
        new IndexModel(Indexes.ascending(GENE_FIELD, SAMPLE_NAME, VCF_ID, CHROM, POS )),
        new IndexModel(Indexes.ascending(GENE_FIELD, TRANSCRIPT_IMPACT, SAMPLE_NAME, VCF_ID, CHROM, POS )),        
        new IndexModel(Indexes.ascending(TRANSCRIPT_FEATURE, VCF_ID))
    );
     
    private ObjectId id;
    
    @BsonProperty(VCF_ID)
    private ObjectId vcfId;    
    
    @BsonProperty(CHROM)
    private  String chromosome;    
    
    @BsonProperty(POS)
    private  Integer position;    
    
    @BsonProperty(IDS)
    private Set<String> ids;
    
    @BsonProperty(QUAL)
    private double quality;    
    
    @BsonProperty(FILTER)
    private Set<String> filter;    
    
    @BsonProperty(VARIANT_ID)
    private String variantId;
    
    @BsonProperty(OTHER_SAMPLES)
    private Set<String> otherSamples;

    @BsonIgnore
    @JsonProperty(ANNOTATIONS)
    private AttributeMap annotations = new AttributeMap();

    private final AttributeMap info;    
    private final SampleModel sample;    
    private final AlleleModel refAllele;    
    private final AlleleModel allele;   
    private final AttributeMap transcript;  
    

    public VariantModel() {
        info = new AttributeMap();
        sample = new SampleModel();
        refAllele = new AlleleModel();
        allele = new AlleleModel();
        transcript = new AttributeMap();
    }

    @BsonCreator
    public VariantModel(@BsonProperty(INFO) Map<String, Object> info,
            @BsonProperty(SAMPLE) SampleModel sample,
            @BsonProperty(REF_ALLELE) Map<String, Object> refAllele,
            @BsonProperty(ALT_ALLELE) Map<String, Object> allele,
            @BsonProperty(TRANSCRIPT) Map<String, Object> transcript) {
        this.info = new AttributeMap(info);
        this.sample = sample;
        this.refAllele = new AlleleModel(refAllele);
        this.allele = new AlleleModel(allele);
        this.transcript = new AttributeMap(transcript);
    }
    
    @JsonProperty(SAMPLE)
    @BsonProperty(SAMPLE)
    public SampleModel getSample() {
        return sample;
    }
    
    @JsonProperty(INFO)
    @BsonProperty(INFO)
    public Map<String,Object> getInfoAsMap() {
        return info;
    }

    @JsonIgnore
    @BsonIgnore
    public AttributeMap getInfo() {
        return info;
    }
    
    @JsonProperty(REF_ALLELE)
    @BsonProperty(REF_ALLELE)
    public Map<String,Object> getRefAlleleAsMap() {
        return refAllele;
    }
    
    @JsonIgnore
    @BsonIgnore
    public AlleleModel getRefAllele() {
        return refAllele;
    }

    @JsonProperty(ALT_ALLELE)
    @BsonProperty(ALT_ALLELE)
    public Map<String,Object> getAlleleAsMap() {
        return allele;
    }
    
    @JsonIgnore
    @BsonIgnore
    public AlleleModel getAllele() {
        return allele;
    }
    
    @JsonProperty(TRANSCRIPT)
    @BsonProperty(TRANSCRIPT)
    public Map<String, Object> getTranscriptAsMap() {
        return transcript;
    }
    
    @JsonIgnore
    @BsonIgnore
    public AttributeMap getTranscript() {
        return transcript;
    }
    
    @JsonIgnore
    public ObjectId getId() {
        return id;
    }
    
    @JsonProperty("REF_ID")
    @BsonIgnore
    public String getDBIdString() {
        if (id==null) return null;
        return id.toHexString();
    }
    
    public void setId(ObjectId id) {
        this.id = id;
    }
         
    
    @JsonProperty(CHROM)
    public String getChromosome() {
        return chromosome;
    }
   
    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    @JsonProperty(POS)
    public Integer getPosition() {
        return position;
    }
    
    public void setPosition(Integer position) {
        this.position = position;
    }  

    @JsonIgnore
    public ObjectId getVcfId() {
        return vcfId;
    }

    public void setVcfId(ObjectId vcfId) {
        this.vcfId = vcfId;
    }

    @JsonProperty(VCF_ID)
    @BsonIgnore
    public String getVcfIdString() {
        return vcfId.toHexString();
    } 
    
    @JsonProperty(IDS)
    public Set<String> getIds() {
        return ids;
    }   
   
    public void setIds(Set<String> ids) {
        this.ids = ids;
    }

    @JsonProperty(QUAL)
    public double getQuality() {
        return quality;
    }

    public void setQuality(double quality) {
        this.quality = quality;
    }

    @JsonProperty(FILTER)
    public Set<String> getFilter() {
        return filter;
    }

    public void setFilter(Set<String> filter) {
        this.filter = filter;
    }     
    
    
    @JsonProperty(VARIANT_ID)
    public String getVariantId() {
        return variantId;
    }
       
    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }
    
   @JsonProperty(OTHER_SAMPLES)
   public Set<String> getOtherSamples() {
        return otherSamples;
    }

    public void setOtherSamples(Set<String> otherSamples) {
        this.otherSamples = otherSamples;
    }
   
    public void calculateVariantId(){
        this.variantId =
                chromosome + "-" +
                String.valueOf(position) + "-" +
                refAllele.getAllele() + "-" +
                allele.getAllele();
    }
    
    public Object getAttribute(String attributePath){
        String[] splittedPath = attributePath.split("\\.");
        String firstElement = splittedPath[0];
        String otherElements = (attributePath.contains(".")) ? attributePath.substring(attributePath.indexOf(".")+1) : "";

        switch(firstElement) {
            case CHROM:
                return chromosome;
            case POS:
                return position;
            case IDS:
                return ids;
            case QUAL:
                return quality;
            case OTHER_SAMPLES:
                return otherSamples;
            case FILTER:
                return filter;
            case INFO:
                 return info.get(otherElements);
            case SAMPLE:
                return sample.getAttribute(otherElements);
            case REF_ALLELE:
                return refAllele.get(otherElements);
            case ALT_ALLELE:
                return allele.get(otherElements);
            case TRANSCRIPT:
                return transcript.get(otherElements);
            case ANNOTATIONS:
                return annotations.get(otherElements);
            default:
                return null;
        }
    }
    
    public String getAttributeAsString(String attributePath){
        Object attribute = getAttribute(attributePath);
        return AttributeMap.parseObjectAsString(attribute);
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


    public AttributeMap getAnnotations() {
        return annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariantModel that = (VariantModel) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
