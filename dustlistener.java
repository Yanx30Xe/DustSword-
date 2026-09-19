package com.dust.sword;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import java.util.*;
public class DustListener implements Listener {
    DustSword plugin;
    Map<UUID, Long> cd1=new HashMap<>(),cd2=new HashMap<>(),cd3=new HashMap<>(),lastShift=new HashMap<>();
    Map<UUID, List<ItemDisplay>> activeBlades=new HashMap<>();
    public DustListener(DustSword p){plugin=p;}
    boolean isDust(ItemStack i){return i!=null&&i.hasItemMeta()&&i.getItemMeta().getDisplayName()!=null&&i.getItemMeta().getDisplayName().contains("Dust Sword");}
    boolean checkCd(Map<UUID, Long> m,UUID id,int sec,Player p,String s){if(m.containsKey(id)){long sisa=(m.get(id)+sec*1000L-System.currentTimeMillis())/1000;if(sisa>0){p.sendActionBar("§c"+s+" "+sisa+"s");return false;}}return true;}
    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p=e.getPlayer();
        if(!isDust(p.getInventory().getItemInMainHand())||!p.isSneaking()) return;
        if(e.getAction()==Action.RIGHT_CLICK_AIR||e.getAction()==Action.RIGHT_CLICK_BLOCK){
            if(activeBlades.containsKey(p.getUniqueId())){shootBlades(p);return;}
            if(!checkCd(cd1,p.getUniqueId(),40,p,"Heaven Fall")) return;
            Location loc=p.getLocation().clone().add(0,10,0);
            ItemDisplay d=(ItemDisplay)p.getWorld().spawnEntity(loc,EntityType.ITEM_DISPLAY);
            d.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
            d.setTransformation(new Transformation(new Vector3f(),new AxisAngle4f((float)Math.toRadians(180),1,0,0),new Vector3f(1f),new AxisAngle4f()));
            new BukkitRunnable(){int t=0;public void run(){if(t>20||d.isDead()){p.getWorld().spawnParticle(Particle.EXPLOSION,d.getLocation(),5);p.getWorld().playSound(d.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,1,1);for(Entity en:d.getWorld().getNearbyEntities(d.getLocation(),4,4,4))if(en instanceof LivingEntity le&&en!=p)le.damage(8,p);d.remove();cancel();return;}d.teleport(d.getLocation().subtract(0,0.5,0));t++;}}.runTaskTimer(plugin,0,1);
            cd1.put(p.getUniqueId(),System.currentTimeMillis());
        }
        if(e.getAction()==Action.LEFT_CLICK_AIR||e.getAction()==Action.LEFT_CLICK_BLOCK){
            if(!checkCd(cd3,p.getUniqueId(),35,p,"Blade Storm")) return;
            spawnBlades(p);cd3.put(p.getUniqueId(),System.currentTimeMillis());
        }
    }
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e){
        if(!e.isSneaking()) return; Player p=e.getPlayer(); if(!isDust(p.getInventory().getItemInMainHand())) return;
        long now=System.currentTimeMillis();
        if(lastShift.containsKey(p.getUniqueId())&&now-lastShift.get(p.getUniqueId())<400){
            if(!checkCd(cd2,p.getUniqueId(),30,p,"Heal")) return;
            p.setHealth(Math.min(p.getMaxHealth(),p.getHealth()+8));
            p.getWorld().spawnParticle(Particle.HEART,p.getLocation().add(0,1,0),10);
            cd2.put(p.getUniqueId(),now);
        }
        lastShift.put(p.getUniqueId(),now);
    }
    void spawnBlades(Player p){
        List<ItemDisplay> list=new ArrayList<>();
        for(int i=0;i<5;i++){double a=(Math.PI*2/5)*i;Location loc=p.getLocation().clone().add(Math.cos(a)*1.5,1,Math.sin(a)*1.5);ItemDisplay d=(ItemDisplay)p.getWorld().spawnEntity(loc,EntityType.ITEM_DISPLAY);d.setItemStack(new ItemStack(Material.NETHERITE_SWORD));list.add(d);}
        activeBlades.put(p.getUniqueId(),list);
        new BukkitRunnable(){int tick=0;public void run(){if(tick>100||!activeBlades.containsKey(p.getUniqueId())){cancel();return;}List<ItemDisplay> l=activeBlades.get(p.getUniqueId());if(l==null){cancel();return;}for(int i=0;i<l.size();i++){double a=(Math.PI*2/5)*i+tick*0.2;l.get(i).teleport(p.getLocation().clone().add(Math.cos(a)*1.5,1,Math.sin(a)*1.5));}tick++;}}.runTaskTimer(plugin,0,1);
    }
    void shootBlades(Player p){
        List<ItemDisplay> list=activeBlades.remove(p.getUniqueId());if(list==null) return;
        for(ItemDisplay d:list){d.setVelocity(p.getEyeLocation().getDirection().multiply(2));new BukkitRunnable(){public void run(){for(Entity en:d.getWorld().getNearbyEntities(d.getLocation(),1,1,1))if(en instanceof LivingEntity le&&en!=p){le.damage(6,p);d.remove();cancel();return;}if(d.getTicksLived()>60){d.remove();cancel();}}}.runTaskTimer(plugin,0,1);}
    }
}