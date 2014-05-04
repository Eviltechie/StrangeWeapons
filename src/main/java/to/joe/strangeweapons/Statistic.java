package to.joe.strangeweapons;

public enum Statistic {
    BLOCKS_BROKEN("Blocks Broken", .25),
    MOB_KILLS("Mobs Killed", 1),
    PLAYER_KILLS("Players Killed", 1),
    DAMAGE("Damage Done", .25);

    private String description;
    private double multiplier;

    private Statistic(String d, double m) {
        description = d;
        multiplier = m;
    }

    public String getDescription() {
        return description;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
