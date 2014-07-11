package com.builtbroken.region.blacklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
			List<Integer> list = null;
			if (map.containsKey(data.stack().getTypeId()))
			{
				list = map.get(data.stack().getTypeId());
			}
			else
			{
				list = new ArrayList<Integer>();
			}
			if (!list.contains(data.stack().getDurability()) && !list.contains(-1))
			{
				if (!data.allMeta())
					list.add((int) data.stack().getDurability());
				else
					list.add(-1);
				map.put(data.stack().getTypeId(), list);
			}
		}
		return map;
	}

}
