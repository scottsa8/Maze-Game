package idk.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemAttributes {
    private ArrayList<String[]> meleeItems = new ArrayList<String[]>();
    private ArrayList<String[]> shieldItems = new ArrayList<String[]>();;
    private ArrayList<String[]> singleItems = new ArrayList<String[]>();;
    private ArrayList<String[]> rangedItems = new ArrayList<String[]>();;
    private ArrayList<String[]> magicItems = new ArrayList<String[]>();;

    public ItemAttributes() {
        generateItemAttributes();
    }

    private void generateItemAttributes() { //finds item attributes
        FileHandle handle = Gdx.files.internal("items/attributes.csv");
        String text = handle.readString();
        String[] itemAttributes = text.split("\n");
        String[][] items = new String[itemAttributes.length][9];

        for (int i = 0; i < itemAttributes.length; i++) {
            items[i] = itemAttributes[i].split(",");
        }

        for (int i = 0; i < items.length; i++) {
            if (items[i][0].equals("0")) meleeItems.add(items[i]);
            if (items[i][0].equals("1")) rangedItems.add(items[i]);
            if (items[i][0].equals("2")) magicItems.add(items[i]);
            if (items[i][0].equals("3")) shieldItems.add(items[i]);
            if (items[i][0].equals("4")) singleItems.add(items[i]);
        }
    }

    public  String[] getAttributes(int type, int typeIndex) {
        String[] index = {String.valueOf(type), String.valueOf(typeIndex)};
        String[] itemToReturn = meleeItems.get(0);

        if (index[0].equals("0")) {
            for (int i = 0; i < meleeItems.size(); i++) {
                if (index[1].equals(meleeItems.get(i)[1])) itemToReturn = meleeItems.get(i);
            }
        } else if (index[0].equals("1")) {
            for (int i = 0; i < rangedItems.size(); i++) {
                if (index[1].equals(rangedItems.get(i)[1])) itemToReturn = rangedItems.get(i);
            }
        } else if (index[0].equals("2")) {
            for (int i = 0; i < magicItems.size(); i++) {
                if (index[1].equals(magicItems.get(i)[1])) itemToReturn = magicItems.get(i);
            }
        } else if (index[0].equals("3")) {
            for (int i = 0; i < shieldItems.size(); i++) {
                if (index[1].equals(shieldItems.get(i)[1])) itemToReturn = shieldItems.get(i);
            }
        } else if (index[0].equals("4")) {
            for (int i = 0; i < singleItems.size(); i++) {
                if (index[1].equals(singleItems.get(i)[1])) itemToReturn = singleItems.get(i);
            }
        } else {
            System.out.println("ITEM ATTRIBUTES - Error: type out of index");
        }

        return itemToReturn;
    }
}
