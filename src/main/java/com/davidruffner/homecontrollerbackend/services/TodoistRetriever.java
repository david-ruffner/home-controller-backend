package com.davidruffner.homecontrollerbackend.services;

import com.davidruffner.homecontrollerbackend.cache.UserSettingsContext;
import com.davidruffner.homecontrollerbackend.config.TodoistConfig;
import com.davidruffner.homecontrollerbackend.dtoConverters.TodoistTaskMapper;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS.TodoistRetrieverResponse;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS.TodoistRetrieverResponseRaw;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS.TodoistRetrieverTask;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS.TodoistRetrieverTaskRaw;
import com.davidruffner.homecontrollerbackend.enums.ShortCode;
import com.davidruffner.homecontrollerbackend.enums.TodoistPriority;
import com.davidruffner.homecontrollerbackend.enums.TodoistProduct;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.UnaryOperator;

import static com.davidruffner.homecontrollerbackend.enums.ResponseCode.SYSTEM_EXCEPTION;
import static com.davidruffner.homecontrollerbackend.services.TodoistRetriever.PostProcessingAction.ALPHABETICALLY;
import static com.davidruffner.homecontrollerbackend.services.TodoistRetriever.PostProcessingAction.ORDER_BY_CHILD_ORDER;
import static com.davidruffner.homecontrollerbackend.utils.Utils.strNotEmpty;
import static java.util.Map.entry;

public class TodoistRetriever {
    private TodoistRetrieverResponse sendRequest() {
//        List<TodoistRetrieverTaskRaw> rawResults = new ArrayList<>();
        Map<String, TodoistRetrieverTaskRaw> rawResults = new HashMap<>();
        TodoistRetrieverResponseRaw rawResponse = null;
        String previousCursor = "";

        do {
            if (rawResponse != null && strNotEmpty(rawResponse.getNextCursor())
            && !rawResponse.getNextCursor().equals(previousCursor)
            ) {
                previousCursor = rawResponse.getNextCursor();
            }

            URI uri = this.uriBuilder
                .build(true)
                .toUri();

            rawResponse = this.restClient
                .get()
                .uri(uri)
                .header("Authorization", this.todoistConfig.getApiKeyAsBearer())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    String body = new String(res.getBody().readAllBytes());
                    throw new ControllerException(String.format("ERROR | Todoist 4xx | Status Code: '%d' | Body: %s",
                        res.getStatusCode().value(), body), SYSTEM_EXCEPTION, ShortCode.SYSTEM_EXCEPTION.toString());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    String body = new String(res.getBody().readAllBytes());
                    throw new ControllerException(String.format("ERROR | Todoist 5xx | Status Code: '%d' | Body: %s",
                        res.getStatusCode().value(), body), SYSTEM_EXCEPTION, ShortCode.SYSTEM_EXCEPTION.toString());
                })
                .body(TodoistRetrieverResponseRaw.class);

            if (rawResponse != null && !rawResponse.getResults().isEmpty()) {
//                rawResults.addAll(rawResponse.getResults());
//                rawResponse.getResults().get(0).taskId()

                rawResponse.getResults().forEach(r -> rawResults.put(r.taskId(), r));
            }

