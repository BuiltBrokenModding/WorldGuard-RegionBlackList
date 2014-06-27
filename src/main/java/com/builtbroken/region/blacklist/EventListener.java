package com.builtbroken.region.blacklist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
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
	private HashMap<String, Vector> playerLocation = new LinkedHashMap<String, Vector>();
	private HashMap<String, LinkedList<String>> playerRegions = new LinkedHashMap<String, LinkedList<String>>();

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
		if (player != null)
		{
			Vector lastLocation = getLastLocation(player);
			Location loc = player.getLocation();
			Vector vec = new Vector(loc.getX(), loc.getY(), loc.getZ());

			if (lastLocation == null || lastLocation.distance(vec) >= 2)
			{				
				updatePlayerRegions(player, getRegionItems(player), getRegionsWithFlag(player.getWorld(), vec));				
			}
			this.playerLocation.put(player.getName(), new Vector(vec));
		}
	}

	public void updatePlayerRegions(Player player, LinkedList<RegionItems> regions, List<ProtectedRegion> regionList)
	{
		if (regionList != null)
		{
			if (regions == null)
				regions = new LinkedList<RegionItems>();
			
			
			
			if (regions != null && !regions.isEmpty())
				this.playerItemsPerRegion.put(player.getName(), regions);
		}
		else
		{
			clearPlayer(player);
		}
	}

	/** Regions linked to the player with items that belong to the player */
	public LinkedList<RegionItems> getRegionItems(Player player)
	{
		LinkedList<RegionItems> list = new LinkedList<RegionItems>();
		if (playerItemsPerRegion.containsKey(player.getName()) && playerItemsPerRegion.get(player.getName()) != null)
		{
			list = playerItemsPerRegion.get(player.getName());
		}
		return list;
	}

	/** Player's last location when we ran an update */
	public Vector getLastLocation(Player player)
	{
		if (playerLocation.containsKey(player.getName()))
		{
			return playerLocation.get(player.getName());
		}
		return null;
	}

	public ApplicableRegionSet getRegions(World world, Vector vec)
	{
		WorldGuardPlugin guard = PluginRegionBlacklist.instance().getWorldGuard();
		if (guard != null)
		{
			RegionManager manager = guard.getRegionManager(world);

			if (manager != null)
			{
				return manager.getApplicableRegions(vec);
			}
		}
		return null;
	}

	/** Gets all regions that have the flag allow items or deny items */
	public List<ProtectedRegion> getRegionsWithFlag(World world, Vector vec)
	{
		ApplicableRegionSet set = getRegions(world, vec);
		if (set != null)
			return getRegionsWithFlag(set);
		return null;
	}

	/** Gets all regions that have the flag allow items or deny items */
	public List<ProtectedRegion> getRegionsWithFlag(ApplicableRegionSet set)
	{
		List<ProtectedRegion> list = new LinkedList<ProtectedRegion>();
		if (set != null)
		{
			Iterator<ProtectedRegion> iterator = set.iterator();
			while (iterator.hasNext())
			{
				ProtectedRegion region = iterator.next();
				if ((region.getFlags().containsKey(PluginRegionBlacklist.DENY_ITEM_FLAG) || region.getFlags().containsKey(PluginRegionBlacklist.ALLOW_ITEM_FLAG)))
				{
					list.add(region);
				}
			}
		}
		return list;
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
