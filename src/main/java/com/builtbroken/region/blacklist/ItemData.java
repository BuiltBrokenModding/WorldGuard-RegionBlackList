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

}
