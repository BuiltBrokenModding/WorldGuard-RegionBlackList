package com.builtbroken.region.blacklist;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Wrapper to store items taken from a player to the region that took them
 * 
 * @author robert
 * 
 */
public class RegionItems
{
	final String regionName;
	final World world;
	final List<ItemStack> heldItems;

	public RegionItems(World world, String regionName)
	{
		this.world = world;
		this.regionName = regionName;
		heldItems = new LinkedList<ItemStack>();
	}

	public RegionItems(World world, ProtectedRegion region)
	{
		this(world, region.getId());
	}

	/** Removes all banned items from the player */
	public void removeItems(String user)
	{
		removeItems(getPlayer(user));
	}

	/** Returns all banned items from the player */
	public void returnItems(String user)
	{
		returnItems(getPlayer(user));
	}

	/** Removes all banned items from the player */
	public void removeItems(Player player)
	{
	}

	/** Returns all banned items from the player */
	public void returnItems(Player player)
	{
		if (player != null)
		{
			Iterator<ItemStack> it = heldItems.iterator();
			while (it.hasNext())
			{
				ItemStack stack = it.next();
				player.getInventory().addItem(stack);
			}
		}
	}

	public ProtectedRegion getRegion()
	{
		WorldGuardPlugin guard = PluginRegionBlacklist.instance().getWorldGuard();
		if (guard != null)
		{
			RegionManager manager = guard.getRegionManager(world);
			if (manager != null)
			{
				return manager.getRegion(this.regionName);
			}
		}
		return null;
	}

	public Player getPlayer(String user)
	{
		return PluginRegionBlacklist.instance().getServer().getPlayer(user);
	}

	public boolean isEmpty()
	{
		return heldItems.isEmpty();
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof String)
		{
			return ((String) object).equalsIgnoreCase(this.regionName);
		}
		else if (object instanceof ProtectedRegion)
		{
			return ((ProtectedRegion) object).getId().equalsIgnoreCase(this.regionName);
		}
		return super.equals(object);
	}
}
