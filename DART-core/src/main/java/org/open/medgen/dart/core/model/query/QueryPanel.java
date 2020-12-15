package org.open.medgen.dart.core.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.open.medgen.dart.core.model.rdbms.entity.enums.ValidationStatus;
import org.open.medgen.dart.core.model.rdbms.entity.presets.PanelPreset;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Embeddable
public class QueryPanel implements Serializable {

    private static final long serialVersionUID = 1L;

    private final static String REF_ID = "REF_ID";
    private final static String MNEMONIC = "MNEMONIC";
    private final static String BED_REF_ID = "BED_REF_ID";
    private final static String BED_NAME = "BED_NAME";
    private final static String GENES = "GENES";
    private final static String VALID = "VALID";

    @Transient
    @JsonProperty(REF_ID)
    private Integer refId = null;

    @Transient
    @JsonProperty(MNEMONIC)
    private String mnemonic = null;

    @Transient
    @JsonProperty(BED_REF_ID)
    private Integer bedFileId = null;

    @Transient
    @JsonProperty(BED_NAME)
    private String bedFileName;

    @Transient
    @JsonProperty(VALID)
    private boolean valid = true;

    @Transient
    @JsonProperty(GENES)
    private Set<String> geneList = new HashSet<>();

    public QueryPanel() {
    }

    public QueryPanel(PanelPreset panelPreset) {
        if (panelPreset != null) {
            this.refId = panelPreset.getPanelId();
            this.mnemonic = panelPreset.getMnemonic();
            if (panelPreset.getBedFile() != null) {
                this.bedFileId = panelPreset.getBedFile().getBedId();
                this.bedFileName = panelPreset.getBedFile().getFileName();
            }

            this.geneList = panelPreset.getGenesSet();
            
            if (panelPreset.getDateTo() != null) {
                this.valid = false;
            }
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public Integer getBedFileId() {
        return bedFileId;
    }

    public void setBedFileId(Integer bedFileId) {
        this.bedFileId = bedFileId;
    }

    public String getBedFileName() {
        return bedFileName;
    }

    public void setBedFileName(String bedFileName) {
        this.bedFileName = bedFileName;
    }

    public Set<String> getGeneList() {
        return geneList;
    }

    public void setGeneList(Set<String> geneList) {
        if (geneList != null) {
            this.geneList = geneList;
        }
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryPanel that = (QueryPanel) o;

        if (!Objects.equals(this.bedFileId, that.bedFileId)) {
            return false;
        }
        if (!Objects.equals(this.geneList, that.geneList)) {
            return false;
        }

        return true;

    }

    @Override
    public int hashCode() {
        return Objects.hash(bedFileId, geneList);
    }
}
