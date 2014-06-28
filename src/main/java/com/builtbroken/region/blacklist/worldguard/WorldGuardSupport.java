package com.builtbroken.region.blacklist.worldguard;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Class that handles all world guard support for this mod
 * 
 * @author Robert Seifert
 * 
 */
public class WorldGuardSupport implements Listener
{
	private static int CHANGE_IN_DISTANCE = 5;
	private static int SECONDS_BETWEEN_UPDATES = 10;
	private static long MILLS_BETWEEN_UPDATES = SECONDS_BETWEEN_UPDATES * 1000;

	public static final ItemFlag ALLOW_ITEM_FLAG = new ItemFlag("allow-items");
	public static final ItemFlag DENY_ITEM_FLAG = new ItemFlag("deny-items");

	private HashMap<String, RegionList> playerItemsPerRegion = new LinkedHashMap<String, RegionList>();
	private HashMap<String, Vector> playerLocation = new LinkedHashMap<String, Vector>();
	private HashMap<String, Long> playerTime = new LinkedHashMap<String, Long>();

	public WorldGuardSupport()
	{
		WGUtility.customFlags().addCustomFlag(ALLOW_ITEM_FLAG);
		WGUtility.customFlags().addCustomFlag(DENY_ITEM_FLAG);
	}

	/** Updated item data for player */
	public void update(Player player)
	{
		if (player != null)
		{
			Vector lastLocation = getLastLocation(player);
			Location loc = player.getLocation();
			Vector vec = new Vector(loc.getX(), loc.getY(), loc.getZ());
			boolean distance_flag = lastLocation == null || lastLocation.distance(vec) >= CHANGE_IN_DISTANCE;
			boolean time_flag = !playerTime.containsKey(player.getName()) || System.currentTimeMillis() - playerTime.get(player.getName()) >= MILLS_BETWEEN_UPDATES;

			if (distance_flag || time_flag)
			{
				updatePlayerRegions(player, getRegionItems(player), WGUtility.getRegionsWithFlags(player.getWorld(), vec, DENY_ITEM_FLAG, ALLOW_ITEM_FLAG));
				this.playerLocation.put(player.getName(), new Vector(vec));
				this.playerTime.put(player.getName(), System.currentTimeMillis());
			}
		}
	}

	public void updatePlayerRegions(Player player, RegionList regions, List<ProtectedRegion> regionList)
	{
		if (regionList != null)
		{
			if (regions == null)
				regions = new RegionList(player);

			List<String> regionNames = regions.asNames();

			// Go threw all regions the player is in looking for new ones and checking current
			for (ProtectedRegion region : regionList)
			{
				if (regionNames.contains(region.getId()))
				{
					regionNames.remove(region.getId());
				}
				else
				{
					RegionItems item = new RegionItems(player.getWorld(), region);
					item.removeItems(player);
					regions.add(item);
				}
			}

			// Remove regions not found
			for (String regionName : regionNames)
			{
				RegionItems item = regions.getRegion(regionName);
				if (item != null)
				{
					item.returnItems(player);
					if (item.isEmpty())
						regions.remove(item);
				}
			}

			if (regions != null && !regions.isEmpty())
				this.playerItemsPerRegion.put(player.getName(), regions);
		}
		else
		{
			clearPlayer(player);
		}
	}

	/** Regions linked to the player with items that belong to the player */
	public RegionList getRegionItems(Player player)
	{
		RegionList list = null;
		if (playerItemsPerRegion.containsKey(player.getName()) && playerItemsPerRegion.get(player.getName()) != null)
		{
			list = playerItemsPerRegion.get(player.getName());
		}
		else
		{
			list = new RegionList(player);
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

	/********************************
	 * Events that dump the player's data
	 ********************************/

	@EventHandler
	public void onDeath(EntityDeathEvent event)
	{
		if (event.getEntity() instanceof Player)
			clearPlayer((Player) event.getEntity());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		clearPlayer(event.getPlayer());
	}

	@EventHandler
	public void onKicked(PlayerKickEvent evt)
	{
		clearPlayer(evt.getPlayer());
	}

	public void clearPlayer(Player player)
	{
		RegionList list = this.getRegionItems(player);
		if (list != null)
			list.clearPlayer();
	}

	/********************************
	 * Events that trigger updates
	 ********************************/

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event)
	{
		update(event.getPlayer());
	}

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

	//@EventHandler
	public void onPickUpItem(PlayerPickupItemEvent event)
	{
		//TODO if item is banned send strait to item cache
	}

}
