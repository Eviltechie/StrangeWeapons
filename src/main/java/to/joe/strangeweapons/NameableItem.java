package to.joe.strangeweapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagInt;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

public class NameableItem {

    private ItemStack s;

    public NameableItem(CraftItemStack itemStack) {
        s = itemStack.getHandle();
    }
    
    public NBTTagCompound getTag() {
        return s.tag;
    }
    
    public void setTag(NBTTagCompound t) {
        s.tag = t;
    }
    
    public boolean isDescriptionTag() {
        if (s.tag == null)
            return false;
        return s.tag.hasKey("DescriptionTag");
    }
    
    public void makeDescriptionTag() {
        if (s.tag == null)
            s.tag = new NBTTagCompound();
        s.tag.setBoolean("DescriptionTag", true);
        if (!s.tag.hasKey("display"))
            s.tag.setCompound("display", new NBTTagCompound());
    }
    
    public boolean isNameTag() {
        if (s.tag == null)
            return false;
        return s.tag.hasKey("NameTag");
    }
    
    public void makeNameTag() {
        if (s.tag == null)
            s.tag = new NBTTagCompound();
        s.tag.setBoolean("NameTag", true);
        if (!s.tag.hasKey("display"))
            s.tag.setCompound("display", new NBTTagCompound());
    }
    
    public boolean isCrate() {
        if (s.tag == null)
            return false;
        return s.tag.hasKey("ItemCrate");
    }
    
    public void makeCrate() {
        if (s.tag == null)
            s.tag = new NBTTagCompound();
        s.tag.setBoolean("ItemCrate", true);
        if (!s.tag.hasKey("display"))
            s.tag.setCompound("display", new NBTTagCompound());
    }
    
    public void setCrateContents(List<org.bukkit.inventory.ItemStack> items) {
        NBTTagList serializedItems = new NBTTagList();
        for (org.bukkit.inventory.ItemStack i : items) {
            YamlConfiguration config = new YamlConfiguration();
            config.set("item", i);
            NBTTagString string = new NBTTagString(config.saveToString());
            string.data = config.saveToString();
            serializedItems.add(string);
        }
        s.tag.set("CrateContents", serializedItems);
    }
    
    public List<org.bukkit.inventory.ItemStack> getCrateContents() {
        ArrayList<org.bukkit.inventory.ItemStack> contents = new ArrayList<org.bukkit.inventory.ItemStack>();
        NBTTagList serializedItems = s.tag.getList("CrateContents");
        for (int x = 0; x < serializedItems.size(); x++) {
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.loadFromString(((NBTTagString) serializedItems.get(x)).data);
                contents.add(config.getItemStack("item"));
            } catch (InvalidConfigurationException e) {
                return null;
            }
        }
        return contents;
    }
    
    public boolean isKey() {
        if (s.tag == null)
            return false;
        return s.tag.hasKey("CrateKey");
    }
    
    public void makeKey() {
        if (s.tag == null)
            s.tag = new NBTTagCompound();
        s.tag.setBoolean("CrateKey", true);
        if (!s.tag.hasKey("display"))
            s.tag.setCompound("display", new NBTTagCompound());
    }
    
    public boolean isPart() {
        if (s.tag == null)
            return false;
        return s.tag.hasKey("StrangePart");
    }
    
    public void makePart(Part p) {
        if (s.tag == null)
            s.tag = new NBTTagCompound();
        s.tag.setBoolean("StrangePart", true);
        s.tag.setString("PartStat", p.toString());
        s.tag.setString("PartName", p.getName());
        if (!s.tag.hasKey("display")) {
            s.tag.setCompound("display", new NBTTagCompound());
        }
    }
    
    public Part getPart() {
        return Part.valueOf(s.tag.getString("PartStat"));
    }
    
    public boolean isStrange() {
        if (s.tag == null)
            return false;
        return s.tag.hasKey("Strange");
    }

    public void makeStrange() {
        if (s.tag == null) {
            s.tag = new NBTTagCompound();
        }
        s.tag.setBoolean("Strange", true);
        if (!s.tag.hasKey("display")) {
            s.tag.setCompound("display", new NBTTagCompound());
        }
    }
    
    public boolean hasParts() {
        return s.tag.hasKey("StrangeParts");
    }
    
    public HashMap<Part, Integer> getParts() {
        NBTTagCompound rawParts = s.tag.getCompound("StrangeParts");
        HashMap<Part, Integer> parts = new HashMap<Part, Integer>();
        for (Object key : rawParts.c()) {
            String part = ((NBTTagInt) key).getName();
            parts.put(Part.valueOf(part), rawParts.getInt(part));
        }
        return parts;
    }
    
    public int getPartValue(Part part) {
        return s.tag.getCompound("StrangeParts").getInt(part.toString());
    }
    
    public void setPart(Part part, int n) {
        if (!s.tag.hasKey("StrangeParts")) {
            s.tag.setCompound("StrangeParts", new NBTTagCompound());
        }
        s.tag.getCompound("StrangeParts").setInt(part.toString(), n);
    }

    public boolean hasKills() {
        return s.tag.hasKey("PlayerKills");
    }

    public int getKills() {
        return s.tag.getInt("PlayerKills");
    }

    public void setKills(int kills) {
        s.tag.setInt("PlayerKills", kills);
    }
    
    public boolean hasName() {
        return s.tag.getCompound("display").hasKey("Name");
    }
    
    public void setName(String name) {
        s.tag.getCompound("display").setString("Name", name);
    }

    public String getName() {
        return s.tag.getCompound("display").getString("Name");
    }
    
    public boolean hasCustomName() {
        return s.tag.hasKey("CustomName");
    }
    
    public void setCustomName(String name) {
        s.tag.setString("CustomName", name);
    }

    public String getCustomName() {
        return s.tag.getString("CustomName");
    }
    
    public boolean hasDescription() {
        return s.tag.hasKey("Description");
    }
    
    public void setDescription(String name) {
        s.tag.setString("Description", name);
    }

    public String getDescription() {
        return s.tag.getString("Description");
    }

    public List<String> getLore() {
        NBTTagList rawLore = s.tag.getCompound("display").getList("Lore");
        List<String> lore = new ArrayList<String>();
        for (int x = 0; x < rawLore.size(); x++) {
            lore.add(((NBTTagString) rawLore.get(x)).data);
        }
        return lore;
    }
    
    public void setLore(List<String> lore) {
        NBTTagList list = new NBTTagList();
        for (String s : lore) {
            NBTTagString st = new NBTTagString(s);
            st.data = s;
            list.add(st);
        }
        s.tag.getCompound("display").set("Lore", list);
    }

    public void setLore(String[] lore) {
        NBTTagList list = new NBTTagList();
        for (int x = 0; x < lore.length; x++) {
            NBTTagString st = new NBTTagString(lore[x]);
            st.data = lore[x];
            list.add(st);
        }
        s.tag.getCompound("display").set("Lore", list);
    }

}
