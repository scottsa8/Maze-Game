package idk.mazegame;

public class Item {
    private String name;
    private String description;
    private int type; //Melee, ranged, etc
    private int typeIndex; //Used to find the specific item. Example: Type is melee, typeIndex is 0 so item is just fists
    private int rarity;
    private double damage;
    private double defence;
    private double range;
    private double durability;
    private double weight; //Used for affecting the durability of the item

    public Item() { //Create generic item (Could just be fists)

    }

    public void generateItem() { //Generates an item object, including the rarity and item type

    }

    public void useItem() { //Performs the action of the item and modifies it accordingly

    }

    private void destroyItem() { //Destroys the item object

    }

}
