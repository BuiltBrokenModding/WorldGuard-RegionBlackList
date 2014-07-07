package com.builtbroken.region.blacklist;

import java.util.HashMap;
import java.util.LinkedHashMap;

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

public class PlayerEventHandler implements Listener
{
	private static int CHANGE_IN_DISTANCE = 10;
	private static int SECONDS_BETWEEN_UPDATES = 10;
	private static long MILLS_BETWEEN_UPDATES = SECONDS_BETWEEN_UPDATES * 1000;

	PluginRegionBlacklist plugin;

	private HashMap<String, Location> lastPlayerUpdateLocation = new LinkedHashMap<String, Location>();
	private HashMap<String, Long> lastPlayerUpateTime = new LinkedHashMap<String, Long>();

	private HashMap<String, IBlackListRegion> regionSupportListeners = new HashMap<String, IBlackListRegion>();

	public PlayerEventHandler(PluginRegionBlacklist plugin)
	{
		this.plugin = plugin;
	}

	/** Registers a new support class */
	public void register(IBlackListRegion support)
	{
		String name = support.getName();
		if (!regionSupportListeners.containsKey(name))
		{
			regionSupportListeners.put(name, support);
		}
	}

	/** Updates item data for player */
	public void update(Player player)
	{
		if (player != null)
		{
			Location lastLocation = getLastLocation(player);
			Location loc = player.getLocation();
			boolean distance_flag = lastLocation == null || lastLocation.distance(loc) >= CHANGE_IN_DISTANCE;
			boolean time_flag = !lastPlayerUpateTime.containsKey(player.getName()) || System.currentTimeMillis() - lastPlayerUpateTime.get(player.getName()) >= MILLS_BETWEEN_UPDATES;

			if (distance_flag || time_flag)
			{
				this.lastPlayerUpdateLocation.put(player.getName(), loc.clone());
				this.lastPlayerUpateTime.put(player.getName(), System.currentTimeMillis());
				for (IBlackListRegion support : regionSupportListeners.values())
					support.update(player, loc);
			}
		}
	}

	/** Asks each supporting class to unload the player */
	public void unload(Player player)
	{
		for (IBlackListRegion support : regionSupportListeners.values())
			support.unload(player);
	}

	/** Player's last location when we ran an update */
	public Location getLastLocation(Player player)
	{
		if (lastPlayerUpdateLocation.containsKey(player.getName()))
		{
			return lastPlayerUpdateLocation.get(player.getName());
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
			unload((Player) event.getEntity());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		unload(event.getPlayer());
	}

	@EventHandler
	public void onKicked(PlayerKickEvent evt)
	{
		unload(evt.getPlayer());
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

	// @EventHandler
	public void onPickUpItem(PlayerPickupItemEvent event)
	{
		// TODO if item is banned send strait to item cache
	}
}
