package me.BMC.pKHackathon;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AirAbility;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PKHackathon extends AirAbility implements AddonAbility {
    public PKHackathon(Player player) {
        super(player);


    }
    public void load() {

    }
    public void progress() {

    }

    @Override
    public boolean isSneakAbility() {
        return false;
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
        return "";
    }

    @Override
    public Location getLocation() {
        return null;
    }

    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "RyanDusty, ShadowThePlayer, SnowyOwl217";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
}
