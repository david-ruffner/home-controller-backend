package com.davidruffner.homecontrollerbackend.dtos;

import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.enums.ShortCode;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import com.davidruffner.homecontrollerbackend.services.TodoistRetriever;
import com.davidruffner.homecontrollerbackend.services.TodoistRetriever.PostProcessing;
import com.davidruffner.homecontrollerbackend.services.TodoistRetriever.PostProcessingAction;
import com.davidruffner.homecontrollerbackend.utils.Utils.ZDTTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public record TodoistRequestOptions(
         TodoistRequestSortingOptions sortingOptions
    ) {}
}
