package com.davidruffner.homecontrollerbackend.dtos;

import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import com.davidruffner.homecontrollerbackend.enums.ShortCode;
import com.davidruffner.homecontrollerbackend.enums.TodoistPriority;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import com.davidruffner.homecontrollerbackend.services.TodoistRetriever.PostProcessing;
import com.davidruffner.homecontrollerbackend.services.TodoistRetriever.PostProcessingAction;
import com.davidruffner.homecontrollerbackend.utils.Utils.ZDTTime;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

import static com.davidruffner.homecontrollerbackend.enums.ResponseCode.BAD_REQUEST;
import static com.davidruffner.homecontrollerbackend.utils.Utils.getTimestampFromZDT;
import static com.davidruffner.homecontrollerbackend.utils.Utils.getZDTFromTimestamp;

public class TodoistDTOS {
    public record TodoistTaskDeadline(
        String date
    ) {}

    public record TodoistTaskDuration(
        Long amount
    ) {}

    public record TodoistTaskDue(
        String date,
        @JsonProperty("is_recurring") Boolean isRecurring,
        @JsonProperty("string") String strVal
    ) {}

    public record TodoistRetrieverTaskRaw(
        @JsonProperty("id") String taskId,
        @JsonProperty("project_id") String projectId,
        @JsonProperty("section_id") String sectionId,
        @JsonProperty("parent_id") String parentId,
        List<String> labels,
        TodoistTaskDeadline deadline,
        TodoistTaskDuration duration,
        TodoistTaskDue due,
        Long priority,
        String content,
        String description,
        @JsonProperty("child_order") Long childOrder,
        @JsonProperty("note_count") Long noteCount
    ) {}

    public static class TodoistRetrieverResponseRaw {
        private final List<TodoistRetrieverTaskRaw> results;
        private final String nextCursor;
        private final String errBody;
        private final ShortCode shortCode;

        public TodoistRetrieverResponseRaw(
            List<TodoistRetrieverTaskRaw> results,
            @JsonProperty("next_cursor") String nextCursor
        ) {
            this.results = results;
            this.nextCursor = nextCursor;
            this.errBody = null;
            this.shortCode = ShortCode.SUCCESS;
        }

        public TodoistRetrieverResponseRaw(
            String errBody
        ) {
            this.results = null;
            this.nextCursor = null;
            this.errBody = errBody;
            this.shortCode = ShortCode.SYSTEM_EXCEPTION;
        }

        public List<TodoistRetrieverTaskRaw> getResults() {
            return results;
        }

        public String getNextCursor() {
            return nextCursor;
        }

        public String getErrBody() {
            return errBody;
        }

        public ShortCode getShortCode() {
            return shortCode;
        }
    }

    @JsonPropertyOrder({ "numberOfTasks", "tasks" })
    public static class TodoistRetrieverResponse {
        private final List<TodoistRetrieverTask> tasks;
        private final Integer numberOfTasks;

        public TodoistRetrieverResponse(List<TodoistRetrieverTask> tasks) {
            this.tasks = tasks;
            this.numberOfTasks = tasks.size();
        }

        public List<TodoistRetrieverTask> getTasks() {
            return tasks;
        }

        public Integer getNumberOfTasks() {
            return numberOfTasks;
        }
    }

    public static class ZDTDuration {
        private final ZDTTime startTime;
        private final ZDTTime endTime;

        private ZDTDuration(ZDTTime startTime, ZDTTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public static ZDTDuration from(TodoistTaskDuration duration, TodoistTaskDue due,
            UserSettings userSettings) {

            ZDTTime start = getZDTFromTimestamp(due.date, userSettings);
            ZonedDateTime endZDT = start.zdt().plusMinutes(duration.amount);
            ZDTTime end = new ZDTTime(endZDT, getTimestampFromZDT(endZDT));

            return new ZDTDuration(start, end);
        }
    }

    public static class TodoistRetrieverTask {
        private final String taskId;
        private final String projectId;
        private final String sectionId;
        private final String parentId;
        private final List<String> labels;
        private final ZDTTime deadline;
        private final ZDTDuration duration;
        private final TodoistTaskDue due;
        private final Long priority;
        private final Long childOrder;
        private final String content;
        private final String description;
        private final Long noteCount;

        public TodoistRetrieverTask(String taskId, String projectId, String sectionId, String parentId,
            List<String> labels, ZDTTime deadline, ZDTDuration duration, TodoistTaskDue due,
            Long priority, Long childOrder, String content, String description, Long noteCount
        ) {
            this.taskId = taskId;
            this.projectId = projectId;
            this.sectionId = sectionId;
            this.parentId = parentId;
            this.labels = labels;
            this.deadline = deadline;
            this.duration = duration;
            this.due = due;
            this.priority = priority;
            this.childOrder = childOrder;
            this.content = content;
            this.description = description;
            this.noteCount = noteCount;
        }

        public static TodoistRetrieverTask copy(TodoistRetrieverTask t) {
            return new TodoistRetrieverTask(t.taskId, t.projectId, t.sectionId, t.parentId, t.labels,
                t.deadline, t.duration, t.due, t.priority, t.childOrder, t.content, t.description, t.noteCount
            );
        }

        public String getTaskId() {
            return taskId;
        }

        public String getProjectId() {
            return projectId;
        }

        public String getSectionId() {
            return sectionId;
        }

        public String getParentId() {
            return parentId;
        }

        public List<String> getLabels() {
            return labels;
        }

        public ZDTTime getDeadline() {
            return deadline;
        }

        public ZDTDuration getDuration() {
            return duration;
        }

        public TodoistTaskDue getDue() {
            return due;
        }

        public Long getPriority() {
            return priority;
        }

        public Long getChildOrder() {
            return childOrder;
        }

        public Integer getChildOrderAsInt() {
            return Math.toIntExact(this.childOrder);
        }

        public String getContent() {
            return content;
        }

        public String getDescription() {
            return description;
        }

        public Long getNoteCount() {
            return noteCount;
        }
    }

    public static class TodoistRequestSortingOptions {
        private final PostProcessing postProcessing;
        
        public TodoistRequestSortingOptions(
            @JsonProperty("postProcessingActions") List<String> postProcessingActionStrs
        ) {
            this.postProcessing = new PostProcessing();
            postProcessingActionStrs.forEach(str -> this.postProcessing.addOrderingAction(PostProcessingAction.fromLabel(str)
                .orElseThrow(() -> new ControllerException(String.format("Invalid PostProcessingAction " +
                    "'%s'", str), BAD_REQUEST))));
        }

        public PostProcessing getPostProcessing() {
            return postProcessing;
        }
    }

    public static class TodoistRequestFilterOption {
        private final String filterName;
        private final Object value;
        private final Object startValue;
        private final Object endValue;
        private final TodoistPriority todoistPriority;
        private final List<TodoistPriority> todoistPriorities;

        public TodoistRequestFilterOption(
            @JsonProperty("filterName") String filterName,
            @JsonProperty("value") Object value,
            @JsonProperty("startValue") Object startValue,
            @JsonProperty("endValue") Object endValue,
            @JsonProperty("priority") String todoistPriorityStr,
            @JsonProperty("priorities") List<TodoistPriority> todoistPriorities
        ) {
            this.filterName = filterName;
            this.value = value;
            this.startValue = startValue;
            this.endValue = endValue;

            if (todoistPriorityStr != null) {
                this.todoistPriority = TodoistPriority.fromLabel(todoistPriorityStr)
                    .orElseThrow(() -> new ControllerException(String.format(
                        "Invalid Todoist Priority Value: '%s'", todoistPriorityStr
                    ), BAD_REQUEST));
            } else {
                this.todoistPriority = null;
            }

            this.todoistPriorities = todoistPriorities;
        }

        public String getFilterName() {
            return filterName;
        }

        public Object getValue() {
            return value;
        }

        public Object getStartValue() {
            return startValue;
        }

        public Object getEndValue() {
            return endValue;
        }

        public TodoistPriority getTodoistPriority() {
            return todoistPriority;
        }

        public List<TodoistPriority> getTodoistPriorities() {
            return todoistPriorities;
        }
    }

    public record TodoistRequestOptions(
         TodoistRequestSortingOptions sortingOptions,
         List<TodoistRequestFilterOption> filterOptions
    ) {}

    public record GetTodoistProjectsResults(
        String id,
        String name,
        @JsonProperty("view_style") String viewStyle,
        String description,
        @JsonProperty("inbox_project") Boolean inboxProject
    ){}

    public record GetTodoistProjectsResponse(
        List<GetTodoistProjectsResults> results
    ){}

    public record GetTodoistTasksResultDue(
       String date,
       String timezone,
       @JsonProperty("string") String friendlyDate,
       @JsonProperty("is_recurring") Boolean isRecurring
    ){}

    public record GetTodoistTasksResultDeadline(
        String date
    ){}

    public record GetTodoistTasksResultDuration(
        Integer amount,
        String unit
    ){}

