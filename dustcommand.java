package com.dust.sword;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
public class DustCommand implements CommandExecutor {
    DustSword plugin;
    public DustCommand(DustSword p){plugin=p;}
    public static ItemStack createDustSword(){
        ItemStack s=new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta m=s.getItemMeta();
        m.setDisplayName(ChatColor.DARK_GRAY+""+ChatColor.BOLD+"Dust Sword");
        m.setLore(Arrays.asList("§7Shift+Right: Heaven Fall","§7Double Shift: Heal","§7Shift+Left: Blade Storm"));
        m.setUnbreakable(true);
        s.setItemMeta(m);
        return s;
    }
    public boolean onCommand(CommandSender s, Command c, String l, String[] a){
        Player t=a.length>1?plugin.getServer().getPlayer(a[1]):(s instanceof Player?(Player)s:null);
        if(t!=null) t.getInventory().addItem(createDustSword());
        return true;
    }
}