package org.open.medgen.dart.core.model.rdbms.entity.annotations;

import org.open.medgen.dart.core.model.rdbms.entity.User;
import org.open.medgen.dart.core.model.rdbms.entity.UserGroup;
import org.open.medgen.dart.core.model.rdbms.entity.vcf.VCFSample;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "sample_condition")
public class SampleCondition {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "annotation_id")
    private Integer dbId;

    @JoinColumn(name = "sample_id", referencedColumnName = "sample_id")
    @ManyToOne(optional = false)
    private VCFSample sample;

    @JoinColumn(name = "medical_condition", referencedColumnName = "db_id")
    @ManyToOne(optional = false)
    private ConditionDictionary condition;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleCondition that = (SampleCondition) o;
        return dbId.equals(that.dbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbId);
    }

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public VCFSample getSample() {
        return sample;
    }

    public void setSample(VCFSample sample) {
        this.sample = sample;
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
    

    public ConditionDictionary getCondition() {
        return condition;
    }

    public void setCondition(ConditionDictionary condition) {
        this.condition = condition;
    }
}