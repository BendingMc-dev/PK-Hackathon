package me.BMC.pKHackathon;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class PKHackathon extends AirAbility implements AddonAbility {

    public double mobilityHeight = ConfigManager.getConfig().getDouble("ExtraAbilities.Sand.PKHackathon.RideHeight", 2); // CHANGEME
    public final double mobilitySpeed = ConfigManager.getConfig().getDouble("ExtraAbilities.Sand.PKHackathon.SeatingSpeed"); //  CHANGEME
    public final double tornadoLifetime = ConfigManager.getConfig().getDouble("ExtraAbilities.Sand.PKHackathon.Lifetime");

    private Permission perm; // DONT CHANGEME

    public PKHackathon(Player player) {
        super(player);


    }

    public void load() {
        ProjectKorra.log.info(getName() + "has loaded!" + "\n" + getDescription());
        Bukkit.getPluginManager().registerEvents(new MoveListener(), ProjectKorra.plugin);

        perm = new Permission("bending.ability.PKHackathon");

        ConfigManager.getConfig().addDefault("ExtraAbilities.Sand.PKHackathon.RideHeight", 2);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Sand.PKHackathon.SeatingSpeed", 0);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Sand.PKHackathon.Cooldown", 5000);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Sand.PKHackathon.Lifetime", 5000);

    }

    public void progress() {
        // if there are reasons to cancel move canel move
        if (System.currentTimeMillis() - getStartTime() >= tornadoLifetime) {
            this.remove();
            return;
        }
        if (player.getVehicle() != null) {
            if (player.getVehicle() instanceof Boat boat) { // Maybe swap for getType() ==
            // new method tickBoatMovement
            // new method drawParticles
                tickBoatMovement(boat);
                drawParticles();
            return;
        }
        }
    }

    public void stop() {
      this.remove();
      Bukkit.getServer().getPluginManager().removePermission(perm);
    }


    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return 0;
    }

    @Override
    public String getName() {
        return "PKHackathon";
    }

    @Override
    public String getInstructions(){
        return "press x to pay respect"; // CHANGEME
    }

    @Override
    public String getDescription(){
        return "Sand Tornado go brrrr"; // CHANGEME
    }


    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public String getAuthor() {
        return "RyanDusty, ShadowTP, SnowyOwl217";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    public void drawParticles() {

    }

    public void tickBoatMovement(Boat boat) {
         //
    }
}
