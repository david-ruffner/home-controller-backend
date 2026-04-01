package com.davidruffner.homecontrollerbackend.builders;

import com.davidruffner.homecontrollerbackend.dtos.InventoryDTO.SearchFilterType;
import com.davidruffner.homecontrollerbackend.dtos.InventoryDTO.SearchQuantityType;
import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.davidruffner.homecontrollerbackend.enums.ResponseCode.BAD_REQUEST;

public class InventorySearchQueryBuilder {
    private final StringBuilder sql = new StringBuilder();
    private final Map<String, Object> params = new LinkedHashMap<>();
    private boolean whereStarted = false;

    public InventorySearchQueryBuilder selectItems() {
        sql.append("SELECT item.item_id, item.item_name, category.category_id, category.category_name, item.description, item.quantity, item.upc, " +
            "room.room_id, room.room_name, item_container.container_id, item_container.container_name, item.quantity_threshold from item left join category on " +
            "item.category_id = category.category_id left join room on item.room_id = room.room_id left join " +
            "item_container on item.container_id = item_container.container_id ");

        return this;
    }

    public InventorySearchQueryBuilder whereSearchTerm(Object searchTerm) {
        if (!whereStarted) {
            sql.append("WHERE (item.item_name LIKE CONCAT('%', :itemName, '%') ");
            whereStarted = true;
        } else {
            sql.append("OR (item.item_name LIKE CONCAT('%', :itemName, '%') ");
        }

        sql.append("OR item.description LIKE CONCAT('%', :description, '%') OR item.upc LIKE CONCAT('%', :upc, '%')) ");
        params.put("itemName", searchTerm);
        params.put("description", searchTerm);
        params.put("upc", searchTerm);

        return this;
    }

    public InventorySearchQueryBuilder whereQuantity(String searchQuantityType, long quantity) {
        SearchQuantityType sqt = SearchQuantityType.fromName(searchQuantityType)
            .orElseThrow(() -> new ControllerException(String.format("SearchQuantityType '%s' is invalid",
                searchQuantityType), BAD_REQUEST));

        if (!whereStarted) {
            sql.append("WHERE (");
        } else {
            sql.append("AND (");
        }

        switch (sqt) {
            case EQUAL:
                sql.append("item.quantity = :quantity ");
                params.put("quantity", quantity);
                break;

            case LESS_THAN:
                sql.append("item.quantity < :quantity ");
                params.put("quantity", quantity);
                break;

            case LESS_THAN_OR_EQUAL:
                sql.append("item.quantity <= :quantity ");
                params.put("quantity", quantity);
                break;

            case MORE_THAN:
                sql.append("item.quantity > :quantity ");
                params.put("quantity", quantity);
                break;

            case MORE_THAN_OR_EQUAL:
                sql.append("item.quantity >= :quantity ");
                params.put("quantity", quantity);
                break;

            case IS_IN_THRESHOLD:
                sql.append("item.quantity <= item.quantity_threshold ");
                break;

            case IS_OUT_OF_THRESHOLD:
                sql.append("item.quantity > item.quantity_threshold ");
                break;
        }

        sql.append(") ");

        return this;
    }

    public InventorySearchQueryBuilder whereFilter(String searchFilterType, String idStr) {
        SearchFilterType sft = SearchFilterType.fromName(searchFilterType)
            .orElseThrow(() -> new ControllerException(String.format("SearchFilterType '%s' is invalid",
                searchFilterType), BAD_REQUEST));

        if (!whereStarted) {
            sql.append("WHERE (");
        } else {
            sql.append("AND (");
        }

        switch (sft) {
            case CATEGORY:
                sql.append("item.category_id = :categoryId ");
                params.put("categoryId", idStr);
                break;

            case ROOM:
                sql.append("item.room_id = :roomId ");
                params.put("roomId", idStr);
                break;

            case ROOM_WITH_CONTAINER:
                sql.append("item.container_id = :containerId ");
                params.put("containerId", idStr);
                break;
        }

        sql.append(") ");

        return this;
    }

    public String getSql() {
        return sql.toString().trim();
    }

    public Query build(EntityManager entityManager, Class<?> resultClass) {
        Query query = entityManager.createNativeQuery(getSql(), resultClass);
        params.forEach(query::setParameter);

        return query;
    }
}
