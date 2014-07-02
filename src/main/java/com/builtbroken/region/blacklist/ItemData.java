package com.builtbroken.region.blacklist;

import org.bukkit.inventory.ItemStack;

/**
 * Wrapper class to store an itemstack and a bit of data for it
 * 
 * @author robert
 * 
 */
public class ItemData
{
	private final ItemStack stack;
	private final boolean allMeta;

	public ItemData(ItemStack stack)
	{
		this(stack, false);
	}

	public ItemData(ItemStack stack, boolean allMeta)
	{
		this.stack = stack;
		this.allMeta = allMeta;
	}

	public ItemStack stack()
	{
		return stack;
	}

	public boolean allMeta()
	{
		return allMeta;
	}

	@Override
	public String toString()
	{
		return stack.getTypeId() + (allMeta ? "" : ":" + stack.getItemMeta());
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof ItemStack)
		{
			ItemStack stack = (ItemStack) object;
			if (stack.getTypeId() == this.stack().getTypeId())
			{
				if (allMeta() || !stack.hasItemMeta() && !stack().hasItemMeta())
					return true;
				return stack.getItemMeta() == this.stack().getItemMeta();
			}
		}
		else if (object instanceof ItemData)
		{
			return ((ItemData) object).equals(stack());
		}
		return super.equals(object);
	}
}
