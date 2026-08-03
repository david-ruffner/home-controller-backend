package com.davidruffner.homecontrollerbackend.controllers;

import com.davidruffner.homecontrollerbackend.builders.InventorySearchQueryBuilder;
import com.davidruffner.homecontrollerbackend.dtos.InventoryDTO.*;
import com.davidruffner.homecontrollerbackend.entities.inventory.InventoryCategory;
import com.davidruffner.homecontrollerbackend.entities.inventory.InventoryItem;
import com.davidruffner.homecontrollerbackend.entities.inventory.ItemContainer;
import com.davidruffner.homecontrollerbackend.entities.inventory.InventoryRoom;
import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import com.davidruffner.homecontrollerbackend.repositories.inventory.CategoryRepository;
import com.davidruffner.homecontrollerbackend.repositories.inventory.InventoryRoomRepository;
import com.davidruffner.homecontrollerbackend.repositories.inventory.ItemContainerRepository;
import com.davidruffner.homecontrollerbackend.repositories.inventory.ItemRepository;
import com.davidruffner.homecontrollerbackend.repositories.RoomRepository;
import com.davidruffner.homecontrollerbackend.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    InventoryRoomRepository roomRepo;

    @Autowired
    ItemContainerRepository itemContainerRepo;

    @PersistenceContext
    EntityManager entityManager;

    @GetMapping("/getAllRooms")
    public ResponseEntity<GetAllRoomsResponse> getAllRooms() {
        List<InventoryRoom> rooms = this.roomRepo.getAllRooms();
        if (rooms.isEmpty()) {
            return new ResponseEntity<>(new GetAllRoomsResponse(null, NO_ROOMS), OK);
        }

        return new ResponseEntity<>(GetAllRoomsResponse.fromRooms(rooms, SUCCESS), OK);
    }


    @GetMapping("/getAllItemsByRoom/{roomId}")
    public ResponseEntity<GetAllItemsByRoomResponse> getAllItemsByRoom(@PathVariable String roomId) {
        Optional<InventoryRoom> roomOpt = this.roomRepo.findById(roomId);
        if (roomOpt.isEmpty()) {
            return new ResponseEntity<>(GetAllItemsByRoomResponse.fromItems(null, NO_ROOM_ITEMS), OK);
        }
        InventoryRoom room = roomOpt.get();

        return new ResponseEntity<>(GetAllItemsByRoomResponse.fromItems(room.getItems(), SUCCESS), OK);
    }

    @GetMapping("/getAllItemsByContainerId/{containerId}")
    public ResponseEntity<GetAllItemsByContainerResponse> getAllItemsByContainerId(@PathVariable String containerId) {
        List<InventoryItem> items = this.itemRepo.getItemsByContainerId(containerId);

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
        List<InventoryCategory> categories = this.categoryRepo.findAll();

        if (categories.isEmpty()) {
            return new ResponseEntity<>(GetAllCategoriesResponse.fromCategories(null, NO_CATEGORIES), OK);
        } else {
            return new ResponseEntity<>(GetAllCategoriesResponse.fromCategories(categories, SUCCESS), OK);
        }
    }

    @GetMapping("/getAllItemsForCategory/{categoryId}")
    public ResponseEntity<GetAllItemsForCategoryResponse> getAllItemsForCategory(@PathVariable String categoryId) {
        List<InventoryItem> items = this.itemRepo.getItemsByCategoryId(categoryId);

        if (items.isEmpty()) {
            return new ResponseEntity<>(GetAllItemsForCategoryResponse.fromItems(null, NO_ITEMS_FOR_CATEGORY), OK);
        } else {
            return new ResponseEntity<>(GetAllItemsForCategoryResponse.fromItems(items, SUCCESS), OK);
        }
    }

    @PostMapping("/searchItems")
    public ResponseEntity<SearchItemsResponse> searchItems(@RequestBody SearchItemsRequest body) {
        InventorySearchQueryBuilder sqlBuilder = new InventorySearchQueryBuilder().selectItems();

        if (!Utils.strEmpty(body.searchTerm())) {
            sqlBuilder.whereSearchTerm(body.searchTerm());
        }

        if (body.searchFilters() != null) {
            body.searchFilters().forEach(sf -> {
                if (sf.searchFilterTypeOpt().isPresent()) {
                    sqlBuilder.whereFilter(sf.searchFilterType(),
                        sf.searchFilterIdOpt().orElseThrow(() -> new ControllerException(
                            "Requested filter type search, but did not provide a searchFilterId", ResponseCode.BAD_REQUEST)));
                }
            });
        }

        if (body.searchQuantityFilters() != null) {
            body.searchQuantityFilters().forEach(sqf -> {
                if (sqf.searchQuantityTypeOpt().isPresent()) {
                    sqlBuilder.whereQuantity(sqf.searchQuantityType(),
                        sqf.searchQuantityValOpt().orElseThrow(() -> new ControllerException(
                            "Requested quantity type search, but did not provide a quantity value", ResponseCode.BAD_REQUEST)));
                }
            });
        }

        List<InventoryItem> items = sqlBuilder
            .build(entityManager, InventoryItem.class)
            .getResultList();

        return new ResponseEntity<>(SearchItemsResponse.fromItems(items, SUCCESS), OK);
    }
}
