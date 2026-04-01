package com.davidruffner.homecontrollerbackend.controllers;

import com.davidruffner.homecontrollerbackend.dtos.InventoryDTO;
import com.davidruffner.homecontrollerbackend.dtos.InventoryDTO.*;
import com.davidruffner.homecontrollerbackend.entities.inventory.Category;
import com.davidruffner.homecontrollerbackend.entities.inventory.Item;
import com.davidruffner.homecontrollerbackend.entities.inventory.ItemContainer;
import com.davidruffner.homecontrollerbackend.entities.inventory.Room;
import com.davidruffner.homecontrollerbackend.repositories.inventory.CategoryRepository;
import com.davidruffner.homecontrollerbackend.repositories.inventory.ItemContainerRepository;
import com.davidruffner.homecontrollerbackend.repositories.inventory.ItemRepository;
import com.davidruffner.homecontrollerbackend.repositories.inventory.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static com.davidruffner.homecontrollerbackend.enums.ShortCode.*;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    ItemRepository itemRepo;

    @Autowired
    CategoryRepository categoryRepo;

    @Autowired
    RoomRepository roomRepo;

    @Autowired
    ItemContainerRepository itemContainerRepo;

    @GetMapping("/getAllRooms")
    public ResponseEntity<GetAllRoomsResponse> getAllRooms() {
        List<Room> rooms = this.roomRepo.fetchAllRooms();
        if (rooms.isEmpty()) {
            return new ResponseEntity<>(new GetAllRoomsResponse(null, NO_ROOMS), OK);
        }

        return new ResponseEntity<>(GetAllRoomsResponse.fromRooms(rooms, SUCCESS), OK);
    }


    @GetMapping("/getAllItemsByRoom/{roomId}")
    public ResponseEntity<GetAllItemsByRoomResponse> getAllItemsByRoom(@PathVariable String roomId) {
        Optional<Room> roomOpt = this.roomRepo.fetchById(roomId);
        if (roomOpt.isEmpty()) {
            return new ResponseEntity<>(GetAllItemsByRoomResponse.fromItems(null, NO_ROOM_ITEMS), OK);
        }
        Room room = roomOpt.get();

        return new ResponseEntity<>(GetAllItemsByRoomResponse.fromItems(room.getItems(), SUCCESS), OK);
    }

    @GetMapping("/getAllItemsByContainerId/{containerId}")
    public ResponseEntity<GetAllItemsByContainerResponse> getAllItemsByContainerId(@PathVariable String containerId) {
        List<Item> items = this.itemRepo.getItemsByContainerId(containerId);

        if (items.isEmpty()) {
            return new ResponseEntity<>(GetAllItemsByContainerResponse.fromItems(null, NO_ROOM_ITEMS), OK);
        } else {
            return new ResponseEntity<>(GetAllItemsByContainerResponse.fromItems(items, SUCCESS), OK);
        }
    }

    @GetMapping("/getAllContainersForRoom/{roomId}")
    public ResponseEntity<GetAllContainersByRoomResponse> getAllContainersForRoom(
        @PathVariable String roomId) {
        List<ItemContainer> containers = this.itemContainerRepo.getContainersByRoomId(roomId);

        if (containers.isEmpty()) {
            return new ResponseEntity<>(GetAllContainersByRoomResponse.withError(NO_ITEM_CONTAINERS), OK);
        } else {
            return new ResponseEntity<>(GetAllContainersByRoomResponse.fromContainers(containers, SUCCESS), OK);
        }
    }

    @GetMapping("/getAllCategories")
    public ResponseEntity<GetAllCategoriesResponse> getAllCategories() {
        List<Category> categories = this.categoryRepo.findAll();

        if (categories.isEmpty()) {
            return new ResponseEntity<>(GetAllCategoriesResponse.fromCategories(null, NO_CATEGORIES), OK);
        } else {
            return new ResponseEntity<>(GetAllCategoriesResponse.fromCategories(categories, SUCCESS), OK);
        }
    }

    @GetMapping("/getAllItemsForCategory/{categoryId}")
    public ResponseEntity<GetAllItemsForCategoryResponse> getAllItemsForCategory(@PathVariable String categoryId) {
        List<Item> items = this.itemRepo.getItemsByCategoryId(categoryId);

        if (items.isEmpty()) {
            return new ResponseEntity<>(GetAllItemsForCategoryResponse.fromItems(null, NO_ITEMS_FOR_CATEGORY), OK);
        } else {
            return new ResponseEntity<>(GetAllItemsForCategoryResponse.fromItems(items, SUCCESS), OK);
        }
    }
}
