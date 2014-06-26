package com.builtbroken.region.blacklist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

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
		System.out.println("\t\tRemoving items from player");
		if (player != null)
		{
			player.sendMessage("Stripping items");
			List<ItemData> list = getData();
			for (ItemData data : list)
			{
				System.out.println("\t\t\tData: " + data.stack());
				HashMap<Integer, ? extends ItemStack> slotStacks = player.getInventory().all(data.stack().getTypeId());
				for (Entry<Integer, ? extends ItemStack> entry : slotStacks.entrySet())
				{
					System.out.println("\t\t\tPlayerItem: " + entry.getValue() + " Slot: " + entry.getKey());
					if (data.allMeta() || entry.getValue().getItemMeta() == data.stack().getItemMeta())
					{
						this.heldItems.add(entry.getValue());
						player.getInventory().setItem(entry.getKey(), null);
					}
				}
			}
		}
	}

	/** Returns all banned items from the player */
	public void returnItems(Player player)
	{
		if (player != null)
		{
			player.sendMessage("Returning items");
			Iterator<ItemStack> it = heldItems.iterator();
			while (it.hasNext())
			{
				ItemStack stack = it.next();
				player.getInventory().addItem(stack);
				it.remove();
			}
		}
	}

	public boolean denyList()
	{
		ProtectedRegion region = getRegion();
		if (region != null)
		{
			if (region.getFlags().containsKey(PluginRegionBlacklist.ALLOW_ITEM_FLAG))
			{
				return false;
			}
		}
		return true;
	}

	public List<ItemData> getData()
	{
		ProtectedRegion region = getRegion();
		if (region != null)
		{
			if (region.getFlags().containsKey(PluginRegionBlacklist.ALLOW_ITEM_FLAG))
			{
				return (List<ItemData>) region.getFlags().get(PluginRegionBlacklist.ALLOW_ITEM_FLAG);
			}
			else if (region.getFlags().containsKey(PluginRegionBlacklist.DENY_ITEM_FLAG))
			{
				return (List<ItemData>) region.getFlags().get(PluginRegionBlacklist.DENY_ITEM_FLAG);
			}
		}
		return null;
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
