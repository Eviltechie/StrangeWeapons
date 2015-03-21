package to.joe.strangeweapons;

public enum Part 
{
    CREEPER("Creepers Killed", 1),
    SKELETON("Skeletons Killed", 1),
    SPIDER("Spiders Killed", 1),
    GIANT("Giants Killed", 1),
    ZOMBIE("Zombies Killed", 1),
    SLIME("Slimes Killed", 1),
    GHAST("Ghasts Killed", 1),
    PIG_ZOMBIE("Zombie Pigmen Killed", 1),
    ENDERMAN("Endermen Killed", 1),
    CAVE_SPIDER("Cave Spiders Killed", 1),
    SILVERFISH("Silverfish Killed", 1),
    BLAZE("Blazes Killed", 1),
    MAGMA_CUBE("Magma Cubes Killed", 1),
    ENDER_DRAGON("Ender Dragons Killed", 1),
    WITHER("Withers Killed", 1),
    BAT("Bats Killed", 1),
    WITCH("Witches Killed", 1),
    PIG("Pigs Killed", 1),
    SHEEP("Sheep Killed", 1),
    COW("Cows Killed", 1),
    CHICKEN("Chickens Killed", 1),
    SQUID("Squids Killed", 1),
    WOLF("Wolves Killed", 1),
    MUSHROOM_COW("Mooshrooms Killed", 1),
    SNOWMAN("Snow Golems Killed", 1),
    OCELOT("Ocelots Killed", 1),
    IRON_GOLEM("Iron Golems Killed", 1),
    VILLAGER("Villagers Killed", 1),
    ENDER_CRYSTAL("Ender Crystals Killed", 1),

    BLOCKS_BROKEN("Blocks Broken", .25),
    MOB_KILLS("Mobs Killed", 1),
    PLAYER_KILLS("Players Killed", 1),
    DAMAGE("Damage Done", .25);

    private String name;
    private double multiplier;

    private Part(String n, double m)
    {
        name = n;
        multiplier = m;
    }

    public String getName()
    {
        return name;
    }

    public double getMultiplier()
    {
        return multiplier;
    }
}
