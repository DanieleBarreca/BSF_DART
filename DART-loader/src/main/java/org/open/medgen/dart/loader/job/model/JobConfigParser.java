package org.open.medgen.dart.loader.job.model;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JobConfigParser {

    protected final static String TEMPLATE_KEY = "*";
    private final static String TEMPLATE_SAMPLE_VARIABLE = "{sample}";

    private Map<String, SampleConfig> configMap = new HashMap<>();
    private boolean hasTemplates = false;

    public enum JobConfigHeader {
        SAMPLE,
        SAMPLE_ALIAS,
        INCLUDE,
        VCF_URL,
        BAM_URL,
        COVERAGE_TRACK_URL,
        COVERAGE_FILE
    }

    public static class SampleConfig {
        private String  sample = null;
        private String sampleAlias = null;
        private Boolean include = false;
        private String vcfUrl = null;
        private String bamUrl = null;
        private String coverageTrackUrl = null;
        private String coverageFile = null;

        public SampleConfig(CSVRecord record) {
            this.sample = getFieldOrNull(record, JobConfigHeader.SAMPLE);
            if (!(TEMPLATE_KEY.equals(this.sample))){
                this.sampleAlias = getFieldOrNull(record, JobConfigHeader.SAMPLE_ALIAS);
            }
            String include = getFieldOrNull(record, JobConfigHeader.INCLUDE);
            if (include!=null){
                this.include = include.equalsIgnoreCase("t") |
                        include.equalsIgnoreCase("true") |
                        include.equalsIgnoreCase("x");
            }

            if (this.include) {
                this.vcfUrl = getFieldOrNull(record, JobConfigHeader.VCF_URL);
                this.bamUrl = getFieldOrNull(record, JobConfigHeader.BAM_URL);
                this.coverageTrackUrl = getFieldOrNull(record, JobConfigHeader.COVERAGE_TRACK_URL);
                this.coverageFile = getFieldOrNull(record, JobConfigHeader.COVERAGE_FILE);
            }
        }

        public SampleConfig(String sample, String sampleAlias, String vcfUrl, String bamUrl, String coverageTrackUrl, String coverageFile){
            this.include=true;
            this.sample = sample;
            this.sampleAlias = sampleAlias;
            this.vcfUrl = vcfUrl;
            this.bamUrl = bamUrl;
            this.coverageTrackUrl = coverageTrackUrl;
            this.coverageFile = coverageFile;
        }

        private String getFieldOrNull(CSVRecord record, JobConfigHeader field) {
            String fieldValue = record.get(field);
            return (fieldValue!=null & !fieldValue.trim().isEmpty()) ? fieldValue.trim() : null;
        }

        public String getSample() {
            return sample;
        }

        public Boolean getInclude() {
            return include;
        }

        public String getVcfUrl() {
            return vcfUrl;
        }

        public String getBamUrl() {
            return bamUrl;
        }

        public String getCoverageTrackUrl() {
            return coverageTrackUrl;
        }

        public String getCoverageFile() {
            return coverageFile;
        }

        public String getSampleAlias() {
            return sampleAlias;
        }

        @Override
        public String toString() {
            return "SampleConfig{" +
                    "sample='" + sample + '\'' +
                    ", sampleAlias='" + sampleAlias + '\'' +
                    ", include=" + include +
                    ", vcfUrl='" + vcfUrl + '\'' +
                    ", bamUrl='" + bamUrl + '\'' +
                    ", coverageTrackUrl='" + coverageTrackUrl + '\'' +
                    ", coverageFile='" + coverageFile + '\'' +
                    '}';
        }
    }

    public JobConfigParser(File jobConfigFile) throws IOException {
        load(jobConfigFile);
    }

    public JobConfigParser() {

    }

    protected Map<String, SampleConfig> getConfigMap() {
        return this.configMap;
    }

    /**
     * Add {@link SampleConfig} to collection.
     * If sample name is the {@link #TEMPLATE_KEY}, all other samples are removed.
     * @param sampleConfig
     */
    public void addSample(SampleConfig sampleConfig) {
        if (sampleConfig.sample.equalsIgnoreCase(TEMPLATE_KEY)) {
            hasTemplates = true;
            configMap.clear();
            configMap.put(sampleConfig.sample, sampleConfig);
        } else {
            if (sampleConfig.include) {
                configMap.put(sampleConfig.sample, sampleConfig);
            }
        }

        System.out.println("Sample Included: "+sampleConfig.toString());
    }

    /**
     * Load configuration file. If File is NULL, a template is set by default.
     * @param jobConfigFile
     * @throws IOException
     */
    public void load(File jobConfigFile) throws IOException {
        if (jobConfigFile != null) {
            try {
                InputStream inputStream = new FileInputStream(jobConfigFile);
                Iterable<CSVRecord> records = CSVFormat.RFC4180
                        .withDelimiter('\t')
                        .withHeader(JobConfigHeader.class)
                        .withSkipHeaderRecord()
                        .parse(new InputStreamReader(inputStream));
                for (CSVRecord record : records) {
                    try {
                        addSample(new SampleConfig(record));
                        // Stop if template was used
                        if (hasTemplates) {
                            break;
                        }
                    } catch (Exception e) {
                        System.err.print(e.getMessage());
                    }
                }

            } catch (Exception e) {
                throw new IOException("Could not load job config file " + jobConfigFile.getAbsolutePath(), e);
            }
        } else {
            addSample(new SampleConfig(TEMPLATE_KEY,null,null,null,null,null));
        }
    }

    public SampleConfig getSampleConfig(String sampleName) {
        if (hasTemplates){
            SampleConfig template = configMap.get(TEMPLATE_KEY);
            return new SampleConfig(
                    sampleName,
                    null,
                    template.getVcfUrl() != null ? template.getVcfUrl().replace(TEMPLATE_SAMPLE_VARIABLE,sampleName) : null,
                    template.getBamUrl() != null ? template.getBamUrl().replace(TEMPLATE_SAMPLE_VARIABLE,sampleName) : null,
                    template.getCoverageTrackUrl() != null ? template.getCoverageTrackUrl().replace(TEMPLATE_SAMPLE_VARIABLE,sampleName) : null,
                    template.getCoverageFile() != null ? template.getCoverageFile().replace(TEMPLATE_SAMPLE_VARIABLE,sampleName) : null
            );
        }else{
            return configMap.get(sampleName);
        }
    }
}
