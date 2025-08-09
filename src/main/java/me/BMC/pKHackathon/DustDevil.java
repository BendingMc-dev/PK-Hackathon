package me.BMC.pKHackathon;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;


public class DustDevil extends SandAbility implements AddonAbility {
    private Vector direction;
    private final ArrayList<Entity> affectedEntities;
    public static double rideheight;
    public final double ridespeed;

    @Attribute(Attribute.COOLDOWN)
    private final long cooldown;
    @Attribute(Attribute.DAMAGE)
    private final double damage;
    @Attribute(Attribute.HEIGHT)
    private final double height;
    @Attribute(Attribute.RADIUS)
    private final double radius;
    @Attribute(Attribute.RANGE)
    private final double range;
    @Attribute(Attribute.SPEED)
    private final double speed;
    private final double lifetime;

    private Location origin;
    private Location currentLoc;
    private Location destination;
    private final String path;
    private final double heightParticle;
    private final double degreeParticle;


    public DustDevil(Player player) {
        super(player);

        this.affectedEntities = new ArrayList<>();
        this.path = "ExtraAbilities.GANG.DustDevil.";

        rideheight = ConfigManager.getConfig().getDouble(path + "Rideheight", 5);
        this.ridespeed = ConfigManager.getConfig().getDouble(path + "Ridespeed", 5);
        this.speed = ConfigManager.getConfig().getDouble(path + "Speed", 2);
        this.lifetime = ConfigManager.getConfig().getDouble(path + "Lifetime", 5000);
        this.cooldown = ConfigManager.getConfig().getLong(path + "Cooldown", 5000);
        this.damage = ConfigManager.getConfig().getDouble(path + "Damage", 2);
        this.height = ConfigManager.getConfig().getDouble(path + "Height", 5);
        this.radius = ConfigManager.getConfig().getDouble(path + "Radius", 3);
        this.range = ConfigManager.getConfig().getDouble(path + "Range", 3);
        this.heightParticle = ConfigManager.getConfig().getDouble(path + "HeightParticle", 0.2);
        this.degreeParticle = ConfigManager.getConfig().getDouble(path + "DegreeParticle", 10);

        this.currentLoc = player.getLocation();

        this.bPlayer.addCooldown(this);
        this.start();
    }

    public void load() {
        ProjectKorra.log.info(getName() + "has loaded!" + "\n" + getDescription());
        Bukkit.getPluginManager().registerEvents(new MoveListener(), ProjectKorra.plugin);

        ConfigManager.getConfig().addDefault(path + "Rideheight", 5);
        ConfigManager.getConfig().addDefault(path + "Ridespeed", 5);
        ConfigManager.getConfig().addDefault(path + "Speed", 5);
        ConfigManager.getConfig().addDefault(path + "Lifetime", 5000);
        ConfigManager.getConfig().addDefault(path + "Cooldown", 5000);
        ConfigManager.getConfig().addDefault(path + "Damage", 2);
        ConfigManager.getConfig().addDefault(path + "Height", 5);
        ConfigManager.getConfig().addDefault(path + "Radius", 3);
        ConfigManager.getConfig().addDefault(path + "Range", 3);
        ConfigManager.getConfig().addDefault(path + "HeightParticle", 0.2);
        ConfigManager.getConfig().addDefault(path + "DegreeParticle", 10);


    }

    public void progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        } else if (this.currentLoc != null && GeneralMethods.isRegionProtectedFromBuild(this, this.currentLoc)) {
            this.remove();
            return;
        }


        if (System.currentTimeMillis() - getStartTime() >= lifetime) {
            this.remove();
            return;
        }
        if (player.getVehicle() != null) {
            if (player.getVehicle() instanceof Boat boat) {

                this.direction = this.player.getEyeLocation().getDirection().clone().normalize();
                this.direction.setY(0);

                tickBoatMovement(boat);
                drawParticles();
            }
        } else {
            combatParticles();
        }
    }

    public void stop() {
        this.remove();
    }

    public void combatParticles() {
        double xOffset = 0;
        double yOffset = 0;
        double zOffset = 0;
        int particleAmount = 1;

        drawParticles();

        Location spawnLoc = player.getEyeLocation().clone().add(player.getEyeLocation().getDirection().clone().normalize().multiply(0.8)); // distance ahead
        currentLoc = spawnLoc;
        if (getParticles() != null) {
            spawnLoc.getWorld().spawnParticle(getParticles(), spawnLoc, particleAmount, xOffset, yOffset, zOffset, 0, Bukkit.createBlockData(Material.SAND));

        } else {
            spawnLoc.getWorld().spawnParticle(Particle.FALLING_DUST, spawnLoc, particleAmount, xOffset, yOffset, zOffset, 0, Bukkit.createBlockData(Material.SAND));
            playSandbendingSound(spawnLoc);
        }
        damage();
    }

    public void drawParticles() {

        /* r(y) = r^base (r^base - r^top / h) * y
           x = x^0 + r(y) x cos(θ)
           y = z^0 + r(y) x sin(θ)

           r^base: radius at the bottom
           r^top: radius at the top
           h: total height of the tornado
           y: current height (from 0 to h)
           x^0,z^0: base center position
           θ: angle in radians
           Convert degrees to radians: θ = ° * π / 180
         */

        double xOffset = 0;
        double yOffset = 0;
        double zOffset = 0;
        int particleAmount = 1;
        int x = 0;
        int z = 0;

        for (double y = 0; y < height; y += heightParticle) { // Temporarily Twister's code, really want to make our own custom implementation. @Snowy do the math pal
            final double animRadius = ((radius / height) * y);
            for (double i = -180; i <= 180; i += degreeParticle) {
                final Vector animDir = GeneralMethods.rotateXZ(new Vector(1, 0, 1), i);
                final Location animLoc = this.player.getLocation().clone().add(animDir.multiply(animRadius));
                animLoc.add(x, y, z);
                Particle particles = this.getParticles();
                if (particles != null) {
                    if (particles == Particle.FALLING_DUST || particles == Particle.BLOCK || particles == Particle.DUST) {
                        animLoc.getWorld().spawnParticle(particles, animLoc, particleAmount, xOffset, yOffset, zOffset, 0, Bukkit.createBlockData(Material.SAND));
                    } else {
                        animLoc.getWorld().spawnParticle(particles, animLoc, particleAmount, xOffset, yOffset, zOffset);
                    }
                    playSandbendingSound(animLoc);
                }
            }
        }

    }

    public void damage() { // @TODO @RyanDusty are you sure you've done this correctly?
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(currentLoc, 1)) {
            if (entity instanceof LivingEntity && entity.getUniqueId() != player.getUniqueId()) {
                ((LivingEntity) entity).damage(damage);
                return;
            }
        }
    }

    public Particle getParticles() {
        String particleName = ConfigManager.getConfig().getString("ExtraAbilities.GANG.DustDevil.Particle"); // @TODO make this work...
        if (particleName == null || particleName.isEmpty()) {
            return Particle.FALLING_DUST;
        }
        try {
            return Particle.valueOf(particleName);
        } catch (IllegalArgumentException e) {
            return Particle.FALLING_DUST;
        }
    }

    public void tickBoatMovement(Boat boat) {
        boat.setVelocity(direction.multiply(ridespeed).add(new Vector(0, 0.1, 0))); // @TODO adjust the vector to "push" up at start then continue at fixed y above the ground
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
        return this.cooldown;
    }

    @Override
    public String getName() {
        return "DustDevil";
    }

    @Override
    public String getInstructions() {
        return "press x to pay respect"; // CHANGEME
    }

    @Override
    public String getDescription() {
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
}
