package com.builtbroken.region.blacklist.worldguard;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class RegionList extends LinkedList<RegionItems>
{
	private static final long serialVersionUID = 3009886584617520185L;
	String playerName = null;

	public RegionList(Player player)
	{
		playerName = player.getName();
	}

	/** Player this list is connected too */
	public Player getPlayer()
	{
		return Bukkit.getPlayer(playerName);
	}

	/** Gets an item region by string name */
	public RegionItems getRegion(String name)
	{
		Iterator<RegionItems> it = this.iterator();
		while (it.hasNext())
		{
			RegionItems item = it.next();
			if (item.getAreaName().equalsIgnoreCase(name))
			{
				return item;
			}
		}
		return null;
	}

	/** Gets the list of regions items as names for sorting */
	public List<String> asNames()
	{
		List<String> regionNames = new LinkedList<String>();
		Iterator<RegionItems> it = this.iterator();

		while (it.hasNext())
		{
			RegionItems item = it.next();
			regionNames.add(item.getAreaName());
		}

		return regionNames;
	}

	/** Released all items to the player and removes data stored for the player */
	public void clearPlayer()
	{
		if (getPlayer() != null)
		{
			Iterator<RegionItems> it = this.iterator();
			while (it.hasNext())
			{
				RegionItems item = it.next();
				item.returnItems(getPlayer());
				if (item.isEmpty())
					it.remove();
			}
		}
	}
}
