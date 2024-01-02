package net.wuchenyu.spawnerminer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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

        if(! commandSender.isOp()) {
            Player player = (Player)commandSender;
            player.sendMessage(ChatColor.RED + "抱歉，仅限op使用此命令");
            return true;
        }

        if (! (commandSender instanceof Player)) {
            SpawnerMiner.getInstance().getServer().getConsoleSender()
                    .sendMessage(ChatColor.RED + "抱歉，不允许控制台使用此命令！");
        }

        switch (sub) {
            case "get" : {
                Player player = (Player)commandSender;
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                if (Objects.nonNull(itemInMainHand) &&
                                ( itemInMainHand.equals(new ItemStack(Material.DIAMOND_PICKAXE))
                                || itemInMainHand.equals(new ItemStack(Material.GOLDEN_PICKAXE))
                                || itemInMainHand.equals(new ItemStack(Material.IRON_PICKAXE))
                                || itemInMainHand.equals(new ItemStack(Material.WOODEN_PICKAXE))
                                || itemInMainHand.equals(new ItemStack(Material.NETHERITE_PICKAXE))
                                || itemInMainHand.equals(new ItemStack(Material.STONE_PICKAXE))
                        )) {   //判断物品是否为镐子
                    ItemMeta itemMeta = itemInMainHand.getItemMeta();

                    //有了meta，就可以调用#getPersistentDataContainer()方法得到持久层容器
                    PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

                    /**
                     * 涉及到NamespaceKey的，就一律不能重复
                     */
                    //在添加前，先删除
                    if (dataContainer.has(isSpawnerMiner, PersistentDataType.STRING)) {
                        //存在
                        dataContainer.remove(isSpawnerMiner);
                    }
                    //添加playername
                    dataContainer.set(isSpawnerMiner, PersistentDataType.STRING, "true");
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
}
