package idk.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;

public class ItemAttributes {
    private ArrayList<String[]> meleeItems;
    private ArrayList<String[]> shieldItems;
    private ArrayList<String[]> singleItems;
    private ArrayList<String[]> rangedItems;
    private ArrayList<String[]> magicItems;

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
            if (items[i][0] == "0") meleeItems.add(items[i]);
            if (items[i][0] == "1") rangedItems.add(items[i]);
            if (items[i][0] == "2") magicItems.add(items[i]);
            if (items[i][0] == "3") shieldItems.add(items[i]);
            if (items[i][0] == "4") singleItems.add(items[i]);
        }
    }

    //public  String[] getAttributes(int[] index) {

    //}
}
