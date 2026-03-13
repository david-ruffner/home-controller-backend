package com.davidruffner.homecontrollerbackend.caching;

import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS.GetTodoistSyncTask;
import com.davidruffner.homecontrollerbackend.dtos.TodoistDTOS.GetTodoistTaskCachedDTO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.UUID;

public class TodoistPaginatedItems {
    private final String paginationToken;
    private final Integer totalItems;
    private final List<GetTodoistTaskCachedDTO> items;

    public TodoistPaginatedItems(
        RedisTemplate<String, Object> redisTemplate,
        List<GetTodoistTaskCachedDTO> items
    ) {
        String pagToken = UUID.randomUUID().toString();
        while (redisTemplate.hasKey(pagToken)) {
            pagToken = UUID.randomUUID().toString();
        }

        this.paginationToken = pagToken;
        this.items = items;
        this.totalItems = items.size();
    }

    @JsonCreator
    public TodoistPaginatedItems(
        @JsonProperty("items") List<GetTodoistTaskCachedDTO> items,
        @JsonProperty("paginationToken") String paginationToken
    ) {
        this.paginationToken = paginationToken;
        this.items = items;
        this.totalItems = items.size();
    }

    public String getPaginationToken() {
        return paginationToken;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public List<GetTodoistTaskCachedDTO> getItems() {
        return items;
    }
}
