package com.builtbroken.region.blacklist.factions;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.builtbroken.region.api.IBlackListRegion;
import com.builtbroken.region.blacklist.PluginRegionBlacklist;
import com.builtbroken.region.blacklist.PluginSupport;

/**
 * Faction support handling
 * 
 * @author Robert Seifert
 * 
 */
public class FactionSupport extends PluginSupport
{
	public FactionSupport(PluginRegionBlacklist plugin)
	{
		super(plugin, "Factions");
	}

	@Override
	public void update(Player player, Location location)
	{
		// TODO Auto-generated method stub

	}
}
