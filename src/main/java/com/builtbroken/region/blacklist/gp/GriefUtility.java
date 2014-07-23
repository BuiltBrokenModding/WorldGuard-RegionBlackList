package com.builtbroken.region.blacklist.gp;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Handles common factions related methods
 * 
 * @author Robert Seifert
 * 
 */
public class GriefUtility
{
	private static GriefPrevention plugin = null;

	public static DataStore getData()
	{
		return plugin.dataStore;
	}

	public static Claim getClaim(Location location)
	{
		return getData().getClaimAt(location, false, null);
	}

	public static PlayerData getPlayer(Player player)
	{
		return getPlayer(player.getName());
	}

	public static PlayerData getPlayer(String name)
	{
		return getData().getPlayerData(name);
	}

	/** Gets the worldguard plugin currently loaded */
	protected static GriefPrevention plugin()
	{
		if (plugin == null)
		{
			Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("GriefPrevention");

			if (plugin instanceof GriefPrevention)
			{
				plugin = (GriefPrevention) plugin;
			}
		}
		return plugin;
	}
}
