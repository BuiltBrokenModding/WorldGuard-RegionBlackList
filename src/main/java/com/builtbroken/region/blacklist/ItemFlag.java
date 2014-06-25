package com.builtbroken.region.blacklist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.mewin.WGCustomFlags.flags.CustomFlag;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;

public class ItemFlag extends CustomFlag<List<ItemData>>
{

	public ItemFlag(String name)
	{
		super(name);
	}

	public ItemFlag(String name, RegionGroup group)
	{
		super(name, group);
	}

	@Override
	public List<ItemData> loadFromDb(String arg0)
	{
		List<ItemData> itemList = new ArrayList<ItemData>();
		String[] stacks = arg0.split(",");
		for (String stack : stacks)
		{
			String[] data = stack.split(":");
			int id = Integer.parseInt(data[0]);
			short meta = 0;
			boolean allMeta = false;
			if (data.length > 1)
			{
				if (data[1].equalsIgnoreCase("all"))
				{
					allMeta = true;
				}
				else
				{
					meta = Short.parseShort(data[1]);
				}
			}
			else
			{
				allMeta = true;
			}
			itemList.add(new ItemData(new ItemStack(id, 1, meta), allMeta));
		}
		return itemList;
	}

	@Override
	public String saveToDb(List<ItemData> list)
	{
		String save = "";
		Iterator<ItemData> it = list.iterator();
		while (it.hasNext())
		{
			ItemData data = it.next();
			save += data.stack().getTypeId();
			save += ":";
			if (data.allMeta())
			{
				save += "all";
			}
			else
			{
				save += data.stack().getItemMeta();
			}
			if (it.hasNext())
			{
				save += ",";
			}
		}
		return save;
	}

	@Override
	public List<ItemData> parseInput(WorldGuardPlugin arg0, CommandSender arg1, String arg2) throws InvalidFlagFormat
	{
		try
		{
			return loadFromDb(arg2);
		}
		catch (NumberFormatException e)
		{
			throw new InvalidFlagFormat("Invalid parse for itemstack");
		}
	}

	@Override
	public List<ItemData> unmarshal(Object arg0)
	{
		return (List<ItemData>) arg0;
	}

	@Override
	public Object marshal(List<ItemData> stack)
	{
		return saveToDb(stack);
	}

	@Override
	public String toString()
	{
		return "ItemFlag";
	}

}
