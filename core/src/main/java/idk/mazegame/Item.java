package idk.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Random;

public class Item {
    public String name;
    public int type; //Melee, ranged, magic, shield, single-use
    private ItemAttributes attributes;

    private String description;
    private int typeIndex; //Used to find the specific item. Example: Type is melee, typeIndex is 0 so item is just fists
    private int rarity;
    private double damage;
    private double defence;
    private double range;
    private double durability;
    private double maxDurability;
    private double weight; //Used for affecting the durability of the item
    private int pathIndex;
    private String path;

    public Item(ItemAttributes itemAttrs) { //Create generic item
        attributes = itemAttrs;
        generateItem(0, 0, 0);
    }

    public Item(ItemAttributes itemAttrs, int t, int i) { //Create an item using the given type and index
        attributes = itemAttrs;
        generateItem(t, i, 0);
    }

    public Item(ItemAttributes itemAttrs, int level) { //Create generic item
        attributes = itemAttrs;
        int[] types = attributes.getLengths();

        int check = 0;
        int randomTypeIndex = 0, randomTypes = 0;
        while (check == 0) {
            check = 1;
            randomTypes = new Random().nextInt(types.length);
            randomTypeIndex = new Random().nextInt(types[randomTypes]);
            if (randomTypeIndex == 0 && randomTypes == 0) check = 0;
            if (randomTypeIndex == 0 && randomTypes == 4) check = 0;
        }

        generateItem(randomTypes, randomTypeIndex, level);
    }

    private void generateItem(int thisType, int thisTypeIndex, int level) { //used for creating an item
        //1. Find the item using the type and type index
        String[] itemAttrs = attributes.getAttributes(thisType, thisTypeIndex);

        //System.out.println(itemAttrs);
        //2. Associate this item with all the attributes of the found item
        type = Integer.valueOf(itemAttrs[0]);
        typeIndex = Integer.valueOf(itemAttrs[1]);
        name = itemAttrs[2];
        description = itemAttrs[3];
        damage = Double.valueOf(itemAttrs[4])*10;
        defence = Double.valueOf(itemAttrs[5])*10;
        range = Double.valueOf(itemAttrs[6]);
        durability = Double.valueOf(itemAttrs[7])*2;
        maxDurability = Double.valueOf(itemAttrs[7])*2;
        weight = Double.valueOf(itemAttrs[8]);
        path = itemAttrs[9];
      
        if (name.equals("Fist") || type == 4) {
            System.out.println("ITEM - Error: This cannot have rarity. Ignore this.");
        } else {
            //3. Create the items rarity
            int levelNum = 46;
            if (level >= 50) levelNum = 101; //Checking player level to see what rarities that can obtain
            else if (level >= 40) levelNum = 96;
            else if (level >= 30) levelNum = 86;
            else if (level >= 20) levelNum = 71;
            int randomNum = new Random().nextInt(levelNum);

            if (randomNum <= 45) rarity = 1;
            else if (randomNum <= 70) rarity = 2;
            else if (randomNum <= 85) rarity = 3;
            else if (randomNum <= 95) rarity = 4;
            else if (randomNum <= 99) rarity = 5;
            else if (randomNum <= 100) rarity = 6;
           
           // 4. Modify attributes according to rarity
            attributeUpdate();
        }
    }

    private void attributeUpdate() {
        if (rarity == 1) {
            name = "Common: " + name;
        } else if (rarity == 2) {
            name = "Uncommon: " + name;
            damage *= 1.25;
            defence *= 1.25;
        } else if (rarity == 3) {
            name = "Rare: " + name;
            damage *= 1.5;
            defence *= 1.5;
        } else if (rarity == 4) {
            name = "Epic: " + name;
            damage *= 2;
            defence *= 2;
        } else if (rarity == 5) {
            name = "Ultra: " + name;
            damage *= 3;
            defence *= 3;
        } else if (rarity == 6) {
            name = "Legendary: " + name;
            damage *= 5;
            defence *= 5;
        }
    }

    public double getDamage() { return damage; }

    public double getDefence() { return defence; }

    public double getDurability() {
        int percent = (int)(durability / maxDurability * 100);
        return percent;
    }
    public String getPath()
    {
        return path;
    }
 
    public void useItem() { //Performs the action of the item and modifies it accordingly
        //1. Find what action is performed with this item
        //2. Perform that action
        //3. Modify item accordingly
        durability -= weight;
        //4. Check if durability is below or equal to 0
        if (durability <= 0) {
            //4. a. If so, destroy item and remove it from its slot
            destroyItem();
        }
    }

    private void destroyItem() { //Destroys the item object
        //Simply destroy this item
        type = -1;
        typeIndex = 0;
        name = "Destroyed item.";
        description = "This item has been destroyed.";
        System.out.println("destroyed");
    }
    public Color getItemColor(Item i)
    {
        if(i.rarity ==1)
        {
            return Color.GRAY;
        }
        else if(i.rarity ==2)
        {
            return Color.GREEN;
        }
        else if(i.rarity ==3)
        {
            return Color.BLUE;
        }
        else if(i.rarity ==4)
        {
            return Color.PURPLE;
        }
        else if(i.rarity ==5)
        {
            return Color.RED;
        }
        else if(i.rarity ==6)
        {
            return Color.ORANGE;
        }
        else if(i.type ==4 && !(i.name.contains("God")))
        {
            return Color.RED;
        }
        else if(i.type ==4 && (i.name.contains("God")))
        {
            return Color.GOLD;
        }
        else
        {
            return Color.WHITE;
        }
        
    }
}