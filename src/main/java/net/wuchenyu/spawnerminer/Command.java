package net.wuchenyu.spawnerminer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Command implements TabExecutor {

    private NamespacedKey isSpawnerMiner = new NamespacedKey(SpawnerMiner.getInstance(), "isSpawnerMiner");

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command
            , String s, String[] strings) {
        int len = strings.length;
        if(len < 1){
            help(commandSender);
            return true;
        }

        String sub = strings[0].toLowerCase();

        if (! (commandSender instanceof Player)) {
            SpawnerMiner.getInstance().getServer().getConsoleSender()
                    .sendMessage(ChatColor.RED + "抱歉，不允许控制台使用此命令！");
        }

        if(! commandSender.isOp()) {
            Player player = (Player)commandSender;
            player.sendMessage(ChatColor.RED + "抱歉，仅限op使用此命令");
            return true;
        }

        switch (sub) {
            case "get" : {
                Player player = (Player) commandSender;
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                Material materialInHand = itemInMainHand.getType();

                if (isPickaxe(materialInHand)) {
                    ItemMeta itemMeta = itemInMainHand.getItemMeta();

                    if (itemMeta == null) {
                        itemMeta = Bukkit.getItemFactory().getItemMeta(materialInHand);
                    }

                    PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
                    dataContainer.set(isSpawnerMiner, PersistentDataType.STRING, "true");
                    itemInMainHand.setItemMeta(itemMeta);
                    player.sendMessage(ChatColor.GREEN + "这个镐子现在可以挖掘刷怪笼了！");
                } else {
                    player.sendMessage(ChatColor.RED + "手上的物品不是一个镐子");
                }
                return true;
            }
            case "check" : {
                Player player = (Player) commandSender;
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                if (Objects.nonNull(itemInMainHand)) {
                    ItemMeta itemMeta = itemInMainHand.getItemMeta();
                    PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
                    if (Objects.equals(dataContainer.get(isSpawnerMiner, PersistentDataType.STRING), "true")) {
                        player.sendMessage(ChatColor.GOLD + "这个物品可以挖掘刷怪笼");
                    } else {
                        player.sendMessage(ChatColor.GOLD + "这个物品不能挖掘刷怪笼");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "对不起你手上没有物品");
                }
                return true;
            }
            case "remove" : {
                Player player = (Player) commandSender;
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                if (Objects.nonNull(itemInMainHand)) {
                    ItemMeta itemMeta = itemInMainHand.getItemMeta();
                    PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
                    if (Objects.equals(dataContainer.get(isSpawnerMiner, PersistentDataType.STRING), "true")) {
                        //添加playername
                        dataContainer.set(isSpawnerMiner, PersistentDataType.STRING, "false");
                    }
                    player.sendMessage(ChatColor.GOLD + "已经移除这个物品的刷怪笼挖掘能力");
                } else {
                    player.sendMessage(ChatColor.RED + "对不起你手上没有物品");
                }
                return true;
            }
            case "help" :
            default: {
                help(commandSender);
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        int len = strings.length;
        if(commandSender.isOp() && commandSender instanceof Player){
            if (len == 1) {
                return filter(Arrays.asList("get", "check", "remove", "help"), strings);
            }
        }
        return null;
    }

    public void help (CommandSender sender) {
        Player player = (Player) sender;
        player.sendMessage(ChatColor.GREEN + "使用方法: ");
        player.sendMessage(ChatColor.GREEN + "spawner_miner/sm get 为镐子添加刷怪笼挖掘能力");
        player.sendMessage(ChatColor.GREEN + "spawner_miner/sm remove 移除能力");
        player.sendMessage(ChatColor.GREEN + "spawner_miner/sm check 检查是否具有挖掘能力");
        player.sendMessage(ChatColor.GREEN + "spawner_miner/sm help 显示本信息");
    }

    public static List<String> filter(List<String> list, String[] args) {
        String latest = null;
        if (args.length != 0) {
            latest = args[args.length - 1];
        }
        if (list.isEmpty() || latest == null)
            return list;
        String ll = latest.toLowerCase();
        List<String> filteredList = new ArrayList<>(list);
        filteredList.removeIf(k -> !k.toLowerCase().startsWith(ll));
        return filteredList;
    }

    private boolean isPickaxe(Material material) {
        return material == Material.DIAMOND_PICKAXE || material == Material.GOLDEN_PICKAXE
                || material == Material.IRON_PICKAXE || material == Material.WOODEN_PICKAXE
                || material == Material.NETHERITE_PICKAXE || material == Material.STONE_PICKAXE;
    }
}
