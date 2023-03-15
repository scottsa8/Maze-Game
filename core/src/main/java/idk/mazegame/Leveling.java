package idk.mazegame;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Leveling{
    private int level;
    private int experience;
    private int levleTracker;
    private Stage stage;

    

    public void increaseXP(int amount){
        experience += amount;

        if (experience > levleTracker){
            I_Level();
        }

    }

    private void I_Level(){
        level +=1; 
        levleTracker += (experience * 0.5);

    }

    public int getXP(){
        return levleTracker;
    }

    public int getLevel(){
        return level;
    }


}
