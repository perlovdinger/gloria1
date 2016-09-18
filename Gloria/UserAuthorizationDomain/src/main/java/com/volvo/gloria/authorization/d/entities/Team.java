package com.volvo.gloria.authorization.d.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for Team.
 * 
 */
@Entity
@Table(name = "TEAM")
public class Team implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = 1880929145636501135L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEAM_OID")
    private long teamOid;

    @Version
    private long version;

    private String name;

    @Enumerated(EnumType.STRING)
    private TeamType type;
    
    private String companyCodeGroup;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE }, fetch = FetchType.EAGER)
    @JoinTable(name = "GLORIA_USER_TEAM", joinColumns = { @JoinColumn(name = "TEAM_OID") }, inverseJoinColumns = { @JoinColumn(name = "GLORIA_USER_OID") })
    private Set<GloriaUser> users = new HashSet<GloriaUser>();

    public long getTeamOid() {
        return teamOid;
    }

    public void setTeamOid(long teamOid) {
        this.teamOid = teamOid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeamType getType() {
        return type;
    }

    public void setType(TeamType type) {
        this.type = type;
    }

    public Set<GloriaUser> getUsers() {
        return users;
    }

    public void setUsers(Set<GloriaUser> users) {
        this.users = users;
    }

    @Override
    public Long getId() {
        return this.teamOid;
    }

    @Override
    public long getVersion() {
        return this.version;
    }
    public String getCompanyCodeGroup() {
        return companyCodeGroup;
    }

    public void setCompanyCodeGroup(String companyCodeGroup) {
        this.companyCodeGroup = companyCodeGroup;
    }

    @Override
    public String toString() {
        return "Team [name=" + name + ", type=" + type + "]";
    }
}
