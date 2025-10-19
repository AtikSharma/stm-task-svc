package com.taskmanager.task.repo;

import com.taskmanager.task.entity.TaskEntity;
import com.taskmanager.task.model.request.TaskSearchRequest;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {

    public static Specification<TaskEntity> build(TaskSearchRequest request) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (request.getTaskNumber() != null) {
                predicates.getExpressions().add(cb.equal(root.get("taskNumber"), request.getTaskNumber()));
            }
            if (request.getAssignedUserId() != null) {
                predicates.getExpressions().add(cb.equal(root.get("assignedTo"), request.getAssignedUserId()));
            }
            if (request.getPriority() != null) {
                predicates.getExpressions().add(cb.equal(root.get("priority"), request.getPriority()));
            }
            if (request.getStatus() != null) {
                predicates.getExpressions().add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getCreatedByUserId() != null) {
                predicates.getExpressions().add(cb.equal(root.get("createdBy"), request.getCreatedByUserId()));
            }
            if (request.getDueDateFrom() != null) {
                predicates.getExpressions().add(cb.greaterThanOrEqualTo(root.get("dueDate"), request.getDueDateFrom()));
            }
            if (request.getDueDateTo() != null) {
                predicates.getExpressions().add(cb.lessThanOrEqualTo(root.get("dueDate"), request.getDueDateTo()));
            }
            if (request.getTitle() != null && !request.getTitle().isBlank()) {
                predicates.getExpressions().add(cb.like(cb.lower(root.get("title")), "%" + request.getTitle().toLowerCase() + "%"));
            }
            if (request.getDueDateFrom() != null && request.getDueDateTo() != null) {
                predicates.getExpressions().add(cb.between(root.get("dueDate"), request.getDueDateFrom(), request.getDueDateTo()));
            }
            return predicates;
        };
    }
}
