package com.davidruffner.homecontrollerbackend.controllers;

import com.davidruffner.homecontrollerbackend.cache.UserSettingsContext;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS.TodoistRequestOptions;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS.TodoistRetrieverResponse;
import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import com.davidruffner.homecontrollerbackend.services.TodoistRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.davidruffner.homecontrollerbackend.enums.TodoistProduct.FILTERED_TASKS;

@RestController
@RequestMapping("/todoist")
public class TodoistController {

    @Autowired
    ApplicationContext appCtx;

    @Autowired
    UserSettingsContext userSettingsCtx;

    @GetMapping("/getInboxTasks")
    public ResponseEntity<TodoistRetrieverResponse> getInboxTasks(
        @RequestBody(required = false) TodoistRequestOptions body
    ) {
        UserSettings userSettings = userSettingsCtx.getRequired();
        TodoistRetriever.Builder retriever = (TodoistRetriever.Builder) this.appCtx.getBean("TodoistRetrieverBuilder");

        TodoistRetrieverResponse response = retriever
            .init(FILTERED_TASKS)
            .inInbox()
            .sendRequest();

        if (body != null && body.sortingOptions() != null) {
            List<TodoistDTOS.TodoistRetrieverTask> processedTasks = body
                .sortingOptions()
                .getPostProcessing()
                .executeActions(response.getTasks());

            return ResponseEntity.ok(new TodoistRetrieverResponse(processedTasks));
        } else {
            return ResponseEntity.ok(response);
        }
    }
}
