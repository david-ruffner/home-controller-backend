package com.davidruffner.homecontrollerbackend.controllers;

import com.davidruffner.homecontrollerbackend.cache.UserSettingsContext;
import com.davidruffner.homecontrollerbackend.config.TodoistConfig;
import com.davidruffner.homecontrollerbackend.dispatchers.TodoistRetrieverBuilderDispatcher;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS.*;
import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import com.davidruffner.homecontrollerbackend.services.TodoistRetriever;
import com.davidruffner.homecontrollerbackend.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.*;

import static com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS.getTodoistAPIAllRequest;
import static com.davidruffner.homecontrollerbackend.enums.TodoistProduct.FILTERED_TASKS;
import static com.davidruffner.homecontrollerbackend.enums.TodoistProduct.TASKS;
import static com.davidruffner.homecontrollerbackend.utils.Utils.getTodoistLabelColorByName;

@RestController
@RequestMapping("/todoist")
public class TodoistController {

    @Autowired
    ApplicationContext appCtx;

    @Autowired
    UserSettingsContext userSettingsCtx;

    @Autowired
    TodoistConfig todoistConfig;

    @GetMapping("/getProjects")
    public ResponseEntity<List<GetTodoistProjectsResults>> getProjects() {
        RestClient todoistRestClient = (RestClient) this.appCtx.getBean("TodoistRestClient");
        GetTodoistProjectsResponse response = todoistRestClient.get()
            .uri("/api/v1/projects")
            .header("Authorization", this.todoistConfig.getApiKeyAsBearer())
            .retrieve()
            .body(GetTodoistProjectsResponse.class);

        // Only return "list" type projects
        List<GetTodoistProjectsResults> filteredResults = response.results()
            .stream()
            .filter(r -> r.viewStyle().equals("list"))
            .sorted(Comparator.comparing(GetTodoistProjectsResults::name))
            .toList();

        return ResponseEntity.ok(filteredResults);
    }

    @PostMapping("/getInboxTasks")
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

    @GetMapping("/getTasksByProjectId/{projectId}")
    public ResponseEntity<List<GetTodoistSyncTask>> getProjectTasksById(@PathVariable("projectId") String projectId,
        @RequestParam("sortingAction") String sortingAction) {

        RestClient todoistRestClient = (RestClient) this.appCtx.getBean("TodoistRestClient");
        GetTodoistSyncTasksResponseDTO response = todoistRestClient.post()
            .uri("/api/v1/sync")
            .body(getTodoistAPIAllRequest())
            .header("Authorization", this.todoistConfig.getApiKeyAsBearer())
            .retrieve()
            .body(GetTodoistSyncTasksResponseDTO.class);

        // Filters project tasks by deletion status, project, and parent
        List<GetTodoistSyncTask> projectTasks = response.getItems()
            .stream()
            .filter(r -> !r.getDeleted() && r.getProjectId().equals(projectId) &&
                r.getParentId() == null)
            .toList();

        Map<String, List<GetTodoistSyncTask>> subTasksMap = new HashMap<>();
        // Filters subtasks by project ID and non‑null parent
        response.getItems()
            .stream()
            .filter(r -> !r.getDeleted() && r.getProjectId().equals(projectId) &&
                r.getParentId() != null)
            .forEach(subTask -> {
                if (!subTasksMap.containsKey(subTask.getParentId())) {
                    List<GetTodoistSyncTask> subTasksList = new ArrayList<>();
                    subTasksList.add(subTask);
                    subTasksMap.put(subTask.getParentId(), subTasksList);
                } else {
                    subTasksMap.get(subTask.getParentId()).add(subTask);
                }
            });

        // Augments tasks with reminders and subtasks if present
        projectTasks.forEach(r -> {
            Optional<List<GetTodoistAPIReminder>> remindersList = response.getReminderByTaskId(r.getId());
            remindersList.ifPresent(reminders -> r.setReminders(
                    reminders
                        .stream()
                        .map(GetTodoistAPIReminder::due)
                        .toList()
                )
            );

            if (subTasksMap.containsKey(r.getId())) {
                r.setSubTasks(subTasksMap.get(r.getId()));
            }
        });

        // Handle sorting options
        switch (sortingAction) {
            case "alphabetically":
                projectTasks = projectTasks.stream()
                    .sorted(Comparator.comparing(GetTodoistSyncTask::getContent))
                    .toList();
                break;

            case "due_date":
                projectTasks = projectTasks.stream()
                    .sorted(Comparator.comparing(
                        GetTodoistSyncTask::getDueDate,
                        Comparator.nullsLast(Comparator.naturalOrder())
                    ))
                    .toList();
                break;
        }

        return ResponseEntity.ok(projectTasks);
    }

