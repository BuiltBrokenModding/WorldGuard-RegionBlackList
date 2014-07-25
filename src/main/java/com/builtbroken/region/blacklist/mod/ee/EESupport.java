package com.builtbroken.region.blacklist.mod.ee;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Event;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.builtbroken.region.blacklist.ForgeSupport;
import com.builtbroken.region.blacklist.PluginRegionBlacklist;
import com.pahimar.ee3.event.ActionEvent.ActionResult;
import com.pahimar.ee3.event.ActionRequestEvent;
import com.pahimar.ee3.event.WorldTransmutationEvent;

public class EESupport extends ForgeSupport
{
	public EESupport(PluginRegionBlacklist plugin)
	{
		super(plugin);
	}

	public boolean onEvent(Event event)
	{
		if (event instanceof ActionRequestEvent)
		{
			ActionRequestEvent e = (ActionRequestEvent) event;
			EntityPlayer player = e.modEvent.player;
			Player p = Bukkit.getPlayer(player.field_71092_bJ);
			Location loc = new Location(p.getWorld(), (double) e.x, (double) e.y, (double) e.z);
			if (!plugin.supportHandler.canBuild(p, loc))
			{
				e.setCanceled(true);
			}
			return true;
		}
		else if (event instanceof WorldTransmutationEvent)
		{
			WorldTransmutationEvent e = (WorldTransmutationEvent) event;
			EntityPlayer player = e.player;
			Player p = Bukkit.getPlayer(player.field_71092_bJ);
			Location loc = new Location(p.getWorld(), (double) e.x, (double) e.y, (double) e.z);

			if (!plugin.supportHandler.canBuild(p, loc))
			{
				e.actionResult = ActionResult.FAILURE;
			}
			return true;
		}
		return false;
	}
}
