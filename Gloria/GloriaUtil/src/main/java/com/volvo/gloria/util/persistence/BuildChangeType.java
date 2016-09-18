package com.volvo.gloria.util.persistence;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
/**
 * Build Change Type interface.
 */
public interface BuildChangeType {

    long getBuildChangeTypeOid();

    void setBuildChangeTypeOid(long buildChangeTypeOid);

    String getCode();

    void setCode(String code);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    Date getValidFrom();

    void setValidFrom(Date validFrom);

    Date getValidTo();

    void setValidTo(Date validTo);

    Timestamp getLastModified();

    void setLastModified(Timestamp lastModified);

    String getLastModifiedUser();

    void setLastModifiedUser(String lastModifiedUser);

    List<BuildChange> getBuildChanges();

    void setBuildChanges(List<BuildChange> buildChanges);
}
