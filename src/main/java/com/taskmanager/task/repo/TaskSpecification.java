package com.taskmanager.task.repo;

import com.taskmanager.task.entity.TaskEntity;
import com.taskmanager.task.model.request.TaskSearchRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {


    public static Specification<TaskEntity> build(TaskSearchRequest request) {
        return (root, query, cb) -> {
            if (request == null) {
                return cb.conjunction();
            }

           List<Predicate> predicates = new ArrayList<>();

            if (request.getTaskNumber() != null) {
                predicates.add(cb.equal(root.get("taskNumber"), request.getTaskNumber()));
            }
            if (request.getAssignedUserId() != null) {
                predicates.add(cb.equal(root.get("assignedTo"), request.getAssignedUserId()));
            }
            if (request.getPriority() != null) {
                predicates.add(cb.equal(root.get("priority"), request.getPriority()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getCreatedByUserId() != null) {
                predicates.add(cb.equal(root.get("createdBy"), request.getCreatedByUserId()));
            }

            if (request.getDueDateFrom() != null && request.getDueDateTo() != null) {
                predicates.add(cb.between(root.get("dueDate"), request.getDueDateFrom(), request.getDueDateTo()));
            } else {
                if (request.getDueDateFrom() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), request.getDueDateFrom()));
                }
                if (request.getDueDateTo() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), request.getDueDateTo()));
                }
            }

            if (request.getTitle() != null && !request.getTitle().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + request.getTitle().toLowerCase() + "%"));
            }

            System.out.println("Specification predicates: " + predicates);
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
