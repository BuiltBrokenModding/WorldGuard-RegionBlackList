package com.builtbroken.region.blacklist;

import net.minecraftforge.event.Event;

public class ForgeSupport
{
	protected PluginRegionBlacklist plugin;

	public ForgeSupport(PluginRegionBlacklist plugin)
	{
		this.plugin = plugin;
	}

	public boolean onEvent(Event event)
	{
		return false;
	}
}
