package me.BMC.pKHackathon;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.util.Vector;
import org.bukkit.Particle;

import java.util.ArrayList;

public class DustDevil extends SandAbility implements AddonAbility {

    public static double mobilityHeight = ConfigManager.getConfig().getDouble("ExtraAbilities.GANG.DustDevil.RideHeight", 2); // CHANGEME
    public final double mobilitySpeed = ConfigManager.getConfig().getDouble("ExtraAbilities.GANG.DustDevil.MobilitySpeed");
    public final double tornadoLifetime = ConfigManager.getConfig().getDouble("ExtraAbilities.GANG.DustDevil.Lifetime");
    // public final String heightparticle = ConfigManager.getConfig().getDouble("ExtraAbilities.GANG.DustDevil.HeightParticles"); // CHANGEME
    public Ability ability = this;
    private Vector direction;
    private ArrayList<Entity> affectedEntities;



    private Permission perm;
    private long cooldown;

    public DustDevil(Player player) {
        super(player);
        this.ability = ability;
        this.affectedEntities = new ArrayList<>();

         cooldown = ConfigManager.getConfig().getLong("ExtraAbilities.GANG.DustDevil.Cooldown", 5000);

        this.bPlayer.addCooldown(this);
        this.start();
    }

    public void load() {
        ProjectKorra.log.info(getName() + "has loaded!" + "\n" + getDescription());
        Bukkit.getPluginManager().registerEvents(new MoveListener(), ProjectKorra.plugin);

        perm = new Permission("bending.ability.DustDevil");

        ConfigManager.getConfig().addDefault("ExtraAbilities.GANG.DustDevil.RideHeight", 2);
        ConfigManager.getConfig().addDefault("ExtraAbilities.GANG.DustDevil.MobilitySpeed", 1);
        ConfigManager.getConfig().addDefault("ExtraAbilities.GANG.DustDevil.Cooldown", 5000);
        ConfigManager.getConfig().addDefault("ExtraAbilities.GANG.DustDevil.Lifetime", 5000);

    }

    public void progress() {
        // if there are reasons to cancel move cancel move


        if (System.currentTimeMillis() - getStartTime() >= tornadoLifetime) {
            this.remove();
            return;
        }
        if (player.getVehicle() != null) {
            if (player.getVehicle() instanceof Boat boat) {

            this.direction = this.player.getEyeLocation().getDirection().clone().normalize();
            this.direction.setY(0);

            // new method tickBoatMovement
            // new method drawParticles
                tickBoatMovement(boat);
                drawParticles();
            return;
        }
        } else {

        }
    }

    public void stop() {
      this.remove();
      Bukkit.getServer().getPluginManager().removePermission(perm);
    }

    public void drawParticles() {
        for (int i = 0; i < 360; i += 10) {
            // ok I gotta cook here idk
            // rotate about the circle for increment i
            // and use height of mobilityHeight to calc offset
            // can use method for pvp move too?



            //for (double y = 0; y < mobilityHeight; y += this.twisterHeightParticles) {
            //    final double animRadius = ((radius / mobilityHeight) * y);
            //    for (double i = -180; i <= 180; i += this.twisterDegreeParticles) {
            //        final Vector animDir = GeneralMethods.rotateXZ(new Vector(1, 0, 1), i);
            //        final Location animLoc = this.player.getLocation().clone().add(animDir.multiply(animRadius));
            //        animLoc.add(0, y, 0);
            //        playAirbendingParticles(animLoc, 1, 0, 0, 0);
            //    }
            //}
        } // move it :fire:
    }

    public Particle getHeightParticles() {
        Particle particle = Particle.valueOf(ConfigManager.getConfig().getString("ExtraAbilities.GANG.DustDevil.HeightParticles"));
        if (particle != null) {
            return particle;
        }
        return Particle.BLOCK;
    }

    public void tickBoatMovement(Boat boat) {
        boat.setGravity(false);
        boat.setVelocity(direction.multiply(mobilitySpeed));
    }

        public Ability getThisAbility() {
        return this.ability;
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
        return "DustDevil";
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
    public Location getLocation() { // What is this?
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
}
