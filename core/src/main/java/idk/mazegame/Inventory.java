package idk.mazegame;

import java.util.ArrayList;

public class Inventory {
    private ArrayList<Item> inv = new ArrayList<Item>();

    public Inventory() {

    }

    public void inventoryAdd(Item item, int slot) { //Adds the specified item to the specified space in the inventory
        //1. If given slot is 0, add item onto end of array list
        //2. Else, add item to the given slot in inventory
    }

    public void inventoryRemove(Item item, int slot) { //Removes the specified item from the specified space in the inventory
        //Remove item from given slot in inventory
    }
}