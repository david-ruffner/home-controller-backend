package com.davidruffner.homecontrollerbackend.dtos;

import com.davidruffner.homecontrollerbackend.entities.inventory.Category;
import com.davidruffner.homecontrollerbackend.entities.inventory.Item;
import com.davidruffner.homecontrollerbackend.entities.inventory.ItemContainer;
import com.davidruffner.homecontrollerbackend.entities.inventory.Room;
import com.davidruffner.homecontrollerbackend.enums.ShortCode;

import java.util.List;

public class InventoryDTO {
    public record GetAllRoomsResponse(
        List<GetAllRoomsRoom> rooms,
        ShortCode shortCode
    ) {
        public static GetAllRoomsResponse fromRooms(List<Room> rooms, ShortCode shortCode) {
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
        public GetAllRoomsRoom(Room room) {
            this(room.getRoomId(), room.getRoomName());
        }
    }

    public record GetAllItemsForCategoryResponse(
        List<GetAllItemsByRoomItem> items,
        ShortCode shortCode
    ) {
        public static GetAllItemsForCategoryResponse fromItems(List<Item> items, ShortCode shortCode) {
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
        public static GetAllCategoriesResponse fromCategories(List<Category> categories, ShortCode shortCode) {
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
        public GetAllCategoriesCategory(Category category) {
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
        public static GetAllItemsByContainerResponse fromItems(List<Item> items, ShortCode shortCode) {
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
        public static GetAllItemsByRoomResponse fromItems(List<Item> items, ShortCode shortCode) {
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
        public GetAllItemsByRoomItem(Item item) {
            this(
                item.getItemId(),
                item.getItemName(),
                item.getDescription(),
                item.getUpc(),
                item.getQuantity(),
                item.getCategory() != null ? item.getCategory().getCategoryId() : null,
                item.getCategory() != null ? item.getCategory().getCategoryName() : null,
                item.getRoom() != null ? item.getRoom().getRoomId() : null,
                item.getRoom() != null ? item.getRoom().getRoomName() : null,
                item.getItemContainer() != null ? item.getItemContainer().getContainerId() : null,
                item.getItemContainer() != null ? item.getItemContainer().getContainerName() : null,
                item.getQuantity() <= item.getQuantityThreshold()
            );
        }
    }
}
