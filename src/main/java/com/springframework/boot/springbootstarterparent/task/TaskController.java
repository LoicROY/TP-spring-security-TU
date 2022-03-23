package com.springframework.boot.springbootstarterparent.task;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/api/tasks/demo")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<Void> update(@RequestBody JsonNode payload,
                                       UriComponentsBuilder uriComponentsBuilder) {
        Long taskId = taskService.createTask(payload.get("taskTitle").asText());
        return ResponseEntity
                .created(uriComponentsBuilder.path("/api/tasks/demo/{taskTitle}").build(taskId))
                .build();
    }

    @DeleteMapping("/{taskId}")
    @RolesAllowed("ADMIN")
    public void deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
    }
}
