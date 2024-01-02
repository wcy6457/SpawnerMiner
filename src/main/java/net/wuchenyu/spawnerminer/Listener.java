package net.wuchenyu.spawnerminer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class Listener implements org.bukkit.event.Listener {

    private final NamespacedKey isSpawnerMiner = new NamespacedKey(SpawnerMiner.getInstance(), "isSpawnerMiner");

    @EventHandler
    public void MiningListener (BlockBreakEvent event) {
        if (event.getBlock().getType().equals(Material.SPAWNER)) {
         ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
            if (Objects.nonNull(itemInMainHand)) {
                Player player = event.getPlayer();
                ItemMeta itemMeta = itemInMainHand.getItemMeta();
                PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
                if (Objects.equals(dataContainer.get(isSpawnerMiner, PersistentDataType.STRING), "true")) {
                    ItemStack itemStack = new ItemStack(Material.SPAWNER);
                    itemStack.setItemMeta( (ItemMeta) event.getBlock().getBlockData() );
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation() ,itemStack);
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }
}
