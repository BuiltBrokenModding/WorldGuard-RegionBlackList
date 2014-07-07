package com.builtbroken.region.blacklist;

import java.util.ArrayList;
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

	public ItemList(List<ItemData> list, boolean  deny)
	{
		super(list);
		this.denyItems = deny;
	}

	public boolean denyItems()
	{
		return denyItems;
	}

}
