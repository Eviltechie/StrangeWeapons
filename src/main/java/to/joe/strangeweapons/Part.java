package to.joe.strangeweapons;

public enum Part {
    CREEPER("Creepers Killed"),
    SKELETON("Skeletons Killed"),
    SPIDER("Spiders Killed"),
    GIANT("Giants Killed"),
    ZOMBIE("Zombies Killed"),
    SLIME("Slimes Killed"),
    GHAST("Ghasts Killed"),
    PIG_ZOMBIE("Zombie Pigmen Killed"),
    ENDERMAN("Endermen Killed"),
    CAVE_SPIDER("Cave Spiders Killed"),
    SILVERFISH("Silverfish Killed"),
    BLAZE("Blazes Killed"),
    MAGMA_CUBE("Magma Cubes Killed"),
    ENDER_DRAGON("Ender Dragons Killed"),
    WITHER("Withers Killed"),
    BAT("Bats Killed"),
    WITCH("Witches Killed"),
    PIG("Pigs Killed"),
    SHEEP("Sheep Killed"),
    COW("Cows Killed"),
    CHICKEN("Chickens Killed"),
    SQUID("Squids Killed"),
    WOLF("Wolves Killed"),
    MUSHROOM_COW("Mooshrooms Killed"),
    SNOWMAN("Snow Golems Killed"),
    OCELOT("Ocelots Killed"),
    IRON_GOLEM("Iron Golems Killed"),
    VILLAGER("Villagers Killed"),
    ENDER_CRYSTAL("Ender Crystals Killed"),
    
    BLOCKS_BROKEN("Blocks Broken"),
    MOB_KILLS("Mobs Killed"),
    PLAYER_KILLS("Players Killed"),
    DAMAGE("Damage Done");
    
    private String name;
    
    private Part(String n) {
        name = n;
    }
    
    public String getName() {
        return name;
    }
}
