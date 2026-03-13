package com.davidruffner.homecontrollerbackend.services;

import com.davidruffner.homecontrollerbackend.caching.TodoistPaginatedItems;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS.*;
import com.davidruffner.homecontrollerbackend.enums.ShortCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.davidruffner.homecontrollerbackend.enums.ShortCode.PAGINATION_EXPIRED;
import static com.davidruffner.homecontrollerbackend.enums.ShortCode.SUCCESS;

@Service
public class TodoistPaginationService {

    private final RedisTemplate<String, Object> redisTemplate;

    public TodoistPaginationService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<String> cacheTasks(List<GetTodoistSyncTask> tasks, Integer pageSize) {
        int currentItemNumber = 1;
        List<GetTodoistTaskCachedDTO> partitionedTasks = new ArrayList<>();
        List<String> partitionKeys = new ArrayList<>();

        for (GetTodoistSyncTask task : tasks) {
            List<GetTodoistTaskCachedDueDTO> convertedReminders = new ArrayList<>();
            task.getReminders().forEach(r -> convertedReminders.add(new GetTodoistTaskCachedDueDTO(r)));

            GetTodoistTaskCachedDTO cachedTask = new GetTodoistTaskCachedDTO(
                task.getId(),
                task.getProjectId(),
                task.getSectionId(),
                task.getParentId(),
                task.getLabels(),
                new GetTodoistTaskCachedDeadlineDTO(task.getDeadline()),
                new GetTodoistTaskCachedDurationDTO(task.getDuration()),
                task.getDeleted(),
                new GetTodoistTaskCachedDueDTO(task.getDue()),
                task.getPriority(),
                task.getContent(),
                task.getDescription(),
                convertedReminders
            );

            if (currentItemNumber++ <= pageSize) {
                partitionedTasks.add(cachedTask);
            } else {
                TodoistPaginatedItems paginatedItems =
                    new TodoistPaginatedItems(this.redisTemplate, partitionedTasks);

                String key = "task:" + paginatedItems.getPaginationToken();
                this.redisTemplate.opsForValue().set(key, paginatedItems, 15, TimeUnit.MINUTES);
                partitionKeys.add(paginatedItems.getPaginationToken());

                // start a new batch with the current task
                currentItemNumber = 2; // because we’re adding task as item #1 now
                partitionedTasks = new ArrayList<>();
                partitionedTasks.add(cachedTask);
            }
        }

        // flush leftover (final partial batch)
        if (!partitionedTasks.isEmpty()) {
            TodoistPaginatedItems paginatedItems =
                new TodoistPaginatedItems(this.redisTemplate, partitionedTasks);

            String key = "task:" + paginatedItems.getPaginationToken();
            this.redisTemplate.opsForValue().set(key, paginatedItems, 15, TimeUnit.MINUTES);
            partitionKeys.add(paginatedItems.getPaginationToken());
        }

        return partitionKeys;
    }

    public TodoistPaginatedTasksResponseDTO retrieveTasks(String paginationKey, List<String> partitionKeys) {
        Object raw = this.redisTemplate.opsForValue().get("task:" + paginationKey);
        if (raw == null) return new TodoistPaginatedTasksResponseDTO(null, null, PAGINATION_EXPIRED);

        return new TodoistPaginatedTasksResponseDTO(partitionKeys,
            ((TodoistPaginatedItems) raw).getItems(), SUCCESS);
    }

    public TodoistPaginatedTasksResponseDTO retrieveTasks(String paginationKey) {
        Object raw = this.redisTemplate.opsForValue().get("task:" + paginationKey);
        if (raw == null) return new TodoistPaginatedTasksResponseDTO(null, null, PAGINATION_EXPIRED);

        return new TodoistPaginatedTasksResponseDTO(null, ((TodoistPaginatedItems) raw).getItems(), SUCCESS);
    }
}
