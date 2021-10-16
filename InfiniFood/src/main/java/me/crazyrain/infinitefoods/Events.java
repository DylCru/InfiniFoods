package me.crazyrain.infinitefoods;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

public class Events implements Listener {

    Infinitefoods plugin;

    public Events(Infinitefoods plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void dontEat(PlayerItemConsumeEvent e){
        if (!(e.getItem().hasItemMeta())){
            return;
        }
        if (e.getItem().getItemMeta().hasEnchant(Enchantment.ARROW_INFINITE)){
            Player player = e.getPlayer();

            player.getInventory().addItem(e.getItem());
            player.getInventory().getItemInMainHand().setAmount(1);
            player.updateInventory();
        }
    }

    @EventHandler
    public void makeinfinite(PrepareAnvilEvent e){
         ItemStack[] contents = e.getInventory().getContents();
         if (contents[0] == null || contents[1] == null){
             return;
         }
         if (!contents[0].getType().isEdible()){
             return;
         }
         if (contents[1].getType().equals(Material.ENCHANTED_BOOK)){
             EnchantmentStorageMeta meta = (EnchantmentStorageMeta) contents[1].getItemMeta(); //Gets the enchant stored on the book
             if (meta.hasStoredEnchant(Enchantment.ARROW_INFINITE)){ //Checks if the book is Infinity
                 ItemStack item = contents[0].clone();
                 e.setResult(infinite(item));
             }
         }
    }

    @EventHandler
    public void giveitem(InventoryClickEvent e){
        if (!(e.getClickedInventory() instanceof AnvilInventory)){
            return;
        }
        if (e.getCurrentItem() == null){
            return;
        }
        ItemStack result = e.getCurrentItem();

        if (!result.hasItemMeta()){
            return;
        }

        if (result.getItemMeta().hasEnchant(Enchantment.ARROW_INFINITE)){
            if (result.getType().equals(Material.BOW)){
                return;
            }
            Player player = (Player) e.getWhoClicked();
            Integer cost = plugin.getConfig().getInt("infinite-cost");
            String denyText = plugin.getConfig().getString("anvil-message");

            if (!player.hasPermission("infinityfoods.anvil")){
                assert denyText != null;
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', denyText));
                return;
            }

            if (result.getAmount() > 1){
                player.sendMessage(ChatColor.RED + "You can only make 1 food infinite at a time");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1.0f);
                return;
            }

            if (plugin.getConfig().getStringList("banned-foods").contains(result.getType().name().toString())){
                return;
            }

            if (player.getLevel() >= cost){
                player.getInventory().addItem(result);
                player.sendMessage(ChatColor.GREEN + "You made your food infinite!");
                player.setLevel(player.getLevel() - cost);
                e.getClickedInventory().clear();
            } else {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You need " + String.valueOf(cost) + " levels to make your food infinite");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1.0f);
            }


        }
    }

    public ItemStack infinite(ItemStack item){
        ItemMeta itemMeta = item.getItemMeta();
        if (plugin.getConfig().getStringList("banned-foods").contains(item.getType().name().toString())){
            itemMeta.setDisplayName(ChatColor.RED + "This food is unable to be made infinite");
        } else{
            itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        }
        item.setItemMeta(itemMeta);
        return item;
    }
}
