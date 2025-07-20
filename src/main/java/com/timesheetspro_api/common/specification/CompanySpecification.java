package com.timesheetspro_api.common.specification;

import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class CompanySpecification {
    public static Specification<CompanyDetails> searchByName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")), "%" + name.toLowerCase() + "%");
    }
    public static Specification<CompanyDetails> registerDateGreaterThanEqual(Date date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("registerDate"), date);
    }
    public static Specification<CompanyDetails> registerDateLessThanEqual(Date date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("registerDate"), date);
    }

    public static Specification<CompanyDetails> isActive(boolean active) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), active ? 1 : 0);
    }
    public static Specification<CompanyDetails> employeeCountBetween(int min, int max) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true); // Important for correct counting
            return criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(
                            criteriaBuilder.size(root.get("companyEmployees")),
                            min
                    ),
                    criteriaBuilder.lessThanOrEqualTo(
                            criteriaBuilder.size(root.get("companyEmployees")),
                            max
                    )
            );
        };
    }

    public static Specification<CompanyDetails> employeeCountGreaterThan(int min) {
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<CompanyEmployee> employeeRoot = subquery.from(CompanyEmployee.class);
            subquery.select(cb.count(employeeRoot.get("employeeId")));
            subquery.where(cb.equal(employeeRoot.get("companyDetails"), root));
            return cb.greaterThanOrEqualTo(subquery, (long) min);
        };
    }

    public static Specification<CompanyDetails> employeeCountLessThan(int max) {
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<CompanyEmployee> employeeRoot = subquery.from(CompanyEmployee.class);
            subquery.select(cb.count(employeeRoot.get("employeeId")));
            subquery.where(cb.equal(employeeRoot.get("companyDetails"), root));
            return cb.lessThanOrEqualTo(subquery, (long) max);
        };
    }

}
