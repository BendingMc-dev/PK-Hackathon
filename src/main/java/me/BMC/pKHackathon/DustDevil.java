package me.BMC.pKHackathon;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
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
    private double heightParticle;
    private double degreeParticle;
    

    public DustDevil(Player player) {
        super(player);


        this.affectedEntities = new ArrayList<>();
        this.path = "ExtraAbilities.GANG.DustDevil.";


        this.rideheight = ConfigManager.getConfig().getDouble(path + "Rideheight", 5);
        this.ridespeed = ConfigManager.getConfig().getDouble(path + "Ridespeed", 5);
        this.speed = ConfigManager.getConfig().getDouble(path + "Speed", 5);
        this.lifetime = ConfigManager.getConfig().getDouble(path + "Lifetime", 5000);
        this.cooldown = ConfigManager.getConfig().getLong(path + "Cooldown", 5000);
        this.damage = ConfigManager.getConfig().getDouble(path + "Damage", 2);
        this.height = ConfigManager.getConfig().getDouble(path + "Height", 5);
        this.radius = ConfigManager.getConfig().getDouble(path + "Radius", 3);
        this.range = ConfigManager.getConfig().getDouble(path + "Range", 3);
        this.heightParticle = ConfigManager.getConfig().getDouble(path + "HeightParticle", 0.2);
        this.degreeParticle = ConfigManager.getConfig().getDouble(path + "DegreeParticle", 10);


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
        }   else if (this.currentLoc != null && GeneralMethods.isRegionProtectedFromBuild(this, this.currentLoc)) {
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
		
        }
	    else {
		combatParticles();
	    }
    }

    public void stop() {
      this.remove();
      // Bukkit.getServer().getPluginManager().removePermission(perm); Not sure why I had this line here?
    }
    public void combatParticles () {
       
       affectEntity();    

	    
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
                          animLoc.getWorld().spawnParticle(Particle.valueOf(String.valueOf(particles)), animLoc, particleAmount, xOffset, yOffset, zOffset);
                          playSandbendingSound(animLoc);
                      }
                      else
                      animLoc.getWorld().spawnParticle(Particle.valueOf("FALLING_DUST"), animLoc, particleAmount, xOffset, yOffset, zOffset);
                      playSandbendingSound(animLoc);
                }
            }

        }
    
    public void affectEntity() {
	ArrayList<Entity> affectedEntities = GeneralMethods.getEntitiesAroundPoint(currentLoc, radius);
        for (Entity entities : entity) {
		if (!(entity.getUniqueId instanceof Player)) {
			DamageHandler.damageEntity(entity, damage, this);
			return;
		}
		else {
			Player player = (Player) entity;
			DamageHandler.damageEntity(player, damage, this);
		}
	}
      	
	    
	    

	    
    }
    public Particle getParticles() {
        Particle particle = Particle.valueOf(ConfigManager.getConfig().getString("ExtraAbilities.GANG.DustDevil.Particle"));
        if (particle != null) {
            return particle;
        }
        return Particle.BLOCK;
    }

    public void tickBoatMovement(Boat boat) {
        boat.setGravity(false);
        boat.setVelocity(direction.multiply(ridespeed));
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
        return "Dust Devil";
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
