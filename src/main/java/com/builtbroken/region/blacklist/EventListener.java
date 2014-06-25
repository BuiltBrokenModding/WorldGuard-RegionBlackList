package com.builtbroken.region.blacklist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class EventListener implements Listener
{
	private HashMap<String, LinkedList<RegionItems>> playerItemsPerRegion = new LinkedHashMap<String, LinkedList<RegionItems>>();

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		update(event.getPlayer());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		update(event.getPlayer());
	}

	/** Updated item data for player */
	public void update(Player player)
	{
		WorldGuardPlugin guard = PluginRegionBlacklist.instance().getWorldGuard();
		if (guard != null && player != null)
		{
			Location loc = player.getLocation();
			Vector vec = new Vector(loc.getX(), loc.getY(), loc.getZ());
			RegionManager manager = guard.getRegionManager(player.getWorld());

			// Get player's existing region data
			List<RegionItems> regions = new LinkedList<RegionItems>();
			if (playerItemsPerRegion.containsKey(player.getName()))
			{
				regions = playerItemsPerRegion.get(player.getName());
			}

			if (manager != null)
			{
				// Look for new regions that the player is now in
				ApplicableRegionSet set = manager.getApplicableRegions(vec);
				Iterator<ProtectedRegion> it = set.iterator();
				while (it.hasNext())
				{
					ProtectedRegion region = it.next();
					if (!regions.contains(region) && (region.getFlags().containsKey(PluginRegionBlacklist.DENY_ITEM_FLAG) || region.getFlags().containsKey(PluginRegionBlacklist.ALLOW_ITEM_FLAG)))
					{
						regions.add(new RegionItems(player.getWorld(), region));
					}
				}

				// check for regions the player has left so to return items
				Iterator<RegionItems> reg = regions.iterator();
				while (reg.hasNext())
				{
					RegionItems region = reg.next();
					Iterator<ProtectedRegion> oo = set.iterator();
					boolean found = false;
					while (it.hasNext())
					{
						ProtectedRegion r = oo.next();
						if (region.equals(r))
						{
							found = true;
							break;
						}
					}
					if (!found || region.isEmpty())
					{
						region.returnItems();
						if (region.isEmpty())
							it.remove();
					}
				}
			}
			else
			{
				clearPlayer(player);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent evt)
	{
		clearPlayer(evt.getPlayer().getName());
	}
	
	@EventHandler
	public void onKicked(PlayerKickEvent evt)
	{
		clearPlayer(evt.getPlayer().getName());
	}

	/** Released all items to the player and removes data stored for the player */
	public void clearPlayer(Player player)
	{
		clearPlayer(player.getName());
	}

	/** Released all items to the player and removes data stored for the player */
	public void clearPlayer(String playerName)
	{
		if (playerItemsPerRegion.containsKey(playerName))
		{
			List<RegionItems> regions = playerItemsPerRegion.get(playerName);
			if (regions != null && !regions.isEmpty())
			{
				Iterator<RegionItems> it = regions.iterator();
				while (it.hasNext())
				{
					RegionItems region = it.next();
					region.returnItems();
					if (region.isEmpty())
					{
						it.remove();
					}
				}
			}
		}
	}
}
