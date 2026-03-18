package com.davidruffner.homecontrollerbackend.controllers;

import com.davidruffner.homecontrollerbackend.dtos.InventoryDTO.GetAllItemsByRoomResponse;
import com.davidruffner.homecontrollerbackend.dtos.InventoryDTO.GetAllRoomsResponse;
import com.davidruffner.homecontrollerbackend.entities.inventory.Room;
import com.davidruffner.homecontrollerbackend.repositories.inventory.CategoryRepository;
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
            return new ResponseEntity<>(new GetAllItemsByRoomResponse(null, BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
        Room room = roomOpt.get();

        if (room.getItems().isEmpty()) {
            return new ResponseEntity<>(new GetAllItemsByRoomResponse(null, NO_ROOM_ITEMS), OK);
        }

        return new ResponseEntity<>(GetAllItemsByRoomResponse.fromItems(room.getItems(), SUCCESS), OK);
    }
}
