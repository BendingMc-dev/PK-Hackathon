package me.BMC.pKHackathon;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.PKListener;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import me.BMC.pKHackathon.PKHackathon;

public class MoveListener implements Listener {
    public PKHackathon ability = PKHackathon.getAbility();
    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        final Player player = event.getPlayer();
        final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if (!bPlayer.canBend())
        if (bPlayer == null) return;
        String bound = bPlayer.getBoundAbilityName();
        if (bound.equalsIgnoreCase("PKHackathon")) {
            final PKHackathon move = CoreAbility.getAbility(player, PKHackathon.class);

            if (player.getLocation().subtract(0, PKHackathon.mobilityHeight, 0).getBlock().getType() == Material.SAND && move != null) {
                new PKHackathon(player);
            }
        }
    }
}
