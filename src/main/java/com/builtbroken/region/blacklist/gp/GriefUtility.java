package com.builtbroken.region.blacklist.gp;

import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Handles common factions related methods
 * 
 * @author Robert Seifert
 * 
 */
public class GriefUtility
{
	private static GriefPrevention factions = null;

	/** Gets the worldguard plugin currently loaded */
	protected static GriefPrevention factions()
	{
		if (factions == null)
		{
			Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("GriefPrevention");

			if (plugin instanceof GriefPrevention)
			{
				factions = (GriefPrevention) plugin;
			}
		}
		return factions;
	}
}
