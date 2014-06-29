package com.builtbroken.region.blacklist;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import com.builtbroken.region.blacklist.worldguard.WorldGuardSupport;
import com.massivecraft.mcore.util.Txt;

/**
 * Bukkit plugin to work with worldguard to take as a user enters a region. Then give them back
 * after the user has left the region.
 * 
 * @since 6/24/2014
 * @author Robert Seifert
 */
public class PluginRegionBlacklist extends JavaPlugin
{
	private Listener worldGuardListener;
	private Listener factionsListener;
	private Logger logger;
	private String loggerPrefix = "";
	public HashMap<String, Boolean> playerOptOutMessages = new LinkedHashMap<String, Boolean>();

	/*
	 * TODO - list of stuff to still do Add: Factions support Add: Chat lang config Add: Global item
	 * ban list Add: Settings config Add: Chat command to change settings Add: Chat command to op
	 * out of messages Add: Item save/load to prevent item loss Add: Events for later use and common
	 * support Merged: Some faction and worldguard common support Add: Chest GUI to show items that
	 * were removed from the player
	 */

	@Override
	public void onEnable()
	{
		loggerPrefix = String.format("[InvReg %s]", this.getDescription().getVersion());
		logger().info("Enabled!");
		loadWorldGuardSupport();
		loadFactionSupport();
		getCommand("RegInv").setExecutor(this);
	}

	/** Loads listener that deals with Factions plugin support */
	public void loadFactionSupport()
	{
		if (factionsListener == null)
		{
			Plugin factions = getServer().getPluginManager().getPlugin("Factions");
			if (factions != null)
			{
				logger().info("Factions support loaded");
				factionsListener = new WorldGuardSupport(this);
				getServer().getPluginManager().registerEvents(this.factionsListener, this);
			}
			else
			{
				logger().info("Failed to find factions on plugin list");
			}
		}
	}

	/** Loads listener that deals with WorldGuard plugin support */
	public void loadWorldGuardSupport()
	{
		if (worldGuardListener == null)
		{
			Plugin wg = getServer().getPluginManager().getPlugin("WorldGuard");
			Plugin wgFlag = getServer().getPluginManager().getPlugin("WGCustomFlags");
			if (wg != null)
			{
				if (wgFlag != null)
				{
					logger().info("WorldGuard support loaded");
					worldGuardListener = new WorldGuardSupport(this);
					getServer().getPluginManager().registerEvents(this.worldGuardListener, this);
				}
				else
				{
					logger().info("Failed to find WGCutomFlags on plugin list");
				}
			}
			else
			{
				logger().info("Failed to find WorldGuard on plugin list");
			}
		}
	}

	@Override
	public void onDisable()
	{
		logger().info("Disabled!");
	}

	/** Logger used by the plugin, mainly just prefixes everything with the name */
	public Logger logger()
	{
		if (logger == null)
		{
			logger = new Logger(PluginRegionBlacklist.this.getClass().getCanonicalName(), null)
			{
				public void log(LogRecord logRecord)
				{				
					
					logRecord.setMessage(loggerPrefix + logRecord.getMessage());
					super.log(logRecord);
				}
			};
			logger.setParent(getLogger());
		}
		return logger;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (command.getName().equalsIgnoreCase("RegInv"))
		{
			if (args != null && args[0] != null)
			{
				String mainCmd = args[0];

				boolean isPlayer = sender instanceof Player;
				boolean subCmd_flag = args.length > 1 && args[1] != null;
				boolean subCmd2_flag = args.length > 2 && args[2] != null;
				boolean subCmd3_flag = args.length > 3 && args[3] != null;

				Player player = isPlayer ? (Player) sender : null;
				String subCmd = subCmd_flag ? args[1] : null;
				String subCmd2 = subCmd2_flag ? args[2] : null;
				String subCmd3 = subCmd3_flag ? args[3] : null;

				if (mainCmd.equalsIgnoreCase("help"))
				{
					sender.sendMessage("/RegInv version");
					if (isPlayer)
					{
						sender.sendMessage("/RegInv messages <on/off>");
					}
					return true;
				}
				else if (mainCmd.equalsIgnoreCase("version"))
				{
					sender.sendMessage("Version: " + this.getDescription().getVersion());
					return true;
				}
				else if (mainCmd.equalsIgnoreCase("messages") && isPlayer)
				{
					if (subCmd_flag)
					{
						if (subCmd.equalsIgnoreCase("on"))
						{
							playerOptOutMessages.put(player.getName(), false);
						}
						else if (subCmd.equalsIgnoreCase("off"))
						{
							playerOptOutMessages.put(player.getName(), true);
						}
					}
					else
					{
						if (playerOptOutMessages.containsKey(player.getName()))
						{
							playerOptOutMessages.put(player.getName(), !playerOptOutMessages.get(player.getName()));
						}
						else
						{
							playerOptOutMessages.put(player.getName(), true);
						}
					}
					boolean flag = playerOptOutMessages.get(player.getName());
					if (flag)
					{
						player.sendMessage("You will no longer get messages for inventory changes");
					}
					else
					{
						player.sendMessage("You will receive messages for inventory changes");
					}
					return true;
				}
			}
		}
		return false;
	}
}
