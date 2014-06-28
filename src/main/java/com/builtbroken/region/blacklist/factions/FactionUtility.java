package com.builtbroken.region.blacklist.factions;

import org.bukkit.plugin.Plugin;

import com.builtbroken.region.blacklist.PluginRegionBlacklist;
import com.massivecraft.factions.Factions;

public class FactionUtility
{
	private static Factions factions = null;

	/** Gets the worldguard plugin currently loaded */
	protected static Factions factions()
	{
		if (factions == null)
		{
			Plugin plugin = PluginRegionBlacklist.instance().getServer().getPluginManager().getPlugin("Factions");

			if (plugin instanceof Factions)
			{
				factions = (Factions) plugin;
			}
		}
		return factions;
	}
}
