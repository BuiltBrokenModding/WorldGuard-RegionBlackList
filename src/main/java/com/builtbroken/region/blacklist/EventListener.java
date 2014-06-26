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
			LinkedList<RegionItems> regions = new LinkedList<RegionItems>();
			if (playerItemsPerRegion.containsKey(player.getName()))
			{
				regions = playerItemsPerRegion.get(player.getName());
			}

			if (manager != null)
			{
				System.out.println("Has Manager now checking for new regions");
				// Look for new regions that the player is now in
				ApplicableRegionSet set = manager.getApplicableRegions(vec);
				Iterator<ProtectedRegion> proIt = set.iterator();
				while (proIt.hasNext())
				{
					ProtectedRegion region = proIt.next();
					System.out.println("\tRegion: " + region.getId());
					if (!regions.contains(region) && (region.getFlags().containsKey(PluginRegionBlacklist.DENY_ITEM_FLAG) || region.getFlags().containsKey(PluginRegionBlacklist.ALLOW_ITEM_FLAG)))
					{
						System.out.println("\t  Not Found adding to list");
						regions.add(new RegionItems(player.getWorld(), region));
					}
				}

				// check for regions the player has left so to return items
				Iterator<RegionItems> regIt = regions.iterator();
				while (regIt.hasNext())
				{
					RegionItems itemRegion = regIt.next();
					Iterator<ProtectedRegion> oo = set.iterator();
					boolean found = false;
					while (oo.hasNext())
					{
						ProtectedRegion region = oo.next();
						// Compare region id
						if (itemRegion.equals(region))
						{
							// check to make sure the region still has either flag
							if (region.getFlags().containsKey(PluginRegionBlacklist.DENY_ITEM_FLAG) || region.getFlags().containsKey(PluginRegionBlacklist.ALLOW_ITEM_FLAG))
							{
								found = true;
							}
							break;
						}
					}
					if(found)
					{
						itemRegion.removeItems(player);
					}
					// If not found or empty clear region
					if (!found || itemRegion.isEmpty())
					{
						itemRegion.returnItems(player);
						if (itemRegion.isEmpty())
							regIt.remove();
					}
				}
			}
			else
			{
				System.out.println("No Manger");
				clearPlayer(player);
			}
			if (regions != null && !regions.isEmpty())
				this.playerItemsPerRegion.put(player.getName(), regions);
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
					region.returnItems(playerName);
					if (region.isEmpty())
					{
						it.remove();
					}
				}
			}
		}
	}
}
