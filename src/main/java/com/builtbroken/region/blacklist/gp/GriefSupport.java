package com.builtbroken.region.blacklist.gp;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.builtbroken.region.blacklist.PluginRegionBlacklist;
import com.builtbroken.region.blacklist.PluginSupport;

/**
 * Grief Prevention support handling
 * 
 * @author Robert Seifert
 * 
 */
public class GriefSupport extends PluginSupport
{
	public GriefSupport(PluginRegionBlacklist plugin)
	{
		super(plugin, "GriefProtection");
	}

	@Override
	public void update(Player player, Location location)
	{
		// TODO Auto-generated method stub

	}
}