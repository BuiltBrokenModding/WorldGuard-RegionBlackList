package com.builtbroken.region.blacklist;

import org.bukkit.inventory.ItemStack;

public class ItemWrapper
{
	int slot = 0;
	ItemStack stack = null;

	public ItemWrapper(ItemStack stack, int slot)
	{
		this.stack = stack;
		this.slot = slot;
	}

	public ItemStack getStack()
	{
		return stack;
	}

	public int getSlot()
	{
		return slot;
	}

	public void setStack(ItemStack stack)
	{
		this.stack = stack;		
	}
}
