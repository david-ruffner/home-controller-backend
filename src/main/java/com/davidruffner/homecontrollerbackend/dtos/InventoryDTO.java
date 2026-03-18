package com.davidruffner.homecontrollerbackend.dtos;

import com.davidruffner.homecontrollerbackend.entities.inventory.Item;
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

    public record GetAllItemsByRoomResponse(
        List<GetAllItemsByRoomItem> items,
        ShortCode shortCode
    ) {
        public static GetAllItemsByRoomResponse fromItems(List<Item> items, ShortCode shortCode) {
            return new GetAllItemsByRoomResponse(
                items != null
                    ? items.stream()
                    .map(GetAllItemsByRoomItem::new)
                    .toList()
                    : null,
                shortCode
            );
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
        String roomName
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
                item.getRoom() != null ? item.getRoom().getRoomName() : null
            );
        }
    }
}
