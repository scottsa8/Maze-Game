package idk.mazegame;
public class Leveling{
    private int level;
    private int experience;
    private int levleTracker;
    
    public void increaseXP(int amount){
        if(level <=10)
        {
            amount = amount/4;
        }
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
