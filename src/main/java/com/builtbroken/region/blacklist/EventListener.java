package com.builtbroken.region.blacklist;

import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class EventListener implements Listener
{
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		WorldGuardPlugin guard = PluginRegionBlacklist.instance().getWorldGuard();
		if (guard != null && event.getPlayer() != null)
		{
			RegionManager manager = guard.getRegionManager(event.getPlayer().getWorld());
			if (manager != null)
			{
				Location loc = event.getPlayer().getLocation();
				Vector vec = new Vector(loc.getX(), loc.getY(), loc.getZ());
				ApplicableRegionSet set = manager.getApplicableRegions(vec);
				Iterator<ProtectedRegion> it = set.iterator();
				while (it.hasNext())
				{
					ProtectedRegion region = it.next();
					event.getPlayer().sendMessage("Region: " + region.getTypeName());
					if (region.getFlags() != null)
					{
						for(Entry<Flag<?>, Object> entry : region.getFlags().entrySet())
						{
							event.getPlayer().sendMessage("\tFlag: " + entry.getKey().getClass().getSimpleName() + " Value: " + entry.getValue());
						}
						if (region.getFlags().containsKey(PluginRegionBlacklist.ALLOW_ITEM_FLAG))
						{
							event.getPlayer().sendMessage("Region has Item Allow flag");
						}
						else if (region.getFlags().containsKey(PluginRegionBlacklist.DENY_ITEM_FLAG))
						{
							event.getPlayer().sendMessage("Region has Item Deny flag");
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt)
	{
		evt.getPlayer().sendMessage("Hello there");
	}
}