    public static class GetTodoistTasksResult {
        private String id;
        private String projectId;
        private String sectionId;
        private List<String> labels;
        private GetTodoistTasksResultDeadline deadline;
        private GetTodoistTasksResultDuration duration;
        private Boolean isDeleted;
        private GetTodoistTasksResultDue due;
        private Integer priorityIntVal;
        private TodoistPriority priority;
        private String content;
        private String description;
        private List<GetTodoistTasksResultDue> reminders;

        public GetTodoistTasksResult(String id, String projectId, String sectionId, List<String> labels,
            GetTodoistTasksResultDeadline deadline, GetTodoistTasksResultDuration duration,
            @JsonProperty("is_deleted") Boolean isDeleted, GetTodoistTasksResultDue due, Integer priority,
            String content, String description) {

            this.id = id;
            this.projectId = projectId;
            this.sectionId = sectionId;
            this.labels = labels;
            this.deadline = deadline;
            this.duration = duration;
            this.isDeleted = isDeleted;
            this.due = due;
            this.priorityIntVal = priority;
            this.priority = TodoistPriority.fromIntVal(priority);
            this.content = content;
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public String getProjectId() {
            return projectId;
        }

        public String getSectionId() {
            return sectionId;
        }

        public List<String> getLabels() {
            return labels;
        }

        public GetTodoistTasksResultDeadline getDeadline() {
            return deadline;
        }

        public GetTodoistTasksResultDuration getDuration() {
            return duration;
        }

        public Boolean getDeleted() {
            return isDeleted;
        }

        public GetTodoistTasksResultDue getDue() {
            return due;
        }

        @JsonIgnore
        public Date getDueDate() {
            if (due == null || due.date() == null) {
                return null;
            }

            String dateStr = due.date();

            try {
                // Date + time
                LocalDateTime ldt = LocalDateTime.parse(dateStr);
                return Date.from(
                    ldt.atZone(ZoneId.systemDefault()).toInstant()
                );
            } catch (DateTimeParseException e) {
                // Date only
                LocalDate ld = LocalDate.parse(dateStr);
                return Date.from(
                    ld.atStartOfDay(ZoneId.systemDefault()).toInstant()
                );
            }
        }

        public Integer getPriorityIntVal() {
            return priorityIntVal;
        }

        public TodoistPriority getPriority() {
            return priority;
        }

        public String getContent() {
            return content;
        }

        public String getDescription() {
            return description;
        }

        public List<GetTodoistTasksResultDue> getReminders() {
            return reminders;
        }

        public void setReminders(List<GetTodoistTasksResultDue> reminders) {
            this.reminders = reminders;
        }
    }

    public record GetTodoistTasksResponse(
       List<GetTodoistTasksResult> results
    ){}

    // Returned to the user
    public record GetTodoistLabel(
        String name,
        String colorName,
        String rgbaString
    ) {}

    // Returned to the user
    public record GetTodoistLabelsResponse(
        List<GetTodoistLabel> labels
    ) {}

    // Used with the Todoist API
    public record GetTodoistAPILabel(
        String name,
        String color
    ) {}

    // Used with the Todoist API
    public record GetTodoistAPILabelsResponse(
        List<GetTodoistAPILabel> results
    ) {}

    public record GetTodoistAPIReminder(
        GetTodoistTasksResultDue due,
        @JsonProperty("item_id") String itemId
    ) {}

    public static class GetTodoistAPIReminders {
        Map<String, List<GetTodoistAPIReminder>> remindersMap = new HashMap<>();

        @JsonCreator
        public GetTodoistAPIReminders(@JsonProperty("reminders") List<GetTodoistAPIReminder> reminders) {
            reminders.forEach(r -> {
                if (!remindersMap.containsKey(r.itemId())) {
                    List<GetTodoistAPIReminder> newList = new ArrayList<>();
                    newList.add(r);
                    remindersMap.put(r.itemId(), newList);
                } else {
                    this.remindersMap.get(r.itemId()).add(r);
                }
            });
        }

        public Optional<List<GetTodoistAPIReminder>> getReminderByTaskId(String taskId) {
            return Optional.ofNullable(this.remindersMap.get(taskId));
        }
    }

    public static class GetTodoistAPIRemindersRequest {
        private final String syncToken;
        private final List<String> resourceTypes;

        public GetTodoistAPIRemindersRequest() {
            this.syncToken = "*";
            this.resourceTypes = List.of("reminders");
        }

        @JsonProperty("sync_token")
        public String getSyncToken() {
            return syncToken;
        }

        @JsonProperty("resource_types")
        public List<String> getResourceTypes() {
            return resourceTypes;
        }
    }
}