    @PostMapping("/getLabels")
    public ResponseEntity<GetTodoistLabelsResponse> getLabels(
        @RequestBody(required = false) List<String> filterLabelNames
    ) {
        RestClient todoistRestClient = (RestClient) this.appCtx.getBean("TodoistRestClient");
        GetTodoistAPILabelsResponse response = todoistRestClient.get()
            .uri("/api/v1/labels")
            .header("Authorization", this.todoistConfig.getApiKeyAsBearer())
            .retrieve()
            .body(GetTodoistAPILabelsResponse.class);

        List<GetTodoistAPILabel> apiLabels;
        if (filterLabelNames == null || filterLabelNames.isEmpty()) {
            apiLabels = response.results();
        } else {
            Map<String, String> filterLabelNamesMap = new HashMap<>();
            filterLabelNames.forEach(label -> filterLabelNamesMap.put(label, label));

            apiLabels = response.results().stream()
                .filter(label -> {
                    return filterLabelNamesMap.containsKey(label.name());
                })
                .toList();
        }

        List<GetTodoistLabel> returnLabels = new ArrayList<>();
        apiLabels.forEach(apiLabel -> returnLabels.add(new GetTodoistLabel(apiLabel.name(),
            apiLabel.color(), getTodoistLabelColorByName(apiLabel.color()))));

        return ResponseEntity.ok(new GetTodoistLabelsResponse(returnLabels));
    }

    @PostMapping("/filterTasks")
    public ResponseEntity<TodoistRetrieverResponse> filterTasks(
        @RequestBody TodoistRequestOptions body
    ) {
        UserSettings userSettings = userSettingsCtx.getRequired();
        TodoistRetriever.Builder retriever = (TodoistRetriever.Builder) this.appCtx.getBean("TodoistRetrieverBuilder");

        for (TodoistRequestFilterOption filter : body.filterOptions()) {
            if (filter.getValue() != null) {
                // Used for singular filter values
                if (filter.getValue() instanceof String) {
                    // String filter values
                    retriever = TodoistRetrieverBuilderDispatcher.apply(retriever, filter.getFilterName(),
                        (String) filter.getValue()).init(TASKS);
                } else if (filter.getValue() instanceof Integer) {
                    // Integer filter values
                    retriever = TodoistRetrieverBuilderDispatcher.apply(retriever, filter.getFilterName(),
                        (Integer) filter.getValue()).init(TASKS);
                }
            } else if (filter.getStartValue() != null && filter.getEndValue() != null) {
                if (filter.getStartValue() instanceof String && filter.getEndValue() instanceof String) {
                    retriever = TodoistRetrieverBuilderDispatcher.apply(retriever, filter.getFilterName(),
                        (String) filter.getStartValue(), (String) filter.getEndValue()).init(TASKS);
                }
            } else if (filter.getValue() == null && filter.getStartValue() == null && filter.getEndValue() == null) {
                retriever = TodoistRetrieverBuilderDispatcher.apply(retriever, filter.getFilterName()).init(TASKS);
            } else if (filter.getTodoistPriority() != null) {
                 TodoistRetrieverBuilderDispatcher.apply(retriever, filter.getFilterName(),
                    filter.getTodoistPriority()).init(TASKS);
            } else if (filter.getTodoistPriorities() != null) {
                TodoistRetrieverBuilderDispatcher.apply(retriever, filter.getFilterName(),
                    filter.getTodoistPriorities()).init(TASKS);
            }

        }

        return ResponseEntity.ok(retriever.sendRequest());
    }
}
