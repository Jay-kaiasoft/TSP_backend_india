package com.timesheetspro_api.common.specification;

import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.UserInOut.UserInOut;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;

public class EmployeeStatementSpecification {

    public static Specification<UserInOut> betweenCreatedOn(Date startDate, Date endDate) {
        return (root, query, cb) -> cb.between(root.get("createdOn"), startDate, endDate);
    }

    // Filter by createdOn >= startDate
    public static Specification<UserInOut> createdOnGreaterThanEqual(Date startDate) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("createdOn"), startDate);
    }

    // Filter by createdOn <= endDate
    public static Specification<UserInOut> createdOnLessThanEqual(Date endDate) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("createdOn"), endDate);
    }

    // Filter UserInOut by a list of user IDs
    public static Specification<UserInOut> hasUserIds(List<Integer> userIds) {
        return (root, query, cb) ->
                root.get("user").get("id").in(userIds);
    }

    // Filter CompanyEmployee by a list of department IDs
    public static Specification<CompanyEmployee> hasDepartmentIds(List<Integer> departmentIds) {
        return (root, query, cb) ->
                root.get("department").get("id").in(departmentIds);
    }

    // Filter CompanyEmployee by a list of employee IDs
    public static Specification<CompanyEmployee> hasEmployeeIds(List<Integer> employeeIds) {
        return (root, query, cb) ->
                root.get("id").in(employeeIds);
    }

    public static Specification<CompanyEmployee> hasCompanyId(Integer companyId) {
        return (root, query, cb) ->
                root.get("companyDetails").get("id").in(companyId);
    }
}
