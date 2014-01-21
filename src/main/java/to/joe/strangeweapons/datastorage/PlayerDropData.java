package to.joe.strangeweapons.datastorage;

import java.util.Random;

import to.joe.strangeweapons.StrangeWeapons;

public class PlayerDropData implements Cloneable {

    private static Random random = new Random();

    /*
     * Store their name, their playtime, when their next item drop is, and when their next crate drop is.
     * When a player joins the server, this object is loaded or created for them.
     * This will calculate when the players next drop will be.
     * When they reach that time, if they are eligible for a drop they will receive it.
     */

    private boolean isUpdated = true;
    private String player;
    private int playTime;
    private int nextItemDrop;
    private int nextCrateDrop;

    public PlayerDropData(String player, int playTime, int nextItemDrop, int nextCrateDrop) {
        this.player = player;
        this.playTime = playTime;
        this.nextItemDrop = nextItemDrop;
        this.nextCrateDrop = nextCrateDrop;
        if (nextItemDrop == 0) {
            rollItem();
        }
        if (nextCrateDrop == 0) {
            rollCrate();
        }
    }

    public String getPlayer() {
        return player;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        if (this.playTime != playTime) {
            isUpdated = false;
            this.playTime = playTime;
        }
    }

    public int getNextItemDrop() {
        return nextItemDrop;
    }

    public void setNextItemDrop(int nextItemDrop) {
        if (this.nextItemDrop != nextItemDrop) {
            isUpdated = false;
            this.nextItemDrop = nextItemDrop;
        }
    }

    public int getNextCrateDrop() {
        return nextCrateDrop;
    }

    public void setNextCrateDrop(int nextCrateDrop) {
        if (this.nextCrateDrop != nextCrateDrop) {
            isUpdated = false;
            this.nextCrateDrop = nextCrateDrop;
        }
    }

    public int rollItem() {
        nextItemDrop = (random.nextInt(StrangeWeapons.plugin.config.itemDropRollMaxTime - StrangeWeapons.plugin.config.itemDropRollMinTime) + StrangeWeapons.plugin.config.itemDropRollMinTime) * 60 + playTime;
        isUpdated = false;
        return nextItemDrop;
    }

    public int rollCrate() {
        nextCrateDrop = (random.nextInt(StrangeWeapons.plugin.config.crateDropRollMaxTime - StrangeWeapons.plugin.config.crateDropRollMinTime) + StrangeWeapons.plugin.config.crateDropRollMinTime) * 60 + playTime;
        isUpdated = false;
        return nextCrateDrop;
    }

    @Override
    public PlayerDropData clone() {
        return new PlayerDropData(player, playTime, nextItemDrop, nextCrateDrop);
    }

}
