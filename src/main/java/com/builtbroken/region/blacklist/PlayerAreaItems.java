package com.builtbroken.region.blacklist;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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

	protected final HashMap<Integer, ItemStack> inventory;
	protected final HashMap<Integer, ItemStack> armor;

	public PlayerAreaItems(World world, String name)
	{
		this.world = world;
		this.areaName = name;
		inventory = new LinkedHashMap<Integer, ItemStack>();
		armor = new LinkedHashMap<Integer, ItemStack>();
	}

	/** Data used to restrict the items in the player's inventory */
	public abstract List<ItemData> getInventoryData();

	/** Data used to restrict the items in the player's armor set */
	public abstract List<ItemData> getArmorData();

	/** Is our inventory data a deny list */
	public abstract boolean denyInventory();

	/** Is our inventory data a deny list */
	public abstract boolean denyArmor();

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
			if (removeItems(player, getInventoryData(), denyInventory(), true))
				taken_flag = true;
		}
		// Take items from player's armor slots
		if (!player.hasPermission("reginv.armor.keep"))
		{
			if (removeArmor(player, getArmorData(), denyArmor()))
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
	public boolean removeItems(Player player, List<ItemData> list, boolean denyList, boolean logSlot)
	{
		boolean taken_flag = false;
		if (list != null && !list.isEmpty())
		{
			for (ItemData data : list)
			{
				if (removeItems(player, data, denyList, logSlot))
					taken_flag = true;
			}
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
	public boolean removeItems(Player player, ItemData data, boolean denyList, boolean logSlot)
	{
		boolean taken_flag = false;
		for (int slot = 0; slot < player.getInventory().getContents().length; slot++)
		{
			ItemStack stack = player.getInventory().getContents()[slot];
			if (stack != null)
			{
				boolean match = data.isMatch(stack);

				if (match)
				{
					if (denyList)
					{
						this.inventory.put(logSlot ? slot : -1, stack);
						player.getInventory().setItem(slot, null);
						taken_flag = true;
					}
				}
				else if (!denyList)
				{
					this.inventory.put(logSlot ? slot : -1, stack);
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
	 * @param denyList - deny items, true will use data as a banlist, false will use data as an
	 * allow only list
	 * @return true if any items were removed
	 */
	public boolean removeArmor(Player player, List<ItemData> list, boolean denyList)
	{
		boolean taken_flag = false;

		if (list != null && !list.isEmpty() && player.getInventory().getArmorContents() != null)
		{
			for (ItemData data : list)
			{
				if (removeArmor(player, data, denyList))
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
			System.out.println("Armor slot: " + slot + "  item: " + armorContent[slot]);
			ItemStack stack = armorContent[slot];
			if (stack != null)
			{
				boolean match = data.isMatch(stack);

				if (match)
				{
					System.out.println("Armor is a match");
					if (denyList)
					{
						System.out.println("Denying armor");
						this.armor.put(slot, stack);
						armorContent[slot] = null;
						taken_flag = true;
					}
				}
				else if (!denyList)
				{
					System.out.println("Armor not allowed");
					this.armor.put(slot, stack);
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
			HashMap<Integer, ItemStack> updateMap = new HashMap<Integer, ItemStack>();
			Iterator<Entry<Integer, ItemStack>> it = inventory.entrySet().iterator();
			while (it.hasNext())
			{
				boolean remove = false;
				Entry<Integer, ItemStack> entry = it.next();
				if (entry.getValue() != null)
				{
					ItemStack returned = returnItem(player, entry.getKey(), entry.getValue());
					if (returned == null || returned.getAmount() <= 0)
						return_flag = remove;
					else
						updateMap.put(entry.getKey(), returned);
				}
				it.remove();
			}
			if (!updateMap.isEmpty())
				inventory.putAll(updateMap);

			// Return armor items
			updateMap = new HashMap<Integer, ItemStack>();
			it = armor.entrySet().iterator();
			ItemStack[] armorContent = player.getInventory().getArmorContents();
			while (it.hasNext())
			{
				boolean remove = false;
				Entry<Integer, ItemStack> entry = it.next();
				if (entry.getValue() != null)
				{
					if (armorContent[entry.getKey()] == null || armorContent[entry.getKey()].getTypeId() == 0)
					{
						armorContent[entry.getKey()] = entry.getValue();
						return_flag = true;
					}
					else
					{
						ItemStack returned = returnItem(player, -1, entry.getValue());
						if (returned == null || returned.getAmount() <= 0)
							return_flag = remove;
						else
							updateMap.put(entry.getKey(), returned);
					}
				}
				it.remove();
			}
			player.getInventory().setArmorContents(armorContent);
			if (!updateMap.isEmpty())
				armor.putAll(updateMap);
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

	/** Returns a single item to the player's inventory */
	public static ItemStack returnItem(Player player, ItemStack stack)
	{
		return returnItem(player, -1, stack);
	}

	/** Returns a single item to the player's inventory */
	public static ItemStack returnItem(Player player, int slot, ItemStack stack)
	{
		if (player != null && stack != null)
		{
			if (slot >= 0 && player.getInventory().getItem(slot) == null)
			{
				player.getInventory().setItem(slot, stack);
				return null;
			}
			else
			{
				HashMap<Integer, ItemStack> re = player.getInventory().addItem(stack);
				if (re != null && !re.isEmpty())
				{
					for (Entry<Integer, ItemStack> entry : re.entrySet())
					{
						if (entry.getValue() != null)
							return entry.getValue();
					}
				}
				return null;
			}
		}
		return stack;
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
