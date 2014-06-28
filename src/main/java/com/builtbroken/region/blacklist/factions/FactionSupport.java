package com.builtbroken.region.blacklist.factions;

import org.bukkit.event.Listener;

import com.builtbroken.region.blacklist.PluginRegionBlacklist;

/**
 * Faction support handling
 * 
 * @author Robert Seifert
 * 
 */
public class FactionSupport implements Listener
{
	private PluginRegionBlacklist plugin = null;

	public FactionSupport(PluginRegionBlacklist plugin)
	{
		this.plugin = plugin;
	}
}
