package org.open.medgen.dart.core.model.rdbms.entity.annotations;

import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.enums.ValidationStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "variant_sample_annotation")
public class VariantSampleAnnotation {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "annotation_id")
    private Integer dbId;

    @JoinColumn(name = "variant_sample_id", referencedColumnName = "variant_sample_id")
    @ManyToOne(optional = false)
    private VariantSample variantSample;

    @JoinColumn(name = "group_id", referencedColumnName = "group_id")
    @ManyToOne(optional = false)
    private UserGroup group;

    @JoinColumn(name = "user_from", referencedColumnName = "userId")
    @ManyToOne(optional = false)
    private User userFrom;

    @JoinColumn(name = "user_to", referencedColumnName = "userId")
    @ManyToOne()
    private User userTo;

    @Basic(optional = false)
    @Column(name = "date_from")
    private Date dateFrom;

    @Basic(optional = true)
    @Column(name = "date_to")
    private Date dateTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "annotation")
    private ValidationStatus validationStatus;

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public VariantSample getVariantSample() {
        return variantSample;
    }

    public void setVariantSample(VariantSample variantSample) {
        this.variantSample = variantSample;
    }

    public UserGroup getGroup() {
        return group;
    }

    public void setGroup(UserGroup group) {
        this.group = group;
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

    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(ValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariantSampleAnnotation that = (VariantSampleAnnotation) o;
        return dbId.equals(that.dbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbId);
    }
}

