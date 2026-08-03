package com.davidruffner.homecontrollerbackend.dtos;

import com.davidruffner.homecontrollerbackend.entities.inventory.InventoryCategory;
import com.davidruffner.homecontrollerbackend.entities.inventory.InventoryItem;
import com.davidruffner.homecontrollerbackend.entities.inventory.ItemContainer;
import com.davidruffner.homecontrollerbackend.entities.inventory.InventoryRoom;
import com.davidruffner.homecontrollerbackend.enums.ShortCode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InventoryDTO {
    public record GetAllRoomsResponse(
        List<GetAllRoomsRoom> rooms,
        ShortCode shortCode
    ) {
        public static GetAllRoomsResponse fromRooms(List<InventoryRoom> rooms, ShortCode shortCode) {
            return new GetAllRoomsResponse(rooms != null ?
                rooms.stream()
                    .map(GetAllRoomsRoom::new)
                    .toList()
                : null,
                shortCode
            );
        }
    }

    public record GetAllRoomsRoom(
        String roomId,
        String roomName
    ) {
        public GetAllRoomsRoom(InventoryRoom room) {
            this(room.getRoomId(), room.getRoomName());
        }
    }

    public record GetAllItemsForCategoryResponse(
        List<GetAllItemsByRoomItem> items,
        ShortCode shortCode
    ) {
        public static GetAllItemsForCategoryResponse fromItems(List<InventoryItem> items, ShortCode shortCode) {
            return new GetAllItemsForCategoryResponse(
                items != null
                    ? items.stream()
                    .map(GetAllItemsByRoomItem::new)
                    .toList()
                    : null,
                shortCode
            );
        }
    }

    public record GetAllCategoriesResponse(
        List<GetAllCategoriesCategory> categories,
        ShortCode shortCode
    ) {
        public static GetAllCategoriesResponse fromCategories(List<InventoryCategory> categories, ShortCode shortCode) {
            return new GetAllCategoriesResponse(
                categories != null
                    ? categories.stream()
                    .map(GetAllCategoriesCategory::new)
                    .toList()
                    : null,
                shortCode
            );
        }
    }

    public record GetAllCategoriesCategory(
        String categoryId,
        String categoryName
    ) {
        public GetAllCategoriesCategory(InventoryCategory category) {
            this(category.getCategoryId(), category.getCategoryName());
        }
    }

    public record GetAllContainersByRoomResponse(
        List<GetAllContainersByRoomContainer> itemContainers,
        ShortCode shortCode
    ) {
        public static GetAllContainersByRoomResponse fromContainers(List<ItemContainer> containers,
            ShortCode shortCode) {
            return new GetAllContainersByRoomResponse(
                containers != null
                    ? containers.stream()
                    .map(GetAllContainersByRoomContainer::new)
                    .toList()
                    : null,
                shortCode
            );
        }

        public static GetAllContainersByRoomResponse withError(ShortCode shortCode) {
            return new GetAllContainersByRoomResponse(null, shortCode);
        }
    }

    public record GetAllContainersByRoomContainer(
        String containerId,
        String containerName
    ) {
        public GetAllContainersByRoomContainer(ItemContainer itemContainer) {
            this(
                itemContainer.getContainerId(),
                itemContainer.getContainerName()
            );
        }
    }

    public record GetAllItemsByContainerResponse(
        List<GetAllItemsByRoomItem> items,
        ShortCode shortCode
    ) {
        public static GetAllItemsByContainerResponse fromItems(List<InventoryItem> items, ShortCode shortCode) {
            return new GetAllItemsByContainerResponse(
                items != null
                    ? items.stream()
                    .map(GetAllItemsByRoomItem::new)
                    .toList()
                    : null,
                shortCode
            );
        }
    }

    public record GetAllItemsByRoomResponse(
        List<GetAllItemsByRoomItem> items,
        ShortCode shortCode,
        String errMsg
    ) {
        public static GetAllItemsByRoomResponse fromItems(List<InventoryItem> items, ShortCode shortCode) {
            return new GetAllItemsByRoomResponse(
                items != null
                    ? items.stream()
                    .map(GetAllItemsByRoomItem::new)
                    .toList()
                    : null,
                shortCode,
                null
            );
        }

        public static GetAllItemsByRoomResponse withError(ShortCode shortCode, String errMsg) {
            return new GetAllItemsByRoomResponse(null, shortCode, errMsg);
        }
    }

    public record GetAllItemsByRoomItem(
        String itemId,
        String itemName,
        String description,
        String upc,
        Long quantity,
        String categoryId,
        String categoryName,
        String roomId,
        String roomName,
        String itemContainerId,
        String itemContainerName,
        Boolean isQuantityAtThreshold
    ) {
        public GetAllItemsByRoomItem(InventoryItem item) {
            this(
                item.getItemId(),
                item.getItemName(),
                item.getItemDescription(),
                item.getUpc(),
                item.getQuantity(),
                item.getInventoryCategory() != null ? item.getInventoryCategory().getCategoryId() : null,
                item.getInventoryCategory() != null ? item.getInventoryCategory().getCategoryName() : null,
                item.getInventoryRoom() != null ? item.getInventoryRoom().getRoomId() : null,
                item.getInventoryRoom() != null ? item.getInventoryRoom().getRoomName() : null,
                item.getItemContainer() != null ? item.getItemContainer().getContainerId() : null,
                item.getItemContainer() != null ? item.getItemContainer().getContainerName() : null,
                item.getQuantity() <= item.getQuantityThreshold()
            );
        }
    }

    public record SearchItemsResponse(
        List<GetAllItemsByRoomItem> items,
        ShortCode shortCode
    ) {
        public static SearchItemsResponse fromItems(List<InventoryItem> items, ShortCode shortCode) {
            return new SearchItemsResponse(
                items != null
                    ? items.stream()
                    .map(GetAllItemsByRoomItem::new)
                    .toList()
                    : null,
                shortCode
            );
        }
    }

    public record SearchItemsRequest(
        String searchTerm,
        List<SearchItemsRequestFilters> searchFilters,
        List<SearchItemsRequestQuantityFilters> searchQuantityFilters
    ) {
        public Optional<String> searchTermOpt() {
            return Optional.ofNullable(searchTerm);
        }
    }

    public record SearchItemsRequestFilters(
        String searchFilterType,
        String searchFilterId
    ) {
        public Optional<String> searchFilterTypeOpt() {
            return Optional.ofNullable(searchFilterType);
        }

        public Optional<String> searchFilterIdOpt() {
            return Optional.ofNullable(searchFilterId);
        }
    }

    public record SearchItemsRequestQuantityFilters(
        String searchQuantityType,
        Integer searchQuantityVal
    ) {
        public Optional<String> searchQuantityTypeOpt() {
            return Optional.ofNullable(searchQuantityType);
        }

        public Optional<Integer> searchQuantityValOpt() {
            return Optional.ofNullable(searchQuantityVal);
        }
    }

    public enum SearchQuantityType {
        EQUAL("equal"),
        LESS_THAN("less_than"),
        LESS_THAN_OR_EQUAL("less_than_or_equal"),
        MORE_THAN("more_than"),
        MORE_THAN_OR_EQUAL("more_than_or_equal"),
        IS_IN_THRESHOLD("is_in_threshold"),
        IS_OUT_OF_THRESHOLD("is_out_of_threshold");

        private final String name;

        private static final Map<String, SearchQuantityType> strMap =
            Arrays.stream(values())
                .collect(Collectors.toMap(
                    SearchQuantityType::getName,
                    Function.identity()
                ));

        SearchQuantityType(String name) { this.name = name; }

        public String getName() { return name; }

        public static Optional<SearchQuantityType> fromName(String name) {
            if (strMap.containsKey(name)) {
                return Optional.of(strMap.get(name));
            } else {
                return Optional.empty();
            }
        }

        @Override
        public String toString() { return this.name; }
    }

    public enum SearchFilterType {
        CATEGORY("category"),
        ROOM("room"),
        ROOM_WITH_CONTAINER("room_with_container");

        private final String name;

        private static final Map<String, SearchFilterType> strMap =
            Arrays.stream(values())
                .collect(Collectors.toMap(
                    SearchFilterType::getName,
                    Function.identity()
                ));

        SearchFilterType(String name) { this.name = name; }

        public String getName() { return name; }

        public static Optional<SearchFilterType> fromName(String name) {
            if (strMap.containsKey(name)) {
                return Optional.of(strMap.get(name));
            } else {
                return Optional.empty();
            }
        }

        @Override
        public String toString() { return this.name; }
    }
}
