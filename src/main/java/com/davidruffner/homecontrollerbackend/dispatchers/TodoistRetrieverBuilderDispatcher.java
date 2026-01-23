package com.davidruffner.homecontrollerbackend.dispatchers;

import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.enums.TodoistPriority;
import com.davidruffner.homecontrollerbackend.enums.TodoistProduct;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import com.davidruffner.homecontrollerbackend.services.TodoistRetriever;
import com.davidruffner.homecontrollerbackend.services.TodoistRetriever.Builder;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Map.entry;

public final class TodoistRetrieverBuilderDispatcher {
    private static final Map<String, BiFunction<Builder, String, Builder>> REGULAR_FILTER_SETTERS =
        Map.ofEntries(
            entry("byUserId", Builder::byUserId),
            entry("byTaskId", Builder::byTaskId),
            entry("byProjectId", Builder::byProjectId),
            entry("bySectionId", Builder::bySectionId),
            entry("byParentId", Builder::byParentId),
            entry("byAddedUID", Builder::byAddedUID),
            entry("byAssignedUID", Builder::byAssignedUID),
            entry("byResponsibleUID", Builder::byResponsibleUID)
        );

    private static final Map<String, BiFunction<Builder, String, Builder>> BI_QUERY_FILTER_STR_SETTERS = Map.ofEntries(
        entry("bySearchTerm", Builder::bySearchTerm),
        entry("onDate", Builder::onDate),
        entry("beforeDate", Builder::beforeDate),
        entry("afterDate", Builder::afterDate),
        entry("dueOn", Builder::dueOn),
        entry("dueBefore", Builder::dueBefore),
        entry("dueAfter", Builder::dueAfter),
        entry("inProject", Builder::inProject),
        entry("parentTaskId", Builder::getSubTasks)
    );

    private static final Map<String, BiFunction<Builder, Integer, Builder>> BI_QUERY_FILTER_INT_SETTERS =
        Map.ofEntries(
            entry("dueInNextNDays", Builder::dueInNextNDays)
        );

    private static final Map<String, TriFunction<Builder, String, String, Builder>> TRI_QUERY_FILTER_SETTERS =
        Map.ofEntries(
            entry("inBetweenDate", Builder::inBetweenDate),
            entry("dueInBetween", Builder::dueInBetween)
        );

    private static final Map<String, Function<Builder, Builder>> SG_QUERY_FILTER_SETTERS =
        Map.ofEntries(
            entry("recurring", Builder::recurring),
            entry("noDate", Builder::noDate),
            entry("inInbox", Builder::inInbox)
        );

    private static final Map<String, BiFunction<Builder, TodoistPriority, Builder>> BI_QUERY_FILTER_TP_SETTERS =
        Map.ofEntries(
            entry("isPriority", Builder::isPriority)
        );

    private static final Map<String, BiFunction<Builder, List<TodoistPriority>, Builder>> BI_QUERY_FILTER_LTP_SETTERS =
        Map.ofEntries(
            entry("isOneOfPriority", Builder::isOneOfPriority)
        );

    public static TodoistRetriever.Builder apply(TodoistRetriever.Builder b, String name, String value) {
        var fn = REGULAR_FILTER_SETTERS.get(name);
        if (fn == null) {
            fn = BI_QUERY_FILTER_STR_SETTERS.get(name);

            if (fn == null) {
                throw new ControllerException(String.format("Unknown Todoist Retriever Builder Method '%s'",
                    name), ResponseCode.BAD_REQUEST);
            }

            if (b.getTodoistProduct().isEmpty()) {
                b.changeProduct(TodoistProduct.FILTERED_TASKS); // /tasks/filter?query endpoint
            }
        }

        if (b.getTodoistProduct().isEmpty()) {
            b.changeProduct(TodoistProduct.TASKS); // Regular /tasks endpoint
        }

        return fn.apply(b, value);
    }

    public static TodoistRetriever.Builder apply(TodoistRetriever.Builder b, String name, Integer value) {
        var fn = BI_QUERY_FILTER_INT_SETTERS.get(name);
        if (fn == null) {
            throw new ControllerException(String.format("Unknown Todoist Retriever Builder Method '%s'",
                name), ResponseCode.BAD_REQUEST);
        }

        if (b.getTodoistProduct().isEmpty()) {
            b.changeProduct(TodoistProduct.FILTERED_TASKS);
        }

        return fn.apply(b, value);
    }

    public static TodoistRetriever.Builder apply(TodoistRetriever.Builder b, String name, String val1, String val2) {
        var fn = TRI_QUERY_FILTER_SETTERS.get(name);
        if (fn == null) {
            throw new ControllerException(String.format("Unknown Todoist Retriever Builder Method '%s'",
                name), ResponseCode.BAD_REQUEST);
        }

        if (b.getTodoistProduct().isEmpty()) {
            b.changeProduct(TodoistProduct.FILTERED_TASKS);
        }

        return fn.apply(b, val1, val2);
    }

    public static TodoistRetriever.Builder apply(TodoistRetriever.Builder b, String name) {
        var fn = SG_QUERY_FILTER_SETTERS.get(name);
        if (fn == null) {
            throw new ControllerException(String.format("Unknown Todoist Retriever Builder Method '%s'",
                name), ResponseCode.BAD_REQUEST);
        }

        if (b.getTodoistProduct().isEmpty()) {
            b.changeProduct(TodoistProduct.FILTERED_TASKS);
        }

        return fn.apply(b);
    }

    public static TodoistRetriever.Builder apply(TodoistRetriever.Builder b, String name, TodoistPriority value) {
        var fn = BI_QUERY_FILTER_TP_SETTERS.get(name);
        if (fn == null) {
            throw new ControllerException(String.format("Unknown Todoist Retriever Builder Method '%s'",
                name), ResponseCode.BAD_REQUEST);
        }

        if (b.getTodoistProduct().isEmpty()) {
            b.changeProduct(TodoistProduct.FILTERED_TASKS);
        }

        return fn.apply(b, value);
    }

    public static TodoistRetriever.Builder apply(TodoistRetriever.Builder b, String name,
        List<TodoistPriority> value
    ) {
        var fn = BI_QUERY_FILTER_LTP_SETTERS.get(name);
        if (fn == null) {
            throw new ControllerException(String.format("Unknown Todoist Retriever Builder Method '%s'",
                name), ResponseCode.BAD_REQUEST);
        }

        if (b.getTodoistProduct().isEmpty()) {
            b.changeProduct(TodoistProduct.FILTERED_TASKS);
        }

        return fn.apply(b, value);
    }
}
