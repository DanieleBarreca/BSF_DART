package org.open.medgen.dart.loader.job.model;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.open.medgen.dart.core.controller.bed.BEDParser;
import org.open.medgen.dart.core.model.mongo.coverage.CoverageEntry;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CoverageFileParser {
    private final File coverageFile;

    private List<CoverageEntry> entries = new LinkedList<>();

    public enum CoverageFileHeader {
        seqnames,
        start,
        end,
        width,
        strand,
        mapping_status,
        gene_ids,
        gene_names,
        transcript_ids,
        transcript_names,
        exon_ids
    }

    public CoverageFileParser(String coverageFile) throws FileNotFoundException {
        this(new File(coverageFile));
    }

    public CoverageFileParser(File coverageFile) throws FileNotFoundException {
        this.coverageFile = coverageFile;
        if (!this.coverageFile.exists()){
            throw new FileNotFoundException(String.format("Sample coverage file %s not found",this.coverageFile.getAbsolutePath()));
        }else if (!this.coverageFile.canRead()){
            throw new FileNotFoundException(String.format("Sample coverage file %s not readable",this.coverageFile.getAbsolutePath()));
        }
    }


    public List<CoverageEntry> parse() throws  IOException {
        if (coverageFile != null) {
            try {
                InputStream inputStream = new FileInputStream(coverageFile);
                Iterable<CSVRecord> records = CSVFormat.RFC4180
                        .withDelimiter('\t')
                        .withQuote('"')
                        .withHeader(CoverageFileHeader.class)
                        .withSkipHeaderRecord()
                        .parse(new InputStreamReader(inputStream));
                for (CSVRecord record : records) {
                    try {
                        entries.addAll(parseRecord(record));
                    } catch (Exception e) {
                        System.err.print(e.getMessage());
                    }
                }

            } catch (Exception e) {
                throw new IOException("Could not load job config file " + coverageFile.getAbsolutePath(), e);
            }
        }

        return entries;
    }

    public void clear(){
        entries.clear();
    }

    private List<CoverageEntry> parseRecord(CSVRecord record){

        String chrom = record.get(CoverageFileHeader.seqnames);
        Integer start = Integer.parseInt(record.get(CoverageFileHeader.start));
        Integer end = Integer.parseInt(record.get(CoverageFileHeader.end));



        Integer bucket = (int) (1.0*start/ BEDParser.BUCKET_SIZE);
        Integer finalBucket = (int) (1.0*end/BEDParser.BUCKET_SIZE);

        LinkedList<CoverageEntry> results = new LinkedList<>();
        while (bucket<=finalBucket){
            Integer bucketEnd = (bucket+1)*BEDParser.BUCKET_SIZE-1;
            if (bucketEnd<end){
                results.add(getEntry(chrom,start,bucketEnd,String.valueOf(bucket),record));
                start=bucketEnd+1;

            } else {
                results.add(getEntry(chrom,start,end,String.valueOf(bucket),record));
                break;
            }
            bucket = bucket+1;
        }

        return results;

    }

    private CoverageEntry getEntry(String chrom, Integer start, Integer end, String bucket, CSVRecord record) {
        String mappingStatus = record.get(CoverageFileHeader.mapping_status);
        List<String> geneNames = Arrays.asList(record.get(CoverageFileHeader.gene_names).split(","));
        List<String> geneIds = Arrays.asList(record.get(CoverageFileHeader.gene_ids).split(","));
        List<String> transcripts = Arrays.asList(record.get(CoverageFileHeader.transcript_ids).split(","));
        List<String> exons = Arrays.asList(record.get(CoverageFileHeader.exon_ids).split(","));

        CoverageEntry result =  new CoverageEntry(null,chrom, start,end, mappingStatus, String.valueOf(bucket));
        result.setGeneIds(geneIds);
        result.setGeneNames(geneNames);
        result.setTranscriptIds(transcripts);
        result.setExonIds(exons);

        return result;

    }
}
