package me.BMC.pKHackathon;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


public class DustDevil extends SandAbility implements AddonAbility {
    private Vector direction;
    public static double rideheight;
    public final double ridespeed;
    private double distanceTravelled;

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

    private final Location currentLoc;
    private final double heightParticle;
    private final double degreeParticle;


    public DustDevil(Player player) {
        super(player);
        String path = "ExtraAbilities.ShadowTP.DustDevil.";

        this.distanceTravelled = 0;
        rideheight = ConfigManager.getConfig().getDouble(path + "Rideheight", 1);
        this.ridespeed = ConfigManager.getConfig().getDouble(path + "Ridespeed", 2);
        this.speed = ConfigManager.getConfig().getDouble(path + "Speed", 0.35);
        this.lifetime = ConfigManager.getConfig().getDouble(path + "Lifetime", 5000);
        this.cooldown = ConfigManager.getConfig().getLong(path + "Cooldown", 5000);
        this.damage = ConfigManager.getConfig().getDouble(path + "Damage", 2);
        this.height = ConfigManager.getConfig().getDouble(path + "Height", 8);
        this.radius = ConfigManager.getConfig().getDouble(path + "Radius", 4);
        this.range = ConfigManager.getConfig().getDouble(path + "Range", 1000);
        this.heightParticle = ConfigManager.getConfig().getDouble(path + "HeightParticle", 3);
        this.degreeParticle = ConfigManager.getConfig().getDouble(path + "DegreeParticle", 45);

        this.currentLoc = player.getLocation().clone();



        this.bPlayer.addCooldown(this);
        this.start();
    }

    public void load() {
        ProjectKorra.log.info(getName() + " has loaded!" + "\n" + getDescription());
        Bukkit.getPluginManager().registerEvents(new MoveListener(), ProjectKorra.plugin);
        String path = "ExtraAbilities.ShadowTP.DustDevil.";

        ConfigManager.getConfig().addDefault(path + "Rideheight", 1);
        ConfigManager.getConfig().addDefault(path + "Ridespeed", 2);
        ConfigManager.getConfig().addDefault(path + "Speed", 0.35);
        ConfigManager.getConfig().addDefault(path + "Lifetime", 5000);
        ConfigManager.getConfig().addDefault(path + "Cooldown", 5000);
        ConfigManager.getConfig().addDefault(path + "Damage", 2);
        ConfigManager.getConfig().addDefault(path + "Height", 8);
        ConfigManager.getConfig().addDefault(path + "Radius", 4);
        ConfigManager.getConfig().addDefault(path + "Range", 1000);
        ConfigManager.getConfig().addDefault(path + "HeightParticle", 3);
        ConfigManager.getConfig().addDefault(path + "DegreeParticle", 45);
        ConfigManager.getConfig().addDefault(path + "Particle", "");

        ConfigManager.defaultConfig.save();
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

    public void combatParticles() {
        final Player player = getBendingPlayer().getPlayer();
        if (player == null || !player.isOnline()) {
            remove();
            return;
        }

        final World world = player.getWorld();
        final Location eyeLoc = player.getEyeLocation();
        final Vector direction = eyeLoc.getDirection();

        final double stepSize = 0.4;
        final double spiralRadius = 0.6;
        final double spiralTightness = 3.5;
        final int mainParticleAmount = 2;
        final int secondaryParticleAmount = 1;

        BlockData dustParticleData = null;
        if (playerOnRedSand(player)) {
            dustParticleData = Bukkit.createBlockData(Material.RED_SAND);
        } else if (playerOnSand(player)) {
            dustParticleData = Bukkit.createBlockData(Material.SAND);
        }

        Vector perpendicular = new Vector(direction.getZ(), 0, -direction.getX()).normalize();
        if (perpendicular.lengthSquared() < 1E-6) {
            perpendicular = new Vector(1, 0, 0);
        }

        for (distanceTravelled = 0; distanceTravelled < range; distanceTravelled += stepSize) {
            Location centerPoint = eyeLoc.clone().add(direction.clone().multiply(distanceTravelled));
            drawParticles();

            if (GeneralMethods.isSolid(centerPoint.getBlock()) || isWater(centerPoint.getBlock())) {
                createImpactEffect(centerPoint, dustParticleData);
                break;
            }

            double angle = distanceTravelled * spiralTightness;
            Vector spiralOffset = perpendicular.clone().rotateAroundAxis(direction, Math.toRadians(angle)).multiply(spiralRadius);
            Location spawnLoc = centerPoint.add(spiralOffset);

            if (dustParticleData != null) {
                world.spawnParticle(Particle.FALLING_DUST, spawnLoc, mainParticleAmount, 0.1, 0.1, 0.1, 0.02, dustParticleData);
            } else {
                world.spawnParticle(Particle.SMOKE, spawnLoc, mainParticleAmount, 0.05, 0.05, 0.05, 0.01);
            }

            world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, spawnLoc, secondaryParticleAmount, 0.05, 0.05, 0.05, 0.01);
            world.spawnParticle(Particle.CRIT, centerPoint, secondaryParticleAmount, 0.2, 0.2, 0.2, 0.05);

            playSandbendingSound(spawnLoc);
            damage(centerPoint);
        }

        remove();
    }

    private void createImpactEffect(Location location, BlockData dustParticleData) {
        if (location.getWorld() == null) {
            return;
        }
        int impactAmount = 15;
        double impactRadius = 0.5;

        if (dustParticleData != null) {
            location.getWorld().spawnParticle(Particle.FALLING_DUST, location, impactAmount, impactRadius, impactRadius, impactRadius, 0.01, dustParticleData);
        } else {
            location.getWorld().spawnParticle(Particle.SMOKE, location, impactAmount, impactRadius, impactRadius, impactRadius, 0.02);
        }

        location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0.1, 0.1, 0.1, 0);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.2F);
    }

    public void drawParticles() {
        double xOffset = 0;
        double yOffset = 0;
        double zOffset = 0;
        int particleAmount = 1;
        int x = 0;
        int z = 0;

        for (double y = 0; y < height; y += heightParticle) {
            final double animRadius = ((radius / height) * y);
            for (double i = -180; i <= 180; i += degreeParticle) {
                final Vector animDir = GeneralMethods.rotateXZ(new Vector(1, 0, 1), i);
                final Location animLoc = this.player.getLocation().clone().add(animDir.multiply(animRadius));
                animLoc.add(x, y, z);
                Particle particles = this.getParticles();
                if (particles != null) {
                        animLoc.getWorld().spawnParticle(particles, animLoc, particleAmount, xOffset, yOffset, zOffset, 3, Bukkit.createBlockData(Material.SAND));
                    } else if (playerOnSand(player) && !playerOnRedSand(player)) {
                        animLoc.getWorld().spawnParticle(Particle.valueOf("FALLING_DUST"), animLoc, particleAmount, xOffset, yOffset, zOffset, 3, Bukkit.createBlockData(Material.SAND));
                        playSandbendingSound(animLoc);
                    } else if (playerOnRedSand(player) && !playerOnSand(player)) {
                        animLoc.getWorld().spawnParticle(Particle.valueOf("FALLING_DUST"), animLoc, particleAmount, xOffset, yOffset, zOffset, 3, Bukkit.createBlockData(Material.RED_SAND));
                        playSandbendingSound(animLoc);
                    }
            }
        }
    }


    public void damage(Location location) {
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 1)) {
            if (entity instanceof LivingEntity && entity.getUniqueId() != player.getUniqueId()) {
                ((LivingEntity) entity).damage(damage);
                return;
            }
        }
    }

    public Particle getParticles() {
        String path = "ExtraAbilities.ShadowTP.DustDevil.";
        String particleName = ConfigManager.getConfig().getString(path + "Particle");
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
        boat.setVelocity(direction.multiply(ridespeed).add(new Vector(0, 0.1, 0)));
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
        return "Left click in a boat on sand to trigger a sand tornado which propels the boat forwards. Alternatively, left click whilst standing on sand without a boat to summon a tornado and shoot a sand projectile.";
    }

    @Override
    public String getDescription() {
        return "Copilot said: DustDevil is a versatile Sandbending ability with\n" +
                "DustDevil is a versatile Sandbending ability with two distinct modes.\n" +
                "\n" +
                "When the user left-clicks while in a boat, they are propelled forward by a swirling sand tornado, allowing for rapid movement across water. The tornado is visually represented by particles of sand or red sand, depending on the environment.\n" +
                "\n" +
                "If the user left-clicks while on foot, they shoot a destructive beam of smoky sand. This projectile travels in the direction the user is facing, damaging any entities in its path and creating a small impact explosion. This makes DustDevil a powerful tool for both transportation and combat.";
    }

    @Override
    public Location getLocation() {
        return this.currentLoc;
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