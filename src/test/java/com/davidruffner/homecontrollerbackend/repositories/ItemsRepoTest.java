package com.davidruffner.homecontrollerbackend.repositories;

import com.davidruffner.homecontrollerbackend.entities.inventory.Category;
import com.davidruffner.homecontrollerbackend.entities.inventory.Item;
import com.davidruffner.homecontrollerbackend.entities.inventory.Room;
import com.davidruffner.homecontrollerbackend.repositories.inventory.CategoryRepository;
import com.davidruffner.homecontrollerbackend.repositories.inventory.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemsRepoTest {

    @Autowired
    ItemRepository itemRepo;

    @Autowired
    CategoryRepository categoryRepo;

    @Autowired
    RoomRepository roomRepo;

    @Test
    public void testInventoryEntities() {
        Category cat = new Category();
        cat.setCategoryName("Medicine");
        String categoryId = cat.getCategoryId();

        Room room = new Room();
        room.setRoomName("Kitchen");
        String roomId = room.getRoomId();

        Item item = new Item();
        item.setItemName("My Item");
        item.setDescription("Description");
        item.setUpc("FJJFD8AJFDAJF");
        item.setQuantity(3);
        item.setCategory(cat);
        item.setRoom(room);
        String item1Id = item.getItemId();

        Item item2 = new Item();
        item2.setItemName("2nd Item");
        item2.setDescription("Description");
        item2.setUpc("FJKFDAJ8FDJAF");
        item2.setQuantity(6);
        item2.setCategory(cat);
        item2.setRoom(room);
        String item2Id = item2.getItemId();

        this.categoryRepo.save(cat);
        this.roomRepo.save(room);
        this.itemRepo.save(item);
        this.itemRepo.save(item2);

        Item actualItem1 = this.itemRepo.fetchById(item1Id).get();
        Item actualItem2 = this.itemRepo.fetchById(item2Id).get();
        Category actualCategory = this.categoryRepo.fetchById(categoryId).get();
        Room actualRoom = this.roomRepo.fetchById(roomId).get();

        assertEquals(categoryId, actualItem1.getCategory().getCategoryId());
        assertEquals(roomId, actualItem2.getRoom().getRoomId());

        assertFalse(actualCategory.getItems()
            .stream()
            .filter(i -> i.getItemId().equals(item1Id))
            .toList().isEmpty());
        assertFalse(actualRoom.getItems()
            .stream()
            .filter(r -> r.getItemId().equals(item2Id))
            .toList().isEmpty());
    }
}