            if (rawResponse != null && strNotEmpty(rawResponse.getNextCursor()) &&
                !rawResponse.getNextCursor().equals(previousCursor)
            ) {
                this.uriBuilder.queryParam("cursor", rawResponse.getNextCursor());
            }
        } while (rawResponse != null && strNotEmpty(rawResponse.getNextCursor())
        && !rawResponse.getNextCursor().equals(previousCursor));

        List<TodoistRetrieverTask> enriched = rawResults.values().stream()
            .map(todoistTaskMapper::toEnriched)
            .toList();

        if (!this.postProcessingActions.orderingActions.isEmpty()) {
            enriched = this.postProcessingActions.executeActions(enriched);
        }

        return new TodoistRetrieverResponse(enriched);
    }

    private final TodoistConfig todoistConfig;
    private final RestClient restClient;
    private final UserSettingsContext userSettingsCtx;
    private final TodoistTaskMapper todoistTaskMapper;
    private final UriComponentsBuilder uriBuilder;
    private final TodoistProduct todoistProduct;
    private final PostProcessing postProcessingActions;

    private TodoistRetriever(Builder builder) {
        this.todoistConfig = builder.todoistConfig;
        this.restClient = builder.restClient;
        this.userSettingsCtx = builder.userSettingsCtx;
        this.todoistTaskMapper = builder.todoistTaskMapper;
        this.uriBuilder = builder.uriBuilder;
        this.todoistProduct = builder.todoistProduct;
        this.postProcessingActions = builder.postProcessingActions;
    }

    @Component(value = "TodoistRetrieverBuilder")
    @Scope(value = "prototype")
    public static class Builder {

        @Autowired
        private TodoistConfig todoistConfig;

        @Autowired
        @Qualifier("TodoistRestClient")
        private RestClient restClient;

        @Autowired
        UserSettingsContext userSettingsCtx;

        @Autowired
        TodoistTaskMapper todoistTaskMapper;

        private UriComponentsBuilder uriBuilder;
        private StringBuilder queryFilterBuilder;
        private TodoistProduct todoistProduct;
        private PostProcessing postProcessingActions;

        public Builder init(TodoistProduct product) {
            this.todoistProduct = product;
            this.uriBuilder = UriComponentsBuilder.fromUriString(
                todoistConfig.getApiUrl() + product.getPath());
            this.queryFilterBuilder = new StringBuilder();
            this.postProcessingActions = new PostProcessing();

            return this;
        }

        // Used by RetrieverBuilderDispatcher
        public Builder initWithoutProduct() {
            this.queryFilterBuilder = new StringBuilder();
            this.postProcessingActions = new PostProcessing();

            return this;
        }

        public Builder changeProduct(TodoistProduct product) {
            this.todoistProduct = product;
            this.uriBuilder = UriComponentsBuilder.fromUriString(
                todoistConfig.getApiUrl() + product.getPath()
            );

            return this;
        }

        public Optional<TodoistProduct> getTodoistProduct() {
            return Optional.ofNullable(this.todoistProduct);
        }

        public Builder byUserId(String userId) {
            this.uriBuilder.queryParam("user_id", userId);
            return this;
        }

        public Builder byTaskId(String taskId) {
            this.uriBuilder.queryParam("id", taskId);
            return this;
        }

        public Builder byProjectId(String projectId) {
            this.uriBuilder.queryParam("project_id", projectId);
            return this;
        }

        public Builder bySectionId(String sectionId) {
            this.uriBuilder.queryParam("section_id", sectionId);
            return this;
        }

        public Builder byParentId(String parentId) {
            this.uriBuilder.queryParam("parent_id", parentId);
            return this;
        }

        public Builder byAddedUID(String addedUID) {
            this.uriBuilder.queryParam("added_by_uid", addedUID);
            return this;
        }

        public Builder byAssignedUID(String assignedUID) {
            this.uriBuilder.queryParam("assigned_by_uid", assignedUID);
            return this;
        }

        public Builder byResponsibleUID(String responsibleUID) {
            this.uriBuilder.queryParam("responsible_uid", responsibleUID);
            return this;
        }

        /**
         * Must be used with FILTERED_TASKS product
         */
        public Builder bySearchTerm(String searchTerm) {
            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(")
                .append("search: ")
                .append(searchTerm)
                .append(")");

            return this;
        }

        /**
         * Must be used with FILTERED_TASKS product
         *
         * @param date - Ideally this will be in 'yyyy-MM-dd' format, but looser formats are accepted.
         */
        public Builder onDate(String date) {
            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(")
                .append("date: ")
                .append(date)
                .append(")");

            return this;
        }

        /**
         * Must be used with FILTERED_TASKS product
         *
         * @param date - 'yyyy-MM-dd'
         */
        public Builder beforeDate(String date) {
            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(")
                .append("date before: ")
                .append(date)
                .append(")");

            return this;
        }

        /**
         * Must be used with FILTERED_TASKS product
         *
         * @param date - 'yyyy-MM-dd'
         */
        public Builder afterDate(String date) {
            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(")
                .append("date after: ")
                .append(date)
                .append(")");

            return this;
        }

        /**
         * Must be used with FILTERED_TASKS product
         *
         *
         * @param startDate - 'yyyy-MM-dd' - excluded from range
         * <br/>
         * @param endDate - 'yyyy-MM-dd' - excluded from range
         */
        public Builder inBetweenDate(String startDate, String endDate) {
            String filterVal = new StringBuilder("(")
                .append("date after: ")
                .append(startDate)
                .append(" & ")
                .append("date before: ")
                .append(endDate)
                .append(")")
                .toString();

            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(")
                .append(filterVal)
                .append(")");

            return this;
        }

        /**
         * Must be used with FILTERED_TASKS product
         *
         * @param date - 'yyyy-MM-dd'
         */
        public Builder dueOn(String date) {
            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(due: ")
                .append(date)
                .append(")");

            return this;
        }

        /**
         * Must be used with FILTERED_TASKS product
         *
         * @param date - 'yyyy-MM-dd'
         */
        public Builder dueBefore(String date) {
            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(")
                .append("due before: ")
                .append(date)
                .append(")");

            return this;
        }

        /**
         * Must be used with FILTERED_TASKS product
         *
         * @param date - 'yyyy-MM-dd'
         */
        public Builder dueAfter(String date) {
            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(")
                .append("due after: ")
                .append(date)
                .append(")");

            return this;
        }

        /**
         * Must be used with FILTERED_TASKS product
         *
         *
         * @param startDate - 'yyyy-MM-dd' - excluded from range
         * <br/>
         * @param endDate - 'yyyy-MM-dd' - excluded from range
         */
        public Builder dueInBetween(String startDate, String endDate) {
            String filterVal = new StringBuilder("(")
                .append("due after: ")
                .append(startDate)
                .append(" & ")
                .append("due before: ")
                .append(endDate)
                .append(")")
                .toString();

            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(")
                .append(filterVal)
                .append(")");

            return this;
        }

        /**
         * Must be used with FILTERED_TASKS product
         *
         * @param numDays - Search for tasks due in next N days
         */
        public Builder dueInNextNDays(Integer numDays) {
            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(")
                .append("next ")
                .append(numDays)
                .append(" days")
                .append(")");

            return this;
        }

        /**
         * Must be used with FILTERED_TASKS product
         * <br/>
         * Get all recurring tasks
         */
        public Builder recurring() {
            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(")
                .append("recurring")
                .append(")");

            return this;
        }

        /**
         * Must be used with FILTERED_TASKS product
         * <br/>
         * Get all recurring tasks
         */
        public Builder noDate() {
            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(")
                .append("no date")
                .append(")");

            return this;
        }

        /**
         * Must be used with FILTERED_TASKS product
         *
         * Gets all tasks that are in the inbox
         */
        public Builder inInbox() {
            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(")
                .append("#Inbox")
                .append(")");

            return this;
        }

        /**
         * Must be used with FILTERED_TASKS product
         *
         * Gets all tasks that belong to a given project name
         * @param projectName String
         */
        public Builder inProject(String projectName) {
            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(#")
                .append(projectName)
                .append(")");

            return this;
        }

        /**
         * Must be used with <b>FILTERED_TASKS</b> product
         * <br/><br/>
         * Gets all tasks that match the given priority level
         * @param priority TodoistPriority enum
         */
        public Builder isPriority(TodoistPriority priority) {
            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(")
                .append(priority.getFilterTerm())
                .append(")");

            return this;
        }

        /**
         * Must be used with <b>FILTERED_TASKS</b> product
         * <br/><br/>
         * Gets all tasks that match one or more of the given priority levels
         * @param priorities List[TodoistPriority]
         */
        public Builder isOneOfPriority(List<TodoistPriority> priorities) {
            if (!this.queryFilterBuilder.isEmpty()) {
                this.queryFilterBuilder.append(" & ");
            }
            this.queryFilterBuilder
                .append("(");

            // Filtering through HashMap first prevents duplicates
            Map<String, TodoistPriority> priorityMap = new HashMap<>();
            priorities.forEach(p -> priorityMap.put(p.getFilterTerm(), p));

            List<Map.Entry<String, TodoistPriority>> entries = priorityMap
                .entrySet()
                .stream()
                .toList();

            for (int i = 0; i < entries.size(); i++) {
                TodoistPriority p = entries.get(i).getValue();
                this.queryFilterBuilder.append(p.getFilterTerm());

                if (i < (entries.size() - 1)) {
                    this.queryFilterBuilder.append(" | ");
                }
            }
            this.queryFilterBuilder.append(")");

            return this;
        }

        /**
         * Must be used with the <b>TASKS</b> product
         * <br/><br/>
         * Gets all sub-tasks for some given parent task ID
         * @param parentTaskId String - Listed under the key 'id' in the raw Todoist response for a task
         */
        public Builder getSubTasks(String parentTaskId) {
            this.uriBuilder.queryParam("parent_id", parentTaskId);

            return this;
        }

        public Builder addPostProcessingTask(PostProcessingAction actionName) {
            this.postProcessingActions.addOrderingAction(actionName);
            return this;
        }

        public TodoistRetrieverResponse sendRequest() {
            if (!this.queryFilterBuilder.isEmpty() && this.todoistProduct.equals(TodoistProduct.FILTERED_TASKS)) {
                String queryVal = URLEncoder.encode(this.queryFilterBuilder.toString());
                this.uriBuilder.queryParam("query", queryVal);
            }

            TodoistRetriever retriever = new TodoistRetriever(this);
            return retriever.sendRequest();
        }
    }

    public enum PostProcessingAction {
        ORDER_BY_CHILD_ORDER("order-by-child-order"),
        ALPHABETICALLY("alphabetically");

        private final String label;

        private static final Map<String, PostProcessingAction> labelMap = Map.ofEntries(
            entry("order-by-child-order", ORDER_BY_CHILD_ORDER),
            entry("alphabetically", ALPHABETICALLY)
        );

        public static Optional<PostProcessingAction> fromLabel(String label) {
            return Optional.ofNullable(labelMap.get(label));
        }

        PostProcessingAction(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return this.label;
        }
    }

    public static class PostProcessing {

        private Map<PostProcessingAction, Ordering> orderingActions;

        public PostProcessing() {
            this.orderingActions = new HashMap<>();
        }

        public void addOrderingAction(PostProcessingAction actionName) {
            this.orderingActions.put(actionName, new Ordering(actionName));
        }

        public void removeOrderingAction(String actionName) {
            this.orderingActions.remove(actionName);
        }

        public List<TodoistRetrieverTask> executeActions(List<TodoistRetrieverTask> tasks) {
            // Execute ordering actions
            List<TodoistRetrieverTask> tasksCopy = tasks.stream()
                .map(TodoistRetrieverTask::copy)
                .toList();
            List<Ordering> orderings = this.orderingActions.values()
                .stream()
                .toList();

            for (Ordering o : orderings) {
                tasksCopy = o.performAction(tasksCopy);
            }

            return tasksCopy;
        }

        public static class Ordering {

            private final UnaryOperator<List<TodoistRetrieverTask>> action;
            private final PostProcessingAction actionName;

            private Map<PostProcessingAction, UnaryOperator<List<TodoistRetrieverTask>>> actionMap = Map.ofEntries(
                entry(ORDER_BY_CHILD_ORDER, this::orderTasksByChildOrder),
                entry(ALPHABETICALLY, this::orderTasksAlphabetically)
            );

            public Ordering(PostProcessingAction actionName) {
                if (!actionMap.containsKey(actionName)) {
                    throw new ControllerException(String.format("Todoist Ordering Action '%s' " +
                        "doesn't exist.", actionName.name()), SYSTEM_EXCEPTION, ShortCode.SYSTEM_EXCEPTION.toString());
                }

                this.action = actionMap.get(actionName);
                this.actionName = actionName;
            }

            public static Ordering copy(Ordering toCopy) {
                return new Ordering(toCopy.getActionName());
            }

            private List<TodoistRetrieverTask> performAction(List<TodoistRetrieverTask> tasks) {
                return this.action.apply(tasks);
            }

            public UnaryOperator<List<TodoistRetrieverTask>> getAction() {
                return action;
            }

            public PostProcessingAction getActionName() {
                return actionName;
            }

            private List<TodoistRetrieverTask> orderTasksByChildOrder(List<TodoistRetrieverTask> tasks) {
                return tasks.stream()
                    .sorted(Comparator.comparingInt(TodoistRetrieverTask::getChildOrderAsInt))
                    .toList();
            }

            private List<TodoistRetrieverTask> orderTasksAlphabetically(List<TodoistRetrieverTask> tasks) {
                return tasks.stream()
                    .sorted(Comparator.comparing(TodoistRetrieverTask::getContent))
                    .toList();
            }
        }
    }
}
