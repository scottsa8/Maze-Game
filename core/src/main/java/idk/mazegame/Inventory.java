package idk.mazegame;

import java.util.ArrayList;

public class Inventory {
    private ArrayList<Item> inv = new ArrayList<Item>();
    private ItemAttributes attributes;

    public Inventory(ItemAttributes itemAttrs) {
        attributes = itemAttrs;
    }

    public void inventoryAdd(Item item, int slot) { //Adds the specified item to the specified space in the inventory
        if (item.name.equals("Fists")) {
            System.out.println("INVENTORY - ERROR: Tried to add a fist to inventory...");
        } else {
            //1. If given slot is 0, add item onto end of array list
            if (slot == 0) inv.add(item);
                //2. Else, add item to the given slot in inventory
            else inv.add(slot, item);
        }
    }

    public void inventoryRemove(Item item) { //Removes the specified item from the inventory
        //Remove item from inventory
        inv.remove(item);
    }

    public Item getFirstItem() {
        for (int i = 0; i < inv.size(); i++) {
            if (inv.get(i).type != 4) { //If the item can be equipped
                return inv.get(i);
            }
        }

        return null; //If no item is found, return nothing
    }

    public void printInventory() {
        System.out.print("\n");
        for (int i = 0; i < inv.size(); i++) {
            if (i == inv.size()-1 && inv.size() > 1) {
                System.out.print("& " + inv.get(i).name + "\n");
            } else System.out.print(inv.get(i).name + " ");
        }
    }
}