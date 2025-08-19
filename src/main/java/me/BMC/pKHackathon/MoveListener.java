package me.BMC.pKHackathon;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

    private boolean playerOnSand(Player player) {
        Location playerLoc = player.getLocation().clone();
        Block blockBelow = playerLoc.subtract(0, 1, 0).getBlock();
        return blockBelow.getType() == Material.SAND;
    }

    private boolean playerOnRedSand(Player player) {
        Location playerLoc = player.getLocation().clone();
        Block blockBelow = playerLoc.subtract(0, 1, 0).getBlock();
        return blockBelow.getType() == Material.RED_SAND;
    }

    @EventHandler
    public void onPlayerLeftClick(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        if (!playerOnSand(player) && !playerOnRedSand(player)) {
            return;
        }
        if (bPlayer == null) return;
        if (!bPlayer.canBend(CoreAbility.getAbility(DustDevil.class))) return;

        String bound = bPlayer.getBoundAbilityName();
        if (!bound.equalsIgnoreCase("DustDevil")) return;



        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK && !player.isSneaking()) {
            new DustDevil(player);

        }
        new DustDevil(player);

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
            if (player.isInsideVehicle()) {
                event.setCancelled(true);
                move.progress();
            }
        }
    }
    @EventHandler
    public void onPlayerExitBoat(VehicleExitEvent event) {
        Entity exited = event.getExited();

        if (exited instanceof Player player) {
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

            if (bPlayer == null) return;
            if (!bPlayer.canBend(CoreAbility.getAbility(DustDevil.class))) return;

            String bound = bPlayer.getBoundAbilityName();
            DustDevil devil = CoreAbility.getAbility(player, DustDevil.class);

            if (devil != null && bound != null && bound.equalsIgnoreCase("DustDevil")) {
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
            String bound = bPlayer.getBoundAbilityName();
            DustDevil devil = CoreAbility.getAbility(player, DustDevil.class);

            if (devil != null && bound != null && bound.equalsIgnoreCase("DustDevil")) {
                event.setCancelled(true);

            }
        }
    }
}
