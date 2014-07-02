package com.builtbroken.region.blacklist.worldguard;

import java.util.List;

import org.bukkit.World;

import com.builtbroken.region.blacklist.ItemData;
import com.builtbroken.region.blacklist.PlayerAreaItems;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Wrapper to store items taken from a player to the region that took them
 * 
 * @author robert
 * 
 */
public class RegionItems extends PlayerAreaItems
{
	public RegionItems(World world, String regionName)
	{
		super(world, regionName);
	}

	public RegionItems(World world, ProtectedRegion region)
	{
		this(world, region.getId());
	}

	@Override
	public boolean denyInventory()
	{
		ProtectedRegion region = getRegion();
		if (region != null)
		{
			if (region.getFlags().containsKey(WorldGuardSupport.ALLOW_ITEM_FLAG))
			{
				return false;
			}
		}
		return true;
	}

	/** Gets the data used to block items */
	@Override
	@SuppressWarnings("unchecked")
	public List<ItemData> getInventoryData()
	{
		ProtectedRegion region = getRegion();
		if (region != null)
		{
			if (region.getFlags().containsKey(WorldGuardSupport.ALLOW_ITEM_FLAG))
			{
				return (List<ItemData>) region.getFlags().get(WorldGuardSupport.ALLOW_ITEM_FLAG);
			}
			else if (region.getFlags().containsKey(WorldGuardSupport.DENY_ITEM_FLAG))
			{
				return (List<ItemData>) region.getFlags().get(WorldGuardSupport.DENY_ITEM_FLAG);
			}
		}
		return null;
	}

	public ProtectedRegion getRegion()
	{
		WorldGuardPlugin guard = WGUtility.worldGuard();
		if (guard != null)
		{
			RegionManager manager = guard.getRegionManager(world);
			if (manager != null)
			{
				return manager.getRegion(areaName);
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof ProtectedRegion)
		{
			return ((ProtectedRegion) object).getId().equalsIgnoreCase(areaName);
		}
		return super.equals(object);
	}

	@Override
	public List<ItemData> getArmorData()
	{
		ProtectedRegion region = getRegion();
		if (region != null)
		{
			if (region.getFlags().containsKey(WorldGuardSupport.ALLOW_ARMOR_FLAG))
			{
				return (List<ItemData>) region.getFlags().get(WorldGuardSupport.ALLOW_ARMOR_FLAG);
			}
			else if (region.getFlags().containsKey(WorldGuardSupport.DENY_ARMOR_FLAG))
			{
				return (List<ItemData>) region.getFlags().get(WorldGuardSupport.DENY_ARMOR_FLAG);
			}
		}
		return null;
	}

	@Override
	public boolean denyArmor()
	{
		ProtectedRegion region = getRegion();
		if (region != null)
		{
			if (region.getFlags().containsKey(WorldGuardSupport.ALLOW_ARMOR_FLAG))
			{
				return false;
			}
		}
		return false;
	}
}
