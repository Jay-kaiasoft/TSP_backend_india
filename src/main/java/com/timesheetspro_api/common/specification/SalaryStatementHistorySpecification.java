package com.timesheetspro_api.common.specification;

import com.timesheetspro_api.common.model.salaryStatementHistory.SalaryStatementHistory;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class SalaryStatementHistorySpecification {
    public static Specification<SalaryStatementHistory> hasUserIds(List<Integer> userIds) {
        return (root, query, cb) ->
                root.get("employeeId").in(userIds);
    }

    public static Specification<SalaryStatementHistory> hasDepartmentIds(List<Integer> departmentIds) {
        return (root, query, cb) ->
                root.get("departmentId").in(departmentIds);
    }

    public static Specification<SalaryStatementHistory> hasMonth(List<String> month) {
        return (root, query, cb) ->
                root.get("monthYear").in(month);
    }

    public static Specification<SalaryStatementHistory> hasCompany(Integer companyId) {
        return (root, query, cb) ->
                root.get("companyDetails").get("id").in(companyId);
    }
}
