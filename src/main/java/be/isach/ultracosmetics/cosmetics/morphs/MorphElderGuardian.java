package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.morphs.customentities.v1_8_R3.CustomGuardian_1_8_R3;
import be.isach.ultracosmetics.util.ServerVersion;
import be.isach.ultracosmetics.util.customfirework.CustomEntityFirework_1_8_R3;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import be.isach.ultracosmetics.util.EntityUtils;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.customfirework.CustomEntityFirework_1_9_R1;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by Sacha on 19/12/15.
 */
public class MorphElderGuardian extends Morph {

    private boolean cooldown;
    private CustomGuardian_1_8_R3 customGuardian;

    public MorphElderGuardian(UUID owner) {
        super(owner, MorphType.ELDERGUARDIAN);
        if (owner == null) return;
        UltraCosmetics.getInstance().registerListener(this);

        World world = ((CraftWorld) getPlayer().getWorld()).getHandle();

        customGuardian = new CustomGuardian_1_8_R3(world);
        MorphType.customEntities.add(customGuardian);
        customGuardian.check();

        Location location = getPlayer().getLocation();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        customGuardian.setLocation(x, y, z, 0, 0);

        EntitySpawningManager.setBypass(true);
        world.addEntity(customGuardian);
        EntitySpawningManager.setBypass(false);

        getPlayer().setPassenger(customGuardian.getBukkitEntity());

        customGuardian.setInvisible(true);

        new BukkitRunnable() {

            @Override
            public void run() {
                if (getOwner() == null
                        || getPlayer() == null) {
                    cancel();
                    return;
                }
                if (customGuardian == null
                        || !customGuardian.isAlive()) {
                    UltraCosmetics.getPlayerManager().getCustomPlayer(getPlayer()).removeMorph();
                    cancel();
                    return;
                }
            }
        }.runTaskTimerAsynchronously(UltraCosmetics.getInstance(), 0, 1);

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if ((event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK) && !cooldown
                && event.getPlayer() == getPlayer()) {
            shootLaser();
            cooldown = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(UltraCosmetics.getInstance(), new Runnable() {
                @Override
                public void run() {
                    cooldown = false;
                }
            }, 80);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (((CraftEntity) event.getDamager()).getHandle() == customGuardian
                && event.getEntity() == getPlayer())
            event.setCancelled(true);
    }

    private void shootLaser() {
        if (customGuardian == null)
            return;

        final Location FROM = customGuardian.getBukkitEntity().getLocation();
        final Location TO = FROM.clone().add(getPlayer()
                .getLocation().getDirection().multiply(10));

        final ArmorStand armorStand = getPlayer().getWorld().spawn(TO, ArmorStand.class);

        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setSmall(true);

        customGuardian.target(armorStand);

        Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                FireworkEffect.Builder builder = FireworkEffect.builder();
                FireworkEffect effect = builder.flicker(false).trail(false).with(FireworkEffect.Type.BALL_LARGE)
                        .withColor(Color.TEAL).withFade(Color.TEAL).build();

                if (UltraCosmetics.getServerVersion() == ServerVersion.v1_8_R3)
                    CustomEntityFirework_1_8_R3.spawn(TO, effect);
                else if (UltraCosmetics.getServerVersion() == ServerVersion.v1_9_R1)
                    CustomEntityFirework_1_9_R1.spawn(TO, effect);

                Vector vector = TO.toVector().subtract(FROM.toVector());

                Location current = FROM.clone();

                for (int i = 0; i < 10; i++) {
                    for (Entity entity : EntityUtils.getEntitiesInRadius(current, 4.5))
                        if (entity instanceof LivingEntity
                                && entity != getPlayer())
                            MathUtils.applyVelocity(entity, new Vector(0, 0.5d, 0));
                    current.add(vector);
                }

                armorStand.remove();
                customGuardian.target(null);
            }
        }, 25);
    }

    @Override
    public void clear() {
        super.clear();
        if (customGuardian != null)
            customGuardian.dead = true;
        MorphType.customEntities.remove(customGuardian);
    }
}
