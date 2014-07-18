package com.builtbroken.region.blacklist.worldguard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldSaveEvent;

import com.builtbroken.region.blacklist.IBlackListRegion;
import com.builtbroken.region.blacklist.ItemData;
import com.builtbroken.region.blacklist.PluginRegionBlacklist;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Class that handles all world guard support for this mod
 * 
 * @author Robert Seifert
 * 
 */
public class WorldGuardSupport implements IBlackListRegion
{
	public static final ItemFlag ALLOW_ITEM_FLAG = new ItemFlag("allow-items");
	public static final ItemFlag DENY_ITEM_FLAG = new ItemFlag("deny-items");
	public static final ItemFlag ALLOW_ARMOR_FLAG = new ItemFlag("allow-armor");
	public static final ItemFlag DENY_ARMOR_FLAG = new ItemFlag("deny-armor");

	private PluginRegionBlacklist plugin = null;
	private HashMap<String, RegionList> playerItemsPerRegion = new LinkedHashMap<String, RegionList>();

	public WorldGuardSupport(PluginRegionBlacklist plugin)
	{
		this.plugin = plugin;
		WGUtility.customFlags().addCustomFlag(ALLOW_ITEM_FLAG);
		WGUtility.customFlags().addCustomFlag(DENY_ITEM_FLAG);
		WGUtility.customFlags().addCustomFlag(ALLOW_ARMOR_FLAG);
		WGUtility.customFlags().addCustomFlag(DENY_ARMOR_FLAG);
	}

	/** Updated item data for player */
	public void update(Player player, Location location)
	{
		updatePlayerRegions(player, getRegionItems(player), WGUtility.getRegionsWithFlags(player.getWorld(), new Vector(location.getX(), location.getY(), location.getZ()), DENY_ITEM_FLAG, ALLOW_ITEM_FLAG));

	}

	public void updatePlayerRegions(Player player, RegionList regions, List<ProtectedRegion> regionList)
	{
		if (regionList != null)
		{
			if (regions == null)
				regions = new RegionList(player);

			List<String> regionNames = regions.asNames();
			boolean itemsRemoved = false;
			boolean itemsReturned = false;

			// Go threw all regions the player is in looking for new ones and checking current
			for (ProtectedRegion region : regionList)
			{
				if (regionNames.contains(region.getId()))
				{
					regionNames.remove(region.getId());
				}
				else
				{
					RegionItems item = new RegionItems(player.getWorld(), region);
					itemsRemoved = item.removeItems(player);
					regions.add(item);
				}
			}

			// Remove regions not found
			for (String regionName : regionNames)
			{
				RegionItems item = regions.getRegion(regionName);
				if (item != null)
				{
					itemsReturned = item.returnItems(player);
					if (item.isEmpty())
						regions.remove(item);
				}
			}

			if (regions != null && !regions.isEmpty())
				this.playerItemsPerRegion.put(player.getName(), regions);

			if (plugin.enabledMessages & plugin.enabledItemMessages)
			{
				if(player.hasPermission("reginv.messages"))
				{
					if (!plugin.playerOptOutMessages.containsKey(player.getName()) || !plugin.playerOptOutMessages.get(player.getName()))
					{
						if (itemsReturned)
						{
							player.sendMessage("Items were returned to your inventory!");
						}
						if (itemsRemoved)
						{
							player.sendMessage("Items were removed from your inventory! They will be returned when you leave this region.");
						}
					}
				}
			}
		}
		else
		{
			unload(player);
		}
	}

	public void unload(Player player)
	{
		RegionList list = this.getRegionItems(player);
		if (list != null && !list.isEmpty())
			list.clearPlayer();
	}

	/** Regions linked to the player with items that belong to the player */
	public RegionList getRegionItems(Player player)
	{
		RegionList list = null;
		if (playerItemsPerRegion.containsKey(player.getName()) && playerItemsPerRegion.get(player.getName()) != null)
		{
			list = playerItemsPerRegion.get(player.getName());
		}
		else
		{
			list = new RegionList(player);
		}
		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (args != null && args.length > 0 && args[0] != null)
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
				sender.sendMessage("/reginv region allow <region> <id:meta,id...>");
				sender.sendMessage("/reginv region allow add <region> <id:meta,id...>");
				sender.sendMessage("/reginv region allow remove <region> <id:meta,id...>");
				sender.sendMessage("/reginv region deny  <region> <id:meta,id...>");
				sender.sendMessage("/reginv region deny add <region> <id:meta,id...>");
				sender.sendMessage("/reginv region deny remove <region> <id:meta,id...>");
				return true;
			}
			else if (mainCmd.equalsIgnoreCase("allow") || mainCmd.equalsIgnoreCase("deny"))
			{
				boolean allow = mainCmd.equalsIgnoreCase("allow");
				if (subCmd_flag)
				{
					ProtectedRegion region = WGUtility.getRegion(subCmd);
					if (region != null)
					{
						if (subCmd2_flag && (subCmd2.equalsIgnoreCase("add") || subCmd2.equalsIgnoreCase("remove")))
						{
							boolean add = subCmd2.equalsIgnoreCase("add") && !subCmd2.equalsIgnoreCase("remove");
							boolean sub = subCmd2.equalsIgnoreCase("add") || subCmd2.equalsIgnoreCase("remove");
							if (!sub || sub && subCmd3_flag)
							{
								String toSplit = sub ? subCmd3 : subCmd2;
								List<ItemData> data = allow ? ALLOW_ITEM_FLAG.loadFromDb(toSplit) : DENY_ITEM_FLAG.loadFromDb(toSplit);
								sender.sendMessage("Command is unfinished");
								return true;
							}
							else
							{
								sender.sendMessage("Item data needed");
								return true;
							}
						}
						else
						{
							sender.sendMessage("Unkown Command");
							return true;
						}
					}
					else
					{
						sender.sendMessage("Unkown region: " + subCmd);
						return true;
					}
				}
				sender.sendMessage("/reginv region allow <region> <id:meta,id:meta,id,id:All>");
				return true;
			}
		}
		return false;
	}

	public void save()
	{
		File file = new File(plugin.getDataFolder() + File.separator + "worldguard.save");
		if (!file.getParentFile().exists() && file.getParentFile().mkdirs())
		{
			plugin.logger().severe("Failed to make directories");
		}
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(this.playerItemsPerRegion);
			oos.flush();
			oos.close();
			// Handle I/O exceptions
		}
		catch (NotSerializableException e)
		{
			plugin.logger().severe("Failed save an object: " + e.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void load()
	{
		File file = new File(plugin.getDataFolder() + File.separator + "worldguard.save");
		if (file.exists())
		{
			try
			{
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
				Object result = ois.readObject();
				if (result instanceof HashMap)
				{
					this.playerItemsPerRegion.putAll((HashMap<? extends String, ? extends RegionList>) result);
				}
				ois.close();
			}
			catch (NotSerializableException e)
			{
				plugin.logger().severe("Failed load an object: " + e.getMessage());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else if (!file.getParentFile().exists() && file.getParentFile().mkdirs())
		{
			plugin.logger().severe("Failed to make directories");
		}
	}

	@Override
	public String getName()
	{
		return "WorldGuard";
	}
}
