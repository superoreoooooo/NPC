package win.oreo.npc.util.quest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class QuestInventory implements InventoryHolder {
    private ItemStack item;
    private Inventory inventory;
    private String npcName;

    public QuestInventory(ItemStack item, String npcName) {
        this.npcName = npcName;
        this.item = item;
        this.init();
    }

    private void init() {
        inventory = Bukkit.createInventory(this, 27, npcName + "의 보상");
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7));
        }
        inventory.setItem(13, item);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getNpcName() {
        return npcName;
    }
}
