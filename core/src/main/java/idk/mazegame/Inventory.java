package idk.mazegame;

import java.util.ArrayList;

public class Inventory {
    private ArrayList<Item> inv = new ArrayList<Item>();

    public Inventory() {

    }

    public void inventoryAdd(Item item, int slot) { //Adds the specified item to the specified space in the inventory
        if (slot == 0) inv.add(item);
        else inv.add(slot, item);
    }

    public void inventoryRemove(Item item) { //Removes the specified item from the inventory
        inv.remove(item);
    }
}