package com.builtbroken.region.blacklist;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Area in which the player's items have been taken and are stored in this class.
 * 
 * @author Robert Seifert
 * 
 */
public abstract class PlayerAreaItems implements Serializable
{
	private static final long serialVersionUID = 2694201623625810072L;

	protected final String areaName;

	protected final World world;

	protected final List<ItemWrapper> inventory;
	protected final List<ItemWrapper> armor;

	public PlayerAreaItems(World world, String name)
	{
		this.world = world;
		this.areaName = name;
		inventory = new ArrayList<ItemWrapper>();
		armor = new ArrayList<ItemWrapper>();
	}

	/** Data used to restrict the items in the player's inventory */
	public abstract ItemList getInventoryData();

	/** Data used to restrict the items in the player's armor set */
	public abstract ItemList getArmorData();

	public void debug(int type, String msg)
	{
		System.out.println("PlayerAreaItems: " + msg);
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

		// Take items from player's inventory
		if (!player.hasPermission("reginv.inventory.keep"))
		{
			debug(0, "Removing Items");
			if (removeItems(player, getInventoryData(), true))
				taken_flag = true;
		}
		// Take items from player's armor slots
		if (!player.hasPermission("reginv.armor.keep"))
		{
			debug(0, "Removing Armor");
			if (removeArmor(player, getArmorData()))
				taken_flag = true;
		}

		return taken_flag;
	}

	/**
	 * Takes all items from a players inventory based on the data points given
	 * 
	 * @param player - player who contains the inventory to search
	 * @param data - List of ItemData used to compare if an ItemStack matches
	 * @param denyList - deny items, true will use data as a banlist, false will use data as an
	 * allow only list
	 * @return true if any items were removed
	 */
	public boolean removeItems(Player player, ItemList list, boolean logSlot)
	{
		boolean taken_flag = false;
		if (list != null && !list.isEmpty())
		{
			HashMap<Integer, List<Integer>> dataList = list.toMap();
			if (removeItems(player, dataList, list.denyItems(), logSlot))
				taken_flag = true;
		}
		return taken_flag;
	}

	/**
	 * Takes all items from a players inventory based on the data point
	 * 
	 * @param player - player who contains the inventory to search
	 * @param data - ItemData used to compare if an ItemStack matches
	 * @param denyList - deny items, true will use data as a banlist, false will use data as an
	 * allow only list
	 * @return true if any items were removed
	 */
	public boolean removeItems(Player player, HashMap<Integer, List<Integer>> data, boolean denyList, boolean logSlot)
	{
		boolean taken_flag = false;
		for (int slot = 0; slot < player.getInventory().getContents().length; slot++)
		{
			ItemStack stack = player.getInventory().getContents()[slot];
			if (stack != null)
			{
				debug(0, "\t\tStack: " + stack + "  Slot: " + slot);
				boolean id_flag = data.containsKey(stack.getType());
				boolean meta_flag = id_flag && data.get(stack.getType()) != null ? data.get(stack.getType()).contains(-1) || data.get(stack.getType()).contains(stack.getDurability()) : false;
				boolean match = id_flag && meta_flag;
				int slotPlace = logSlot ? slot : -1;
				if (match)
				{
					debug(0, "\t\tMatches");
					if (denyList)
					{
						debug(0, "\t\tDenying Item");
						this.inventory.add(new ItemWrapper(stack, slotPlace));
						player.getInventory().setItem(slot, null);
						taken_flag = true;
					}
				}
				else if (!denyList)
				{
					debug(0, "\t\tDisallowing Item");
					this.inventory.add(new ItemWrapper(stack, slotPlace));
					player.getInventory().setItem(slot, null);
					taken_flag = true;
				}
			}
		}
		return taken_flag;
	}

	/**
	 * Removes armor from the player using a list of data points
	 * 
	 * @param player - player
	 * @param data - List of ItemData used to compare if an ItemStack matches
	 * @return true if any items were removed
	 */
	public boolean removeArmor(Player player, ItemList list)
	{
		boolean taken_flag = false;

		if (list != null && !list.isEmpty() && player.getInventory().getArmorContents() != null)
		{
			for (ItemData data : list)
			{
				if (removeArmor(player, data, list.denyItems()))
					taken_flag = true;
			}
		}

		return taken_flag;
	}

	/**
	 * Removes armor from the player using a single data point
	 * 
	 * @param player - player
	 * @param data - ItemData used to compare if an ItemStack matches
	 * @param denyList - deny items, true will use data as a banlist, false will use data as an
	 * allow only list
	 * @return true if any items were removed
	 */
	public boolean removeArmor(Player player, ItemData data, boolean denyList)
	{
		boolean taken_flag = false;
		ItemStack[] armorContent = player.getInventory().getArmorContents();
		for (int slot = 0; slot < armorContent.length; slot++)
		{
			// System.out.println("Armor slot: " + slot + "  item: " + armorContent[slot]);
			ItemStack stack = armorContent[slot];
			if (stack != null)
			{
				boolean match = data.isMatch(stack);

				if (match)
				{
					// System.out.println("Armor is a match");
					if (denyList)
					{
						// System.out.println("Denying armor");
						this.armor.add(new ItemWrapper(stack, slot));
						armorContent[slot] = null;
						taken_flag = true;
					}
				}
				else if (!denyList)
				{
					// System.out.println("Armor not allowed");
					this.armor.add(new ItemWrapper(stack, slot));
					armorContent[slot] = null;
					taken_flag = true;
				}
			}
		}
		player.getInventory().setArmorContents(armorContent);
		return taken_flag;
	}

	/** Returns all banned items from the player */
	public boolean returnItems(Player player)
	{
		boolean return_flag = false;
		if (player != null && !this.isEmpty())
		{
			Iterator<ItemWrapper> it = inventory.iterator();
			while (it.hasNext())
			{
				ItemWrapper item = it.next();
				if (item.returnItem(player))
				{
					it.remove();
				}
			}

			// Return armor items
			it = armor.iterator();
			ItemStack[] armorContent = player.getInventory().getArmorContents();
			while (it.hasNext())
			{
				ItemWrapper item = it.next();
				if (item.returnArmor(player, armorContent))
				{
					it.remove();
				}
			}
			player.getInventory().setArmorContents(armorContent);
		}
		return return_flag;
	}

	public Player getPlayer(String user)
	{
		return Bukkit.getPlayer(user);
	}

	/** Is the area's item storage empty */
	public boolean isEmpty()
	{
		return inventory.isEmpty() && armor.isEmpty();
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof String)
		{
			return ((String) object).equalsIgnoreCase(this.areaName);
		}
		return super.equals(object);
	}

	/** Name of the area */
	public String getAreaName()
	{
		return this.areaName;
	}
}
