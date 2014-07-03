package com.builtbroken.region.blacklist;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

/**
 * Interface used for any class that offers support for restricting inventories within an area
 * 
 * @author Robert Seifert
 */
public interface IBlackListRegion extends Listener
{
	public boolean onCommand(CommandSender sender, String[] args);
}
