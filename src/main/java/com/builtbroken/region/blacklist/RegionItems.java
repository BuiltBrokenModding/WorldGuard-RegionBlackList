package com.builtbroken.region.blacklist;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.World;
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

	public void removeItems()
	{

	}

	public void returnItems()
	{

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
