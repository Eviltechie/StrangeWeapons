package to.joe.strangeweapons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import to.joe.strangeweapons.command.NewCrateCommand;
import to.joe.strangeweapons.command.NewDescriptionTagCommand;
import to.joe.strangeweapons.command.NewKeyCommand;
import to.joe.strangeweapons.command.NewNameTagCommand;
import to.joe.strangeweapons.command.NewPartCommand;
import to.joe.strangeweapons.command.StrangeCommand;

public class StrangeWeapons extends JavaPlugin implements Listener {

    private HashMap<Integer, String> weaponText = new HashMap<Integer, String>();
    public HashMap<String, String> tags = new HashMap<String, String>();

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        getCommand("strange").setExecutor(new StrangeCommand(this));
        getCommand("newcrate").setExecutor(new NewCrateCommand());
        getCommand("newkey").setExecutor(new NewKeyCommand());
        getCommand("newpart").setExecutor(new NewPartCommand());
        getCommand("newnametag").setExecutor(new NewNameTagCommand());
        getCommand("newdescriptiontag").setExecutor(new NewDescriptionTagCommand());
        getServer().getPluginManager().registerEvents(this, this);

        for (String level : getConfig().getConfigurationSection("levels").getKeys(false)) {
            weaponText.put(Integer.parseInt(level), getConfig().getString("levels." + level));
        }
    }

    public static String toTitleCase(String string) {
        StringBuilder titleString = new StringBuilder();
        for (String s : string.split(" ")) {
            titleString.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        }
        return titleString.substring(0, titleString.length() - 1);
    }

    public String getWeaponName(int kills) {
        while (!weaponText.containsKey(kills)) {
            kills--;
            if (kills < 0)
                return "Sub-par";
        }
        return weaponText.get(kills);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player p = event.getEntity().getKiller();
            if (p.getItemInHand().getAmount() > 0) {
                NameableItem item = new NameableItem((CraftItemStack) p.getItemInHand());
                if (item.isStrange()) {
                    int kills = item.getKills();
                    kills++;
                    item.setKills(kills);
                    String oldName = item.getName();
                    item.setName(ChatColor.GOLD + getWeaponName(kills) + " " + toTitleCase(p.getItemInHand().getType().toString().toLowerCase().replaceAll("_", " ")));
                    item.setLore(new String[] { ChatColor.WHITE + "Kills: " + kills });
                    if (!oldName.equals(item.getName())) {
                        getServer().broadcastMessage(ChatColor.GOLD + p.getName() + "'s " + oldName + " has reached a new rank: " + getWeaponName(kills));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() == SlotType.CRAFTING) {
            final CraftingInventory cInv = (CraftingInventory) event.getInventory();
            final Player p = (Player) event.getWhoClicked();
            getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @SuppressWarnings("deprecation")
                @Override
                public void run() {
                    ItemStack[] contents = cInv.getContents();
                    ItemStack normalItem = null;
                    ItemStack crate = null;
                    ItemStack strange = null;
                    ItemStack part = null;
                    int stranges = 0;
                    int crates = 0;
                    int keys = 0;
                    int parts = 0;
                    int items = 0;
                    int normalItems = 0;
                    int names = 0;
                    int descriptions = 0;
                    for (ItemStack i : contents) {
                        if (i.getTypeId() == 0)
                            continue;
                        items++;
                        NameableItem n = new NameableItem((CraftItemStack) i);
                        if (n.isStrange()) {
                            stranges++;
                            strange = i;
                        } else if (n.isCrate()) {
                            crates++;
                            crate = i;
                        } else if (n.isKey()) {
                            keys++;
                        } else if (n.isPart()) {
                            parts++;
                            part = i;
                        } else if (n.isDescriptionTag()) {
                            descriptions++;
                        } else if (n.isNameTag()) {
                            names++;
                        } else {
                            normalItem = i;
                            normalItems++;
                        }
                    }
                    if (crates == 1 && keys == 1 && items == 2) {
                        /*NameableItem n = new NameableItem((CraftItemStack) crate);
                        List<ItemStack> crateContents = n.getCrateContents();
                        Collections.shuffle(crateContents);
                        ItemStack result = crateContents.get(0);
                        cInv.setResult(result);*/
                        CraftItemStack c = new CraftItemStack(Material.DIRT);
                        NameableItem n = new NameableItem(c);
                        n.makeStrange();
                        n.setName(ChatColor.DARK_PURPLE + "Mystery Item!");
                        cInv.setResult(c);
                        p.updateInventory();
                    }
                    if (stranges == 1 && parts == 1 && items == 2) {
                        CraftItemStack combinedItem = new CraftItemStack(strange.getType()); //TODO Damage maybe?
                        NameableItem combinedNameableItem = new NameableItem(combinedItem);
                        NameableItem partNameableItem = new NameableItem((CraftItemStack) part);
                        NameableItem strageNameableItem = new NameableItem((CraftItemStack) strange);
                        List<String> lore = strageNameableItem.getLore();
                        lore.add(ChatColor.WHITE + partNameableItem.getPart().getName() + ": 0");
                        combinedNameableItem.makeStrange();
                        combinedNameableItem.setName(strageNameableItem.getName());
                        combinedNameableItem.setLore(lore);
                        for (Entry<Part, Integer> e : strageNameableItem.getParts().entrySet()) {
                            combinedNameableItem.setPart(e.getKey(), e.getValue());
                        }
                        combinedNameableItem.setPart(partNameableItem.getPart(), 0);
                        combinedNameableItem.setKills(strageNameableItem.getKills());
                        cInv.setResult(combinedItem);
                        p.updateInventory();
                    }
                    if (normalItems == 1 && names == 1 && items == 2) {
                        if (!tags.containsKey(p.getName())) {
                            p.sendMessage(ChatColor.RED + "Set a name with /tag before trying to use a name tag");
                            return;
                        }
                        CraftItemStack combinedItem = new CraftItemStack(strange.getType()); //TODO Damage maybe?
                        NameableItem combinedNameableItem = new NameableItem(combinedItem);
                        NameableItem normalNameableItem = new NameableItem((CraftItemStack) normalItem);
                    }
                }
            }, 1);
        } else if (event.getSlotType() == SlotType.RESULT) {
            CraftingInventory cInv = (CraftingInventory) event.getInventory();
            ItemStack[] ingredients = cInv.getMatrix();
            ItemStack crate = null;
            int crates = 0;
            int keys = 0;
            int items = 0;
            for (ItemStack i : ingredients) {
                if (i == null || i.getTypeId() == 0)
                    continue;
                items++;
                NameableItem n = new NameableItem((CraftItemStack) i);
                if (n.isCrate()) {
                    crates++;
                    crate = i;
                }
                if (n.isKey())
                    keys++;
            }
            if (crates == 1 && keys == 1 && items == 2) {
                NameableItem n = new NameableItem((CraftItemStack) crate);
                List<ItemStack> crateContents = n.getCrateContents();
                Collections.shuffle(crateContents);
                ItemStack result = crateContents.get(0);
                event.setCurrentItem(result);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player p = event.getEntity().getKiller();
        if (p != null) {
            ItemStack weapon = p.getItemInHand();
            NameableItem n = new NameableItem((CraftItemStack) weapon);
            HashMap<Part, Integer> parts = n.getParts();
            Part thisKill = Part.valueOf(event.getEntityType().name());
            if (parts.containsKey(thisKill)) {
                n.setPart(thisKill, n.getPartValue(thisKill) + 1);
                ArrayList<String> lore = new ArrayList<String>();
                lore.add(ChatColor.WHITE + "Kills: " + n.getKills());
                for (Part pa : n.getParts().keySet()) {
                    lore.add(ChatColor.WHITE + pa.getName() + ": " + n.getPartValue(pa));
                }
                n.setLore(lore);
            }
        }
    }
}