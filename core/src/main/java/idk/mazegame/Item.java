package idk.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import com.badlogic.gdx.Gdx;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Item {
    public int type; //Melee, ranged, magic, shield, single-use
    private ItemAttributes attributes;
    private String name;
    private String description;
    private int typeIndex; //Used to find the specific item. Example: Type is melee, typeIndex is 0 so item is just fists
    private int rarity;
    private double damage;
    private double defence;
    private double range;
    private double durability;
    private double weight; //Used for affecting the durability of the item

    public Item(ItemAttributes itemAttrs) { //Create generic item
    }

    public Item(ItemAttributes itemAttrs, int t, int i) { //Create an item using the given type and index
    }

    //public Item generateItem(int thisType, int thisTypeIndex) { //used for creating an item
        //1. Find the item using the type and type index
        //2. Associate this item with all the attributes of the found item (create a csv file to hold these initial attributes)
        //3. Create the items rarity
        //4. Modify attributes according to rarity
        //5. Return item
    //}

    public void useItem() { //Performs the action of the item and modifies it accordingly
        //1. Find what action is performed with this item
        //2. Perform that action
        //3. Modify item accordingly
        //4. Check if durability is below or equal to 0
        //4. a. If so, destroy item and remove it from its slot
    }

    private void destroyItem() { //Destroys the item object
        //Simply destroy this item
    }
}
