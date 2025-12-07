package com.taskmanager.task.repo;

import com.taskmanager.task.model.request.TaskSearchRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TaskSpecification {

    public static Query build(TaskSearchRequest request) {
        Query query = new Query();
        if (request == null) {
            return query;
        }

        List<Criteria> criteriaList = new ArrayList<>();

        if (request.getTaskNumber() != null) {
            criteriaList.add(Criteria.where("taskNumber").is(request.getTaskNumber()));
        }
        if (request.getAssignedUserId() != null) {
            criteriaList.add(Criteria.where("assignedTo").is(request.getAssignedUserId()));
        }
        if (request.getPriority() != null) {
            criteriaList.add(Criteria.where("priority").is(request.getPriority()));
        }
        if (request.getStatus() != null) {
            criteriaList.add(Criteria.where("status").is(request.getStatus()));
        }
        if (request.getCreatedByUserId() != null) {
            criteriaList.add(Criteria.where("createdBy").is(request.getCreatedByUserId()));
        }

        if (request.getDueDateFrom() != null && request.getDueDateTo() != null) {
            criteriaList.add(Criteria.where("dueDate").gte(request.getDueDateFrom()).lte(request.getDueDateTo()));
        } else {
            if (request.getDueDateFrom() != null) {
                criteriaList.add(Criteria.where("dueDate").gte(request.getDueDateFrom()));
            }
            if (request.getDueDateTo() != null) {
                criteriaList.add(Criteria.where("dueDate").lte(request.getDueDateTo()));
            }
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            String title = request.getTitle().trim();
            Pattern pattern = Pattern.compile(".*" + Pattern.quote(title) + ".*", Pattern.CASE_INSENSITIVE);
            criteriaList.add(Criteria.where("title").regex(pattern));
        }

        if (!criteriaList.isEmpty()) {
            Criteria combined = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
            query.addCriteria(combined);
        }

        System.out.println("MongoDB Query: " + query);
        return query;
    }

    private TaskSpecification() {
        // utility
    }
}
