package com.timesheetspro_api.common.specification;

import com.timesheetspro_api.common.model.UserInOut.UserInOut;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;

public class UserInOutSpecification {
    public static Specification<UserInOut> createdOnGreaterThanEqual(Date startDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdOn"), startDate);
    }

    public static Specification<UserInOut> createdOnLessThanEqual(Date endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("createdOn"), endDate);
    }

    public static Specification<UserInOut> hasUserId(int userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user").get("id"), userId);
    }
    public static Specification<UserInOut> userIdIn(List<Integer> userIds) {
        return (root, query, criteriaBuilder) -> root.get("user").get("employeeId").in(userIds);
    }

    public static Specification<UserInOut> hasLocationId(List<Integer> locationIds) {
        return (root, query, criteriaBuilder) -> root.get("locations").get("id").in(locationIds);
    }

    public static Specification<UserInOut> hasDepartmentIds(List<Integer> departmentIds) {
        return (root, query, criteriaBuilder) -> root.get("user").get("department").get("id").in(departmentIds);
    }
    public static Specification<UserInOut> hasCompany(Integer companyId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("companyDetails").get("id"), companyId);
    }
}
