package com.builtbroken.region.blacklist;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import com.builtbroken.region.blacklist.worldguard.WorldGuardSupport;
import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

/**
 * Bukkit plugin to work with worldguard to take as a user enters a region. Then give them back
 * after the user has left the region.
 * 
 * @since 6/24/2014
 * @author Robert Seifert
 */
public class PluginRegionBlacklist extends JavaPlugin
{
	private static PluginRegionBlacklist instance;
	private Listener listener;
	private PluginLogger logger;

	public static PluginRegionBlacklist instance()
	{
		return instance;
	}

	@Override
	public void onEnable()
	{
		logger().info("Enabled!");
		instance = this;
		loadWorldGuardSupport();
	}
	
	/** Loads listener that deals with WorldGuard plugin support */
	public void loadWorldGuardSupport()
	{
		if (listener == null)
		{
			Plugin wg = getServer().getPluginManager().getPlugin("WorldGuard");
			Plugin wgFlag = getServer().getPluginManager().getPlugin("WGCustomFlags");
			if (wg != null)
			{
				if (wgFlag != null)
				{
					logger().info("WorldGuard support loaded");
					listener = new WorldGuardSupport();
					getServer().getPluginManager().registerEvents(this.listener, this);
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
	public PluginLogger logger()
	{
		if (logger == null)
		{
			logger = new PluginLogger(this);
			logger.setParent(getLogger());
		}
		return logger;
	}

}
