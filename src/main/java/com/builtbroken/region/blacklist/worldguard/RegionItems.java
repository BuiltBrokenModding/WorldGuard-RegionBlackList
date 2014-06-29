package com.builtbroken.region.blacklist.worldguard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.builtbroken.region.blacklist.ItemData;
import com.builtbroken.region.blacklist.PluginRegionBlacklist;
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
	public boolean removeItems(String user)
	{
		return removeItems(getPlayer(user));
	}

	/** Returns all banned items from the player */
	public boolean returnItems(String user)
	{
		return returnItems(getPlayer(user));
	}

	/** Removes all banned items from the player */
	public boolean removeItems(Player player)
	{
		boolean taken_flag = false;
		if (player != null)
		{
			boolean denyList = denyList();
			List<ItemData> list = getData();
			for (ItemData data : list)
			{
				for (int slot = 0; slot < player.getInventory().getContents().length; slot++)
				{
					ItemStack stack = player.getInventory().getContents()[slot];
					if (stack != null)
					{
						boolean match = stack.getTypeId() == data.stack().getTypeId() && (data.allMeta() || stack.getItemMeta() == data.stack().getItemMeta());

						if (match)
						{
							if (denyList)
							{
								this.heldItems.add(stack);
								player.getInventory().setItem(slot, null);
								taken_flag = true;
							}
						}
						else if (!denyList)
						{
							this.heldItems.add(stack);
							player.getInventory().setItem(slot, null);
							taken_flag = true;
						}
					}
				}
			}
		}
		return taken_flag;
	}

	/** Returns all banned items from the player */
	public boolean returnItems(Player player)
	{
		boolean return_flag = false;
		if (player != null && !this.isEmpty())
		{
			Iterator<ItemStack> it = heldItems.iterator();
			while (it.hasNext())
			{
				ItemStack stack = it.next();
				player.getInventory().addItem(stack);
				it.remove();
				return_flag = true;
			}
		}
		return return_flag;
	}

	public boolean denyList()
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

	@SuppressWarnings("unchecked")
	public List<ItemData> getData()
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
				return manager.getRegion(this.regionName);
			}
		}
		return null;
	}

	public Player getPlayer(String user)
	{
		return Bukkit.getPlayer(user);
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