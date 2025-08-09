package me.BMC.pKHackathon;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class MoveListener implements Listener {


    @EventHandler
    public void onPlayerLeftClick(PlayerInteractEvent event) {
         Player player = event.getPlayer();
         BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        if (bPlayer == null) return;
        if (!bPlayer.canBend(CoreAbility.getAbility(DustDevil.class))) return;

        String bound = bPlayer.getBoundAbilityName();
        if (!bound.equalsIgnoreCase("DustDevil")) return;

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK && !player.isSneaking()) {
           new DustDevil(player);
       }
    }
    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        final Player player = event.getPlayer();
        final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if (bPlayer == null) return;
        if (!bPlayer.canBend(CoreAbility.getAbility(DustDevil.class))) return;

        String bound = bPlayer.getBoundAbilityName();
        if (!bound.equalsIgnoreCase("DustDevil")) return;

        final DustDevil move = CoreAbility.getAbility(player, DustDevil.class);

        if (player.getLocation().subtract(0, DustDevil.rideheight, 0).getBlock().getType() == Material.SAND && move != null) {
            event.setCancelled(true);
            new DustDevil(player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if (bPlayer == null || !bPlayer.canBend(CoreAbility.getAbility(DustDevil.class))) return;

        String bound = bPlayer.getBoundAbilityName();
        if (!bound.equalsIgnoreCase("DustDevil")) return;

        DustDevil move = CoreAbility.getAbility(player, DustDevil.class);
        if (move != null) {
            event.setCancelled(true);
            move.progress();
        }
    }
    @EventHandler
    public void onPlayerExitBoat(VehicleExitEvent event) {
        Entity livingEntity = event.getExited();

        if (livingEntity instanceof Player) {
            Player player = ((Player) livingEntity).getPlayer();
            assert player != null;
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

                if (bPlayer == null) return;
                if (!bPlayer.canBend(CoreAbility.getAbility(DustDevil.class))) return;

                CoreAbility instances = (CoreAbility) CoreAbility.getAbilitiesByInstances();
                String bound = bPlayer.getBoundAbilityName();

                if (CoreAbility.getAbility(DustDevil.class) == instances && bound.equalsIgnoreCase("DustDevil")) {
                    event.setCancelled(true);
                }

            }
    }

    @EventHandler
    public void onPlayerEnterBoat(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        Entity entity = event.getRightClicked();
        if (bPlayer == null) return;
        if (!bPlayer.canBend(CoreAbility.getAbility(DustDevil.class))) return;

        if (entity instanceof Boat) {
            CoreAbility instances = (CoreAbility) CoreAbility.getAbilitiesByInstances();
            String bound = bPlayer.getBoundAbilityName();

            if (CoreAbility.getAbility(DustDevil.class) == instances && bound.equalsIgnoreCase("DustDevil")) {
                event.setCancelled(true);

            }

        }
    }
}
