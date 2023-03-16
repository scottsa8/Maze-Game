package idk.mazegame;

import java.util.ArrayList;

public class Inventory {
    private ArrayList<Item> inv = new ArrayList<Item>();
    private ItemAttributes attributes;

    public Inventory(ItemAttributes itemAttrs) {
        attributes = itemAttrs;
    }

    public void inventoryAdd(Item item, int slot) { //Adds the specified item to the specified space in the inventory
        if (item.name.equals("Fists") || item.name.equals("Empty")) {
            System.out.println("INVENTORY - ERROR: Tried to add a fist or nothing to inventory...");
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
        double score;
        double[] topScore = {0.0, 0.0};
        for (int i = 0; i < inv.size(); i++) {
            if (inv.get(i).type != 4) { //If the item can be equipped
                score = inv.get(i).getDamage() + inv.get(i).getDefence();
                if (score > topScore[0]) {
                    topScore[0] = score;
                    topScore[1] = i;
                }
            }
        }

        if (topScore[0] > 0.1) return inv.get((int)topScore[1]); //return best item
        else return null; //If no item is found, return nothing
    }

    public Item getFirstSingle() {
        double[] topScore = {0.0, 0.0};
        for (int i = 0; i < inv.size(); i++) {
            if (inv.get(i).type == 4) { //If the item can be equipped
                if (inv.get(i).getDurability() > topScore[0]) {
                    topScore[0] = inv.get(i).getDurability();
                    topScore[1] = i;
                }
            }
        }

        if (topScore[0] > 0.1) return inv.get((int)topScore[1]); //return best item
        return null; //If no item is found, return nothing
    }

    public void printInventory() {
        double score;
        double[] topScore = {0.0, 0.0};
        System.out.print("\n");
        for (int i = 0; i < inv.size(); i++) {
            if (i == inv.size()-1 && inv.size() > 1) {
                score = inv.get(i).getDamage() + inv.get(i).getDefence();
                if (score > topScore[0]) {
                    topScore[0] = score;
                    topScore[1] = i;
                }
                System.out.print("& " + inv.get(i).name + "\n");
            } else System.out.print(inv.get(i).name + " ");
        }
    }
}