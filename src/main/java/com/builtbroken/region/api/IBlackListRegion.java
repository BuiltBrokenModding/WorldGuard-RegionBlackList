package com.builtbroken.region.api;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * Interface used for any class that offers support for restricting inventories within an area
 * 
 * @author Robert Seifert
 */
public interface IBlackListRegion extends Listener
{
	/** Name of the support, used for file saving and lookup maps */
	public String getName();
	
	/** Called to update the player's inventory based on the location */
	public void update(Player player, Location location);
	
	/** Called to unload all data for the player */
	public void unload(Player player);
	
	/** Command trigger from the main command */
	public boolean onCommand(CommandSender sender, String[] args);

	/** Called to save to a file */
	public void save();

	/** Called to load from a file */
	public void load();
	
	/** Loads config settings */
	public void loadConfig(YamlConfiguration config);
	
	/** Creates config settings */
	public void createConfig(YamlConfiguration config);
}
