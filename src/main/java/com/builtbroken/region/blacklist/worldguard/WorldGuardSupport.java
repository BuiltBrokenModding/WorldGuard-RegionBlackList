package com.builtbroken.region.blacklist.worldguard;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import com.builtbroken.region.blacklist.ItemData;
import com.builtbroken.region.blacklist.PluginRegionBlacklist;
import com.builtbroken.region.blacklist.PluginSupport;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Class that handles all world guard support for this mod
 * 
 * @author Robert Seifert
 * 
 */
public class WorldGuardSupport extends PluginSupport
{
	public static final ItemFlag ALLOW_ITEM_FLAG = new ItemFlag("allow-items");
	public static final ItemFlag DENY_ITEM_FLAG = new ItemFlag("deny-items");
	public static final ItemFlag ALLOW_ARMOR_FLAG = new ItemFlag("allow-armor");
	public static final ItemFlag DENY_ARMOR_FLAG = new ItemFlag("deny-armor");

	private HashMap<String, RegionList> playerItemsPerRegion = new LinkedHashMap<String, RegionList>();

	public WorldGuardSupport(PluginRegionBlacklist plugin)
	{
		super(plugin, "WorldGuard");
		WGUtility.customFlags().addCustomFlag(ALLOW_ITEM_FLAG);
		WGUtility.customFlags().addCustomFlag(DENY_ITEM_FLAG);
		WGUtility.customFlags().addCustomFlag(ALLOW_ARMOR_FLAG);
		WGUtility.customFlags().addCustomFlag(DENY_ARMOR_FLAG);
	}

	@Override
	public boolean canUse(Player player, ItemStack stack, Block clickedBlock, Action action)
	{
		LocalPlayer lplayer = WGUtility.getPlayer(player);
		Location loc = clickedBlock.getLocation();
		ApplicableRegionSet set = WGUtility.getRegions(clickedBlock.getWorld(), new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

		if (action == Action.RIGHT_CLICK_BLOCK)
		{
			return set.canConstruct(lplayer);
		}
		return true;
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

			if (plugin.enabledMessages && plugin.enabledItemMessages)
			{
				if (player.hasPermission("reginv.messages"))
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

	@Override
	public void save(FileOutputStream stream) throws Exception
	{
		ObjectOutputStream oos = new ObjectOutputStream(stream);
		oos.writeObject(this.playerItemsPerRegion);
	}

	@Override
	public void load(FileInputStream stream) throws Exception
	{
		ObjectInputStream ois = new ObjectInputStream(stream);
		Object result = ois.readObject();
		if (result instanceof HashMap)
		{
			this.playerItemsPerRegion.putAll((HashMap<? extends String, ? extends RegionList>) result);
		}
	}
}
