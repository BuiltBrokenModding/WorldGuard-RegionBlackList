package com.builtbroken.region.blacklist;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import com.builtbroken.region.blacklist.worldguard.WorldGuardSupport;

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
	private Listener worldGuardListener;
	private Listener factionsListener;
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
		loadFactionSupport();
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
				factionsListener = new WorldGuardSupport();
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
					worldGuardListener = new WorldGuardSupport();
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
