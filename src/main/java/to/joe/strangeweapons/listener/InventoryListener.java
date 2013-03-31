package to.joe.strangeweapons.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import to.joe.strangeweapons.MetaParser;
import to.joe.strangeweapons.StrangeWeapons;
import to.joe.strangeweapons.meta.Crate;
import to.joe.strangeweapons.meta.StrangePart;
import to.joe.strangeweapons.meta.StrangeWeapon;

public class InventoryListener implements Listener {

    private StrangeWeapons plugin;

    public InventoryListener(StrangeWeapons plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @SuppressWarnings({ "unused", "deprecation" })
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if (event.getInventory().getType() == InventoryType.ANVIL) {
            ItemStack item = event.getCursor();
            if ((event.getSlot() == 0 || event.getSlot() == 1) && event.getSlotType() == SlotType.CRAFTING && (Crate.isCrate(item) || MetaParser.isKey(item) || StrangePart.isPart(item) || MetaParser.isNameTag(item) || MetaParser.isDescriptionTag(item))) {
                event.setCancelled(true);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.updateInventory();
                    }
                }, 1);
                player.sendMessage(ChatColor.RED + "You may not use that on an anvil.");
            }
        }
        if (event.getInventory().getType() == InventoryType.FURNACE) {
            ItemStack item = event.getCursor();
            if (item.getType() != Material.AIR && ((event.getSlot() == 0 && event.getSlotType() == SlotType.CONTAINER) || (event.getSlot() == 1 && event.getSlotType() == SlotType.FUEL)) && (StrangeWeapon.isStrangeWeapon(item) || Crate.isCrate(item) || MetaParser.isKey(item) || StrangePart.isPart(item) || MetaParser.isNameTag(item) || MetaParser.isDescriptionTag(item))) {
                event.setCancelled(true);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.updateInventory();
                    }
                }, 1);
                player.sendMessage(ChatColor.RED + "You may not use that in a furnace.");
            }
        }
        if (event.getInventory().getType() == InventoryType.MERCHANT) {
            ItemStack item = event.getCursor();
            if (item.getType() != Material.AIR && (event.getSlot() == 0 || event.getSlot() == 1) && event.getSlotType() == SlotType.CRAFTING && (StrangeWeapon.isStrangeWeapon(item) || Crate.isCrate(item) || MetaParser.isKey(item) || StrangePart.isPart(item) || MetaParser.isNameTag(item) || MetaParser.isDescriptionTag(item))) {
                event.setCancelled(true);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.updateInventory();
                    }
                }, 1);
                player.sendMessage(ChatColor.RED + "You may not trade that with a villager.");
            }
        }
        if (event.getSlotType() == SlotType.CRAFTING) {
            if (!(event.getInventory() instanceof CraftingInventory)) {
                return;
            }
            final CraftingInventory craftingInventory = (CraftingInventory) event.getInventory();
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    ItemStack strangeWeapon = null;
                    int numStrangeWeapons = 0;
                    ItemStack crate = null;
                    int numCrates = 0;
                    ItemStack key = null;
                    int numKeys = 0;
                    ItemStack strangePart = null;
                    int numStrangeParts = 0;
                    ItemStack nameTag = null;
                    int numNameTags = 0;
                    ItemStack descriptionTag = null;
                    int numDescriptionTags = 0;
                    ItemStack normalItem = null;
                    int numNormalItems = 0;
                    int numTotalItems = 0;
                    for (ItemStack i : craftingInventory.getContents()) {
                        if (i == null || i.getTypeId() == 0) {
                            continue;
                        }
                        if (StrangeWeapon.isStrangeWeapon(i)) {
                            numStrangeWeapons++;
                            strangeWeapon = i;
                        } else if (Crate.isCrate(i)) {
                            numCrates++;
                            crate = i;
                        } else if (MetaParser.isKey(i)) {
                            numKeys++;
                            key = i;
                        } else if (StrangePart.isPart(i)) {
                            numStrangeParts++;
                            strangePart = i;
                        } else if (MetaParser.isNameTag(i)) {
                            numNameTags++;
                            nameTag = i;
                        } else if (MetaParser.isDescriptionTag(i)) {
                            numDescriptionTags++;
                            descriptionTag = i;
                        } else {
                            numNormalItems++;
                            normalItem = i;
                        }
                        numTotalItems++;
                    }
                    if (numCrates == 1 && numKeys == 1 && numTotalItems == 2) {
                        ItemStack fakeItem = new ItemStack(Material.DIRT);
                        ItemMeta meta = fakeItem.getItemMeta();
                        meta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Mystery Item!");
                        fakeItem.setItemMeta(meta);
                        craftingInventory.setResult(fakeItem);
                        player.updateInventory();
                        return;
                    }
                    if (numStrangeWeapons == 1 && numStrangeParts == 1 && numTotalItems == 2) {
                        StrangeWeapon weapon = new StrangeWeapon(strangeWeapon.clone());
                        StrangePart part = new StrangePart(strangePart);
                        if (weapon.getParts().size() > plugin.maxParts) {
                            craftingInventory.setResult(null);
                            player.sendMessage(ChatColor.RED + "You may only have " + plugin.maxParts + " strange parts on a weapon");
                        } else if (weapon.getParts().containsKey(part.getPart())) {
                            craftingInventory.setResult(null);
                            player.sendMessage(ChatColor.RED + "This weapon is already tracking " + part.getPart().getName());
                        } else {
                            weapon.getParts().put(part.getPart(), 0);
                            craftingInventory.setResult(weapon.previewItemStack());
                        }
                        player.updateInventory();
                        return;
                    }
                    if (numStrangeWeapons == 1 && numNameTags == 1 && numTotalItems == 2) {
                        if (!plugin.tags.containsKey(player.getName())) {
                            player.sendMessage(ChatColor.RED + "Set a name with /tag before using a name tag");
                            return;
                        }
                        StrangeWeapon weapon = new StrangeWeapon(strangeWeapon.clone());
                        weapon.setCustomName(plugin.tags.get(player.getName()));
                        craftingInventory.setResult(weapon.previewItemStack());
                        player.updateInventory();
                        return;
                    }
                    if (numStrangeWeapons == 1 && numDescriptionTags == 1 && numTotalItems == 2) {
                        if (!plugin.tags.containsKey(player.getName())) {
                            player.sendMessage(ChatColor.RED + "Set a description with /tag before using a description tag");
                            return;
                        }
                        StrangeWeapon weapon = new StrangeWeapon(strangeWeapon.clone());
                        weapon.setDescription(plugin.tags.get(player.getName()));
                        craftingInventory.setResult(weapon.previewItemStack());
                        player.updateInventory();
                        return;
                    }
                    if (numNormalItems != numTotalItems) {
                        craftingInventory.setResult(null);
                        player.updateInventory();
                        return;
                    }
                }
            }, 1);
        } else if (event.getSlotType() == SlotType.RESULT && event.getInventory() instanceof CraftingInventory) {
            CraftingInventory craftingInventory = (CraftingInventory) event.getInventory();
            ItemStack[] matrix = craftingInventory.getMatrix();
            ItemStack strangeWeapon = null;
            int numStrangeWeapons = 0;
            ItemStack crate = null;
            int numCrates = 0;
            ItemStack key = null;
            int numKeys = 0;
            ItemStack strangePart = null;
            int numStrangeParts = 0;
            ItemStack nameTag = null;
            int numNameTags = 0;
            ItemStack descriptionTag = null;
            int numDescriptionTags = 0;
            ItemStack normalItem = null;
            int numNormalItems = 0;
            int numTotalItems = 0;
            for (ItemStack i : matrix) {
                if (i == null || i.getTypeId() == 0) {
                    continue;
                }
                if (StrangeWeapon.isStrangeWeapon(i)) {
                    numStrangeWeapons++;
                    strangeWeapon = i;
                } else if (Crate.isCrate(i)) {
                    numCrates++;
                    crate = i;
                } else if (MetaParser.isKey(i)) {
                    numKeys++;
                    key = i;
                } else if (StrangePart.isPart(i)) {
                    numStrangeParts++;
                    strangePart = i;
                } else if (MetaParser.isNameTag(i)) {
                    numNameTags++;
                    nameTag = i;
                } else if (MetaParser.isDescriptionTag(i)) {
                    numDescriptionTags++;
                    descriptionTag = i;
                } else {
                    numNormalItems++;
                    normalItem = i;
                }
                numTotalItems++;
            }
            if (numCrates == 1 && numKeys == 1 && numTotalItems == 2) {
                ItemStack loot = new Crate(crate).getUncratedItem();
                if (StrangeWeapon.isStrangeWeapon(loot)) {
                    loot = new StrangeWeapon(loot).clone();
                }
                /*if (loot == null) {
                    getLogger().severe("LOOT IS NULL - Report this to the plugin author!");
                    getLogger().severe("Player " + player.getName() + " tried to uncrate a crate!" + crate.serialize().toString());
                }*///http://pastie.org/private/borniaknvtofbio6mfza
                String lootName;
                if (loot.getItemMeta().hasDisplayName()) {
                    lootName = loot.getItemMeta().getDisplayName();
                } else {
                    lootName = ChatColor.YELLOW + StrangeWeapons.toTitleCase(loot.getType().toString().toLowerCase().replaceAll("_", " "));
                }
                plugin.getServer().broadcastMessage(player.getDisplayName() + ChatColor.WHITE + " has unboxed: " + ChatColor.YELLOW + lootName);
                //event.setResult(Result.ALLOW); //Maybe this fixes it?
                event.setCurrentItem(loot);
            }
            if (numStrangeWeapons == 1 && numStrangeParts == 1 && numTotalItems == 2) {
                StrangeWeapon weapon = new StrangeWeapon(strangeWeapon);
                StrangePart part = new StrangePart(strangePart);
                weapon.getParts().put(part.getPart(), 0);
                event.setCurrentItem(weapon.getItemStack());
            }
            if (numStrangeWeapons == 1 && numNameTags == 1 && numTotalItems == 2) {
                if (!plugin.tags.containsKey(player.getName())) {
                    player.sendMessage(ChatColor.RED + "Set a name with /tag before using a name tag");
                    return;
                }
                StrangeWeapon weapon = new StrangeWeapon(strangeWeapon);
                weapon.setCustomName(plugin.tags.get(player.getName()));
                event.setCurrentItem(weapon.getItemStack());
            }
            if (numStrangeWeapons == 1 && numDescriptionTags == 1 && numTotalItems == 2) {
                if (!plugin.tags.containsKey(player.getName())) {
                    player.sendMessage(ChatColor.RED + "Set a description with /tag before using a description tag");
                    return;
                }
                StrangeWeapon weapon = new StrangeWeapon(strangeWeapon);
                weapon.setDescription(plugin.tags.get(player.getName()));
                event.setCurrentItem(weapon.getItemStack());
            }
        }
    }
}