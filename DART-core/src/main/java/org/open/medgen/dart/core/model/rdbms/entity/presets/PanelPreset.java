/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.open.medgen.dart.core.model.rdbms.entity.presets;

import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.bed.BedFile;
import java.io.Serializable;
import java.util.*;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author dbarreca
 */
@Entity
@Table(name = "panel_presets")
public class PanelPreset implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)    
    @Basic(optional = false)
    @Column(name="panel_id")
    private Integer panelId;

    @JoinColumn(name = "group_id", referencedColumnName = "group_id")
    @ManyToOne(optional = false)
    private UserGroup userGroup;

    @Column(name="panel_mnemonic")
    @Size(max=500)
    private String mnemonic;
    
    @JoinColumn(name = "bed_id", referencedColumnName = "bed_id")
    @ManyToOne(optional = true)
    private BedFile bedFile;  
    
    @NotNull
    @Column(name="genes_hash")
    private Integer genesHash;
    
    @Column(name="genes",length=65535)
    private String genes;

    @JoinColumn(name = "user_from", referencedColumnName = "userId")
    @ManyToOne(optional = false)
    private User userFrom;

    @JoinColumn(name = "user_to", referencedColumnName = "userId")
    @ManyToOne()
    private User userTo;

    @Basic(optional = false)
    @Column(name = "date_from")
    private Date dateFrom = new Date();

    @Basic(optional = true)
    @Column(name = "date_to")
    private Date dateTo;

    public Integer getPanelId() {
        return panelId;
    }

    public void setPanelId(Integer panelId) {
        this.panelId = panelId;
    }

    public BedFile getBedFile() {
        return bedFile;
    }

    public void setBedFile(BedFile bedFile) {
        this.bedFile = bedFile;
    }

    public Integer getGenesHash() {
        return genesHash;
    }

    public void setGenesHash(Integer genesHash) {
        this.genesHash = genesHash;
    }

    public String getGenes() {
        return genes;
    }

    public void setGenes(String genes) {
        this.genes = genes;
    }

    
    public Set<String> getGenesSet() {
        if (this.genes == null || this.genes.isEmpty()){
            return new HashSet();
        }else{
            return new HashSet(Arrays.asList(genes.split(",")));
        }
    }

    public void setGenesSet(Set<String> genes) {
        if (genes!=null && !genes.isEmpty()){
            this.genes = "";

            boolean first = true;
            for (String gene: genes){
                if (gene!=null && !gene.isEmpty()){
                    if (first) {
                        first = false;
                    }else{
                        this.genes +=",";
                    }
                    this.genes += gene;                  
                }
            }
        }else{
            this.genes = null;
        }
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public User getUserTo() {
        return userTo;
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.panelId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PanelPreset other = (PanelPreset) obj;
        if (!Objects.equals(this.panelId, other.panelId)) {
            return false;
        }
        return true;
    }

    
    
}
