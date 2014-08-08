package com.builtbroken.region.blacklist.gp;

import java.io.File;

import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.builtbroken.region.blacklist.PluginRegionBlacklist;
import com.builtbroken.region.blacklist.PluginSupport;
import com.builtbroken.region.blacklist.References;

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
	public void loadConfig(YamlConfiguration config)
	{
		super.loadConfig(config);
	}

	@Override
	public void createConfig(YamlConfiguration config)
	{
		super.createConfig(config);
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
