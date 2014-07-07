package com.builtbroken.region.blacklist.factions;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.builtbroken.region.blacklist.IBlackListRegion;
import com.builtbroken.region.blacklist.PluginRegionBlacklist;

/**
 * Faction support handling
 * 
 * @author Robert Seifert
 * 
 */
public class FactionSupport implements IBlackListRegion
{
	private PluginRegionBlacklist plugin = null;

	public FactionSupport(PluginRegionBlacklist plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public void update(Player player, Location location)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		return false;
	}

	@Override
	public void save()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void load()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void unload(Player player)
	{
		// TODO Auto-generated method stub

	}
	

	@Override
	public String getName()
	{
		return "Factions";
	}
}
