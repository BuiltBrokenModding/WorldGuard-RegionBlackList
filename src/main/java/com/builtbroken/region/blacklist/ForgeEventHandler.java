package com.builtbroken.region.blacklist;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.event.Event;
import net.minecraftforge.event.IEventListener;

public class ForgeEventHandler implements IEventListener
{
	public List<ForgeSupport> handlers = new ArrayList<ForgeSupport>();

	@Override
	public void invoke(Event paramEvent)
	{
		for(ForgeSupport support : handlers)
		{
			if(support.onEvent(paramEvent))
				return;
		}
	}
}
