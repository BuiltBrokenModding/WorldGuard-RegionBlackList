package com.builtbroken.region.blacklist.factions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.builtbroken.region.blacklist.PluginRegionBlacklist;
import com.builtbroken.region.blacklist.PluginSupport;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.ps.PS;

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
	
	@Override
	public boolean canBuild(Player player, Block block)
	{
		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		UPlayer me = UPlayer.get(player);
		if (me.isUsingAdminMode()) return true;
		
		return FPerm.BUILD.has(me, PS.valueOf(block), true);
	}
}
