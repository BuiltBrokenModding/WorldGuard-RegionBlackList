package com.builtbroken.region.blacklist;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

/**
 * Bukkit plugin to work with worldguard to take as a user enters a region. Then give them back
 * after the user has left the region.
 * @since 6/24/2014
 * @author Robert Seifert 
 */
public class PluginRegionBlacklist extends JavaPlugin
{
	public static final ItemFlag ALLOW_ITEM_FLAG = new ItemFlag("allow-items");
	public static final ItemFlag DENY_ITEM_FLAG = new ItemFlag("deny-items");
	private static PluginRegionBlacklist instance;
	private EventListener listener;
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
		listener = new EventListener();
		getServer().getPluginManager().registerEvents(this.listener, this);
		this.getWGCustomFlags().addCustomFlag(ALLOW_ITEM_FLAG);
		this.getWGCustomFlags().addCustomFlag(DENY_ITEM_FLAG);
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

	/** Gets the worldguard plugin currently loaded */
	protected WorldGuardPlugin getWorldGuard()
	{
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin))
		{
			return null;
		}

		return (WorldGuardPlugin) plugin;
	}

	/** Gets the custom flags plugin currently loaded */
	protected WGCustomFlagsPlugin getWGCustomFlags()
	{
		Plugin plugin = getServer().getPluginManager().getPlugin("WGCustomFlags");

		if (plugin == null || !(plugin instanceof WGCustomFlagsPlugin))
		{
			return null;
		}

		return (WGCustomFlagsPlugin) plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("test"))
		{
			player.sendMessage(ChatColor.AQUA + "Hello World!");
		}
		return false;
	}

}
