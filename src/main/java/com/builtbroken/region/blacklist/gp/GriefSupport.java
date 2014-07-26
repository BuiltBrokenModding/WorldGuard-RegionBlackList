package com.builtbroken.region.blacklist.gp;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import me.ryanhamshire.GriefPrevention.TextMode;

import org.bukkit.Location;
import org.bukkit.block.Block;
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
	
	@Override
	public boolean canBuild(Player player, Block block)
	{
		String noBuildReason = GriefPrevention.instance.allowBuild(player, block.getLocation());
		if (noBuildReason == null)
			return true;
		return false;
	}
}
