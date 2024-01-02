package net.wuchenyu.spawnerminer;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class MiningListener implements  Listener {

    private final NamespacedKey isSpawnerMiner = new NamespacedKey(SpawnerMiner.getInstance(), "isSpawnerMiner");

    @EventHandler
    public void MiningListener(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.SPAWNER) {
            ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
            ItemMeta toolMeta = tool.getItemMeta();

            if (toolMeta != null) {
                PersistentDataContainer dataContainer = toolMeta.getPersistentDataContainer();
                if (Objects.equals(dataContainer.get(isSpawnerMiner, PersistentDataType.STRING), "true")) {
                    // 创建刷怪笼物品
                    ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
                    BlockStateMeta spawnerMeta = (BlockStateMeta) spawnerItem.getItemMeta();

                    // 将方块的状态复制到物品的 meta 中
                    if (spawnerMeta != null) {
                        CreatureSpawner spawnerState = (CreatureSpawner) block.getState();
                        spawnerMeta.setBlockState(spawnerState);
                        spawnerItem.setItemMeta(spawnerMeta);

                        // 掉落带有数据的刷怪笼
                        block.getWorld().dropItemNaturally(block.getLocation(), spawnerItem);
                        event.getPlayer().getInventory().getItemInMainHand().setAmount(0); // 移除玩家手上的镐子
                        event.setDropItems(false); // 取消默认的掉落
                    }
                }
            }
        }
    }
}
