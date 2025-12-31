package com.taskmanager.task.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class CreateUpdateCommentRequest {

    @NotNull
    private String taskId;

    @NotNull
    private String content;
}
