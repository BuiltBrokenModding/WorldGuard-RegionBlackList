package com.builtbroken.region.blacklist.worldguard;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Handles common worldguard related methods
 * 
 * @author Robert Seifert
 * 
 */
public class WGUtility
{
	private static WorldGuardPlugin worldGuard = null;
	private static WGCustomFlagsPlugin wgFlags = null;

	/** Gets the worldguard plugin currently loaded */
	protected static WorldGuardPlugin worldGuard()
	{
		if (worldGuard == null)
		{
			Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

			if (plugin instanceof WorldGuardPlugin)
			{
				worldGuard = (WorldGuardPlugin) plugin;
			}
		}
		return worldGuard;
	}

	/** Gets the custom flags plugin currently loaded */
	protected static WGCustomFlagsPlugin customFlags()
	{
		if (wgFlags == null)
		{
			Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WGCustomFlags");

			if (plugin instanceof WGCustomFlagsPlugin)
			{
				wgFlags = (WGCustomFlagsPlugin) plugin;
			}
		}
		return wgFlags;
	}

	public static ProtectedRegion getRegion(String name)
	{
		WorldGuardPlugin guard = WGUtility.worldGuard();
		if (guard != null)
		{
			for(World world : Bukkit.getWorlds())
			{
				RegionManager manager = guard.getRegionManager(world);
				if (manager != null)
				{
					if(manager.getRegion(name) != null)
					{
						return manager.getRegion(name);
					}
				}
			}
		}
		return null;
	}

	public static ProtectedRegion getRegion(World world, String name)
	{
		WorldGuardPlugin guard = WGUtility.worldGuard();
		if (guard != null)
		{
			RegionManager manager = guard.getRegionManager(world);
			if (manager != null)
			{
				return manager.getRegion(name);
			}
		}
		return null;
	}

	public static ApplicableRegionSet getRegions(World world, Vector vec)
	{
		WorldGuardPlugin guard = WGUtility.worldGuard();
		if (guard != null)
		{
			RegionManager manager = guard.getRegionManager(world);

			if (manager != null)
			{
				return manager.getApplicableRegions(vec);
			}
		}
		return null;
	}

	/** Gets all regions that have the flag allow items or deny items */
	public static List<ProtectedRegion> getRegionsWithFlags(World world, Vector vec, Flag... flags)
	{
		if (flags != null)
		{
			List<ProtectedRegion> list = new LinkedList<ProtectedRegion>();
			ApplicableRegionSet set = getRegions(world, vec);
			if (set != null)
			{
				Iterator<ProtectedRegion> it = set.iterator();
				while (it.hasNext())
				{
					ProtectedRegion region = it.next();
					for (Flag flag : flags)
					{
						if (region.getFlags().containsKey(flag))
						{
							list.add(region);
							break;
						}
					}
				}
				return list;
			}
		}
		return null;
	}
}
