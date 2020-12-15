package org.open.medgen.dart.core.model.rdbms.entity.annotations;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "variant")
public class Variant {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "variant_id")
    private Integer dbId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "ref_genome")
    private String refGenome;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 450)
    @Column(name = "HGVSg")
    private String hgvsG;

    @Basic(optional = true)
    @Size(min = 1, max = 450)
    @Column(name = "HGVSc")
    private String hgvsC;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "contig")
    private String contig;

    @Basic(optional = false)
    @NotNull
    @Column(name = "pos")
    private Integer pos;

    @Basic(optional = false)
    @Size(min = 1, max = 45)
    @Column(name = "gene")
    private String gene;

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public String getRefGenome() {
        return refGenome;
    }

    public void setRefGenome(String refGenome) {
        this.refGenome = refGenome;
    }

    public String getHgvsG() {
        return hgvsG;
    }

    public void setHgvsG(String hgvsG) {
        this.hgvsG = hgvsG;
    }

    public String getHgvsC() {
        return hgvsC;
    }

    public void setHgvsC(String hgvsC) {
        this.hgvsC = hgvsC;
    }

    public String getContig() {
        return contig;
    }

    public void setContig(String contig) {
        this.contig = contig;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public String getGene() {
        return gene;
    }

    public void setGene(String gene) {
        this.gene = gene;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variant variant = (Variant) o;
        return dbId.equals(variant.dbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbId);
    }
}
