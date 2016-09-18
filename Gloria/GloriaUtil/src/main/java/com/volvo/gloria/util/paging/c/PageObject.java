package com.volvo.gloria.util.paging.c;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.GloriaSystemException;

/**
 * page conents.
 * 
 * Grid content list.
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class PageObject implements Serializable {

    private static final long serialVersionUID = -1221979967311225771L;
    
    private static final int RESULTS_NUMBER = 100;

    private List<PageResults> gridContents = new ArrayList<PageResults>();

    private int count = 1;

    private int currentPage = 1;

    private int resultsPerPage = RESULTS_NUMBER;

    private String sortOrder;

    private String sortBy;

    private Map<String, String> predicates;

    public List<PageResults> getGridContents() {
        return gridContents;
    }

    public void setGridContents(List<PageResults> gridContents) {
        this.gridContents = gridContents;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Map<String, String> getPredicates() {
        return predicates;
    }

    public void setPredicates(Map<String, String> predicates) {
        this.predicates = predicates;
    }

    public String getPredicate(Object key) {
        return predicates.get(key);
    }

    public int getFirstResult() {
        return (getCurrentPage() - 1) * getResultsPerPage();
    }

    public int getMaxResult() {
        int recordsForPage = getFirstResult() + getResultsPerPage();
        if (recordsForPage > getCount()) {
            return getResultsPerPage() - (recordsForPage - getCount());
        }
        return getResultsPerPage();
    }

    public static CriteriaQuery buildQuery(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery, Path selection, List<Predicate> predicatesRules,
            PageObject pageObject, Map<String, JpaAttribute> fieldMap, boolean queryForResultcount) {

        Object[] roots = criteriaQuery.getRoots().toArray();
        Root root = (Root) roots[0];

        if (queryForResultcount) {
            criteriaQuery = criteriaQuery.select(criteriaBuilder.countDistinct(selection));
        } else {
            criteriaQuery = criteriaQuery.select(selection).distinct(true);
        }

        addPredicateRules(criteriaBuilder, criteriaQuery, predicatesRules, pageObject, fieldMap, queryForResultcount, root);

        return criteriaQuery;
    }

    public static CriteriaQuery buildQuery(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery, Selection selection, List<Predicate> predicatesRules,
            PageObject pageObject, Map<String, JpaAttribute> fieldMap, boolean queryForResultcount) {

        Object[] roots = criteriaQuery.getRoots().toArray();
        Root root = (Root) roots[0];

        if (queryForResultcount) {
            criteriaQuery = criteriaQuery.select(selection);
        } else {
            criteriaQuery = criteriaQuery.select(selection).distinct(true);
        }

        addPredicateRules(criteriaBuilder, criteriaQuery, predicatesRules, pageObject, fieldMap, queryForResultcount, root);

        return criteriaQuery;
    }

    private static void addPredicateRules(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery, List<Predicate> predicatesRules, PageObject pageObject,
            Map<String, JpaAttribute> fieldMap, boolean queryForResultcount, Root root) {
        List<Predicate> listOfPredicateRules = new ArrayList<Predicate>();
        if (predicatesRules != null && !predicatesRules.isEmpty()) {
            listOfPredicateRules.addAll(predicatesRules);
        }

        Map<String, String> predicates = pageObject.getPredicates();
        if (predicates != null && !predicates.isEmpty()) {
            listOfPredicateRules = createPredicateRules(criteriaBuilder, root, predicates, listOfPredicateRules, fieldMap);
        }

        addOrderByClause(criteriaBuilder, criteriaQuery, pageObject, fieldMap, queryForResultcount, root);

        if (!listOfPredicateRules.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(listOfPredicateRules.toArray(new Predicate[listOfPredicateRules.size()])));
        }
    }

    private static void addOrderByClause(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery, PageObject pageObject,
            Map<String, JpaAttribute> fieldMap, boolean queryForResultcount, Root root) {
        if (!queryForResultcount) {
            String sortOrder = pageObject.getSortOrder();
            String sortBy = pageObject.getSortBy();
            List<Order> orderExpressions = new ArrayList<Order>();
            String[] sortByAttrs = null;
            if (sortBy != null) {
                sortByAttrs = sortBy.split(",");
                for (String sortByAttr : sortByAttrs) {
                    if (sortByAttr != null && fieldMap.containsKey(sortByAttr) && !sortByAttr.isEmpty()) {
                        JpaAttribute sortByAttribute = fieldMap.get(sortByAttr);
                        if (sortByAttribute != null) {
                            buildOrderExpression(criteriaBuilder, root, sortOrder, orderExpressions, sortByAttribute);
                        }
                    }
                }
            }
            if (!orderExpressions.isEmpty()) {
                criteriaQuery.orderBy(orderExpressions);
            }
        }
    }

    private static void buildOrderExpression(CriteriaBuilder criteriaBuilder, Root root, String sortOrder, List<Order> orderExpressions,
            JpaAttribute sortByAttribute) {
        String[] jpaAttributeNames = sortByAttribute.getJpaAttributeName().split(",");
        for (String jpaAttributeName : jpaAttributeNames) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                orderExpressions.add(criteriaBuilder.asc(buildOrderPath(root, jpaAttributeName)));
            } else {
                orderExpressions.add(criteriaBuilder.desc(buildOrderPath(root, jpaAttributeName)));
            }
        }
    }

    private static List<Predicate> createPredicateRules(CriteriaBuilder criteriaBuilder, Root root, Map<String, String> predicates,
            List<Predicate> listOfPredicateRules, Map<String, JpaAttribute> fieldMap) {
        for (String key : predicates.keySet()) {
            if (fieldMap.containsKey(key)) {
                String jpaAttributeName = fieldMap.get(key).getJpaAttributeName();
                JpaAttributeType jpaAttributeType = fieldMap.get(key).getJpaAttributeType();
                Class jpaAttributeClass = fieldMap.get(key).getJpaAttributeClass();
                if (jpaAttributeName.contains(",")) {
                    List<Predicate> orPredicateRules = new ArrayList<Predicate>();
                    String[] jpaAttributeNames = jpaAttributeName.split(",");
                    for (String jpname : jpaAttributeNames) {
                        evaluateAddPredicates(criteriaBuilder, predicates, orPredicateRules, key, jpaAttributeType, jpaAttributeClass, buildPath(root, jpname));
                    }
                    listOfPredicateRules.add(criteriaBuilder.or(orPredicateRules.toArray(new Predicate[orPredicateRules.size()])));
                } else {
                    evaluateAddPredicates(criteriaBuilder, predicates, listOfPredicateRules, key, jpaAttributeType, jpaAttributeClass,
                                          buildPath(root, jpaAttributeName));
                }
            }
        }
        return listOfPredicateRules;
    }
    
    private static Expression<?> buildOrderPath(Root root, String jpaAttributeName) {
        String delimiter = "\\.";
        String[] attributes = jpaAttributeName.split(delimiter);
        Expression<?> orderExpression = null;
        for (int idx = 0; idx < attributes.length; idx++) {
            if (orderExpression == null) {
                orderExpression = evalOrderExpression(root, attributes, idx);
            } else {
                orderExpression = evalOrderExpression((Path) orderExpression, attributes, idx);
            }
        }
        return orderExpression;
    }
    
    private static Expression<?> evalOrderExpression(Path<?> path, String[] attributes, int currentIndex) {
        if (attributes.length - 1 == currentIndex) {
            return path.get(attributes[currentIndex]);
        } else if (path instanceof Root) {
            return ((Root) path).join(attributes[currentIndex], JoinType.LEFT);
        }
        return ((Join) path).join(attributes[currentIndex], JoinType.LEFT);
    }

    private static Path<?> buildPath(Root root, String jpaAttributeName) {
        String delimiter = "\\.";
        String[] attributes = jpaAttributeName.split(delimiter);
        Path<?> path = null;
        for (String attribute : attributes) {
            if (path == null) {
                path = root.get(attribute);
            } else {
                path = path.get(attribute);
            }
        }
        return path;
    }

    private static void evaluateAddPredicates(CriteriaBuilder criteriaBuilder, Map<String, String> predicates, List<Predicate> listOfPredicateRules,
            String key, JpaAttributeType jpaAttributeType, Class jpaAttributeClass, Path<?> path) {

        switch (jpaAttributeType) {

        case STRINGTYPE:
            List<String> predicateValues = GloriaFormateUtil.getValuesAsString(predicates.get(key).toLowerCase());
            if (predicateValues != null && !predicateValues.isEmpty()) {
                listOfPredicateRules.add((Predicate) evaluateLikeExpressions(criteriaBuilder, path, predicateValues));
            }
            break;
        case DATETYPE:
            try {
                Date dateToMatch = DateUtil.getStringAsDate(predicates.get(key));
                Date dateRangeFrom = DateUtil.getDateWithZeroTime(dateToMatch);
                Date dateRangeTo = DateUtil.getNextDate(dateRangeFrom);
                Path<Date> datePath = (Path<Date>) path;
                listOfPredicateRules.add(criteriaBuilder.greaterThanOrEqualTo((Expression) datePath, dateRangeFrom));
                listOfPredicateRules.add(criteriaBuilder.lessThan((Expression) datePath, dateRangeTo));
            } catch (ParseException parseException) {
                throw new GloriaSystemException(parseException, "Invalid Date Format");
            }
            break;
        case NUMBERTYPE:
            listOfPredicateRules.add(criteriaBuilder.or((Predicate) path.in(GloriaFormateUtil.getValuesAsLong(predicates.get(key)))));
            break;
        case DOUBLETYPE:
            listOfPredicateRules.add(criteriaBuilder.or((Predicate) path.in(GloriaFormateUtil.getValuesAsDouble(predicates.get(key)))));
            break;
        case ENUMTYPE:
            listOfPredicateRules.add(criteriaBuilder.or((Predicate) path.in(GloriaFormateUtil.getValuesAsEnums(predicates.get(key), jpaAttributeClass))));
            break;
        default:
            listOfPredicateRules.add(criteriaBuilder.or((Predicate) path.in((Object[]) predicates.get(key).split(","))));
            break;
        }
    }

    private static Expression evaluateLikeExpressions(CriteriaBuilder criteriaBuilder, Path<?> path, List<String> predicateValues) {
        List<Expression> likeExpressions = new ArrayList<Expression>();
        for (int idx = 0; idx < predicateValues.size(); idx++) {
            String predicateValue = predicateValues.get(idx).trim().replaceAll("\\*", "%");
            likeExpressions.add(criteriaBuilder.like(criteriaBuilder.lower((Expression) path), predicateValue.concat("%")));
        }

        Expression criteriaOr = null;
        for (int idx = 0; idx < likeExpressions.size(); idx++) {
            if (idx == 0) {
                criteriaOr = likeExpressions.get(idx);
                continue;
            }
            criteriaOr = criteriaBuilder.or(criteriaOr, likeExpressions.get(idx));
        }
        return criteriaOr;
    }

    public void setDefaultSort(String sortBy, String sortOrder) {
        if (StringUtils.isEmpty(this.sortBy)) {
            setSortBy(sortBy);
        }

        if (StringUtils.isEmpty(this.sortOrder)) {
            setSortOrder(sortOrder);
        }
    }
}
