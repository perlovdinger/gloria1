package com.volvo.gloria.procurematerial.b.beans;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.procurematerial.repositories.b.beans.RequestHeaderRepositoryBeanHelper;

public class RequestHeaderRepositoryBeanTest {

    @Test
    public void test() throws ParseException {
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        Path<Date> path = Mockito.mock(Path.class);
        List<Predicate> list = RequestHeaderRepositoryBeanHelper.handleDates("2015-08-20", null, null, false, criteriaBuilder, path);
        Assert.assertEquals(2, list.size());
        List<Predicate> list1 = RequestHeaderRepositoryBeanHelper.handleDates(null, "2015-08-20", null, false, criteriaBuilder, path);
        Assert.assertEquals(1, list1.size());
        List<Predicate> list2 = RequestHeaderRepositoryBeanHelper.handleDates(null, null, "2015-08-20", false, criteriaBuilder, path);
        Assert.assertEquals(1, list2.size());
        List<Predicate> list3 = RequestHeaderRepositoryBeanHelper.handleDates(null, "2015-08-20", "2015-08-20", false, criteriaBuilder, path);
        Assert.assertEquals(2, list3.size());
        List<Predicate> list4 = RequestHeaderRepositoryBeanHelper.handleDates(null, null, null, true, criteriaBuilder, path);
        Assert.assertEquals(1, list4.size());
    }
}