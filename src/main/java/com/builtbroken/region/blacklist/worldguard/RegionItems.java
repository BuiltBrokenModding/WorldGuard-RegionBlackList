package com.builtbroken.region.blacklist.worldguard;

import java.util.List;

import org.bukkit.World;

import com.builtbroken.region.blacklist.ItemData;
import com.builtbroken.region.blacklist.ItemList;
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
	public RegionItems()
	{
		super();
	}
	
	public RegionItems(World world, String regionName)
	{
		super(world, regionName);
	}

	public RegionItems(World world, ProtectedRegion region)
	{
		this(world, region.getId());
	}

	/** Gets the data used to block items */
	@Override
	@SuppressWarnings("unchecked")
	public ItemList getInventoryData()
	{
		ProtectedRegion region = getRegion();
		if (region != null)
		{
			if (region.getFlags().containsKey(WorldGuardSupport.ALLOW_ITEM_FLAG))
			{
				return new ItemList(((List<ItemData>) region.getFlags().get(WorldGuardSupport.ALLOW_ITEM_FLAG)), false);
			}
			else if (region.getFlags().containsKey(WorldGuardSupport.DENY_ITEM_FLAG))
			{
				return new ItemList(((List<ItemData>) region.getFlags().get(WorldGuardSupport.DENY_ITEM_FLAG)), true);
			}
		}
		return null;
	}
	
	@Override
	public ItemList getArmorData()
	{
		ProtectedRegion region = getRegion();
		if (region != null)
		{
			if (region.getFlags().containsKey(WorldGuardSupport.ALLOW_ARMOR_FLAG))
			{
				return new ItemList(((List<ItemData>) region.getFlags().get(WorldGuardSupport.ALLOW_ARMOR_FLAG)), false);
			}
			else if (region.getFlags().containsKey(WorldGuardSupport.DENY_ARMOR_FLAG))
			{
				return new ItemList(((List<ItemData>) region.getFlags().get(WorldGuardSupport.DENY_ARMOR_FLAG)), true);
			}
		}
		return null;
	}

	public ProtectedRegion getRegion()
	{
		return WGUtility.getRegion(world, getAreaName());
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

	
}
