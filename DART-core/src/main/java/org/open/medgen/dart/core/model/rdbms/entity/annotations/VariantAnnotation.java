package org.open.medgen.dart.core.model.rdbms.entity.annotations;

import org.open.medgen.dart.core.model.mongo.variant.VariantModel;
import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


@Entity
@Table(name = "variant_annotation")
public class VariantAnnotation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "annotation_id")
    private Integer dbId;

    @JoinColumn(name = "variant_id", referencedColumnName = "variant_id")
    @ManyToOne(optional = false)
    private  Variant variant;

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

    @JoinColumn(name = "annotation", referencedColumnName = "db_id")
    @ManyToOne(optional = false)
    private AnnotationDictionary annotation;

    @JoinColumn(name = "health_condition", referencedColumnName = "db_id")
    @ManyToOne(optional = false)
    private ConditionDictionary condition;

    @JoinColumn(name = "inheritance_model", referencedColumnName = "db_id")
    @ManyToOne(optional = false)
    private InheritanceDictionary inheritance;

    @Lob
    @Column(name="supporting_data",length=16777216)
    @Basic(fetch = FetchType.LAZY)
    private VariantModel supportingData;

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
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

    public AnnotationDictionary getAnnotation() {
        return annotation;
    }

    public void setAnnotation(AnnotationDictionary annotation) {
        this.annotation = annotation;
    }

    public ConditionDictionary getCondition() {
        return condition;
    }

    public void setCondition(ConditionDictionary condition) {
        this.condition = condition;
    }

    public InheritanceDictionary getInheritance() {
        return inheritance;
    }

    public void setInheritance(InheritanceDictionary inheritance) {
        this.inheritance = inheritance;
    }

    public VariantModel getSupportingData() {
        return supportingData;
    }

    public void setSupportingData(VariantModel supportingData) {
        this.supportingData = supportingData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariantAnnotation that = (VariantAnnotation) o;
        return dbId.equals(that.dbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbId);
    }
}

