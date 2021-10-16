package me.crazyrain.infinitefoods;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Commands implements CommandExecutor {

    Infinitefoods plugin;

    public Commands(Infinitefoods plugin){
        this.plugin = plugin;
    }



    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
            System.out.println("[Infini] Only players can use this command!");
        }

        Player player = (Player) sender;

        if (cmd.getLabel().equalsIgnoreCase("infini")){
            if (player.hasPermission("infinitefoods.infini")){
                Integer cost = plugin.getConfig().getInt("infinite-cost");
                if (player.getInventory().getItemInMainHand().getAmount() > 1){
                    player.sendMessage(ChatColor.RED + "You can only make 1 food infinite at a time");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1.0f);
                    return true;
                }

                if (plugin.getConfig().getStringList("banned-foods").contains(player.getInventory().getItemInMainHand().getType().name())){
                    player.sendMessage(ChatColor.RED + "This food is unable to be made infinite");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1.0f);
                    return true;
                }

                if (player.getLevel() >= cost){
                    makeInfinite(player.getInventory().getItemInMainHand(), player);
                    player.setLevel(player.getLevel() - cost);
                } else {
                    player.sendMessage(ChatColor.RED + "You need " + cost + " levels to make your food infinite");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1.0f);
                }

            } else {
                player.sendMessage(ChatColor.RED + "[inf] You don't have the required permission to use this command");
            }

        }

        if (cmd.getLabel().equalsIgnoreCase("infrl")){
            if (player.hasPermission("infinitefoods.admin")){
                player.sendMessage(ChatColor.GREEN + " The infinifood config has been reloaded!");
                plugin.reloadConfig();
            } else {
                player.sendMessage(ChatColor.RED + "[inf] You don't have the required permission to use this command");
            }

        }
        return true;
    }

    public void makeInfinite(ItemStack item, Player player){
        if (!item.getType().isEdible()){
            player.sendMessage(ChatColor.RED + "You need to hold an edible item!");
            return;
        }
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        item.setItemMeta(meta);
        player.sendMessage(ChatColor.GREEN + "You made your food infinite!");
    }
}
