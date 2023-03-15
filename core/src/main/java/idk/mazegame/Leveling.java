package idk.mazegame;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Leveling{
    private int level;
    private int experience;
    private int levleTracker;

    

    public void increaseXP(int amount){
        experience += amount;

        if (experience > levleTracker){
            I_Level();
        }

    }
    private void I_Level(){
        level +=1; 
        levleTracker += 10;

    }
    public int getXP(){
        return experience;
    }
    public int getLevel()
    {
        return level;
    }

    
    

}
