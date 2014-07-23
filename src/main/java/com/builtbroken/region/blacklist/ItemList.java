package com.builtbroken.region.blacklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class ItemList extends ArrayList<ItemData>
{
	private static final long serialVersionUID = -2774268914313235522L;
	private boolean denyItems = false;

	public ItemList()
	{

	}

	public ItemList(boolean deny)
	{
		this.denyItems = deny;
	}

	public ItemList(List<ItemData> list, boolean deny)
	{
		super(list);
		this.denyItems = deny;
	}

	public boolean denyItems()
	{
		return denyItems;
	}

	/** Gets a map of that data as item ids, and meta list */
	public HashMap<Integer, List<Integer>> toMap()
	{
		HashMap<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		for (ItemData data : this)
		{
			List<Integer> meta_list = null;
			int id = data.stack().getTypeId();
			short meta = data.stack().getDurability();

			if (map.containsKey(id))
				meta_list = map.get(id);
			else
				meta_list = new ArrayList<Integer>();

			if (!meta_list.contains(meta) && !meta_list.contains(-1))
			{
				if (!data.allMeta())
					meta_list.add((int) meta);
				else
					meta_list.add(-1);
			}
			map.put(id, meta_list);
		}
		return map;
	}

	@Override
	public boolean contains(Object object)
	{
		for (ItemData data : this)
		{
			if (object instanceof ItemData)
			{
				if (data.stack().getTypeId() == ((ItemData) object).stack().getTypeId())
				{
					if (data.allMeta() || data.stack().getDurability() == ((ItemData) object).stack().getDurability())
					{
						return true;
					}
				}
			}
			else if (object instanceof ItemStack)
			{
				if (data.stack().getTypeId() == ((ItemStack) object).getTypeId())
				{
					if (data.allMeta() || data.stack().getDurability() == ((ItemStack) object).getDurability())
					{
						return true;
					}
				}
			}
		}
		return super.contains(object);
	}

}
