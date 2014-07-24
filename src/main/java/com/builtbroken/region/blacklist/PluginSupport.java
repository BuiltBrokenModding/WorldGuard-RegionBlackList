package com.builtbroken.region.blacklist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class PluginSupport implements Listener
{
	private String name;
	protected PluginRegionBlacklist plugin = null;

	public PluginSupport()
	{

	}

	public PluginSupport(PluginRegionBlacklist plugin, String name)
	{
		this.plugin = plugin;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void update(Player player, Location location)
	{
		// TODO Auto-generated method stub

	}

	public boolean canUse(Player player, ItemStack stack, Block clickedBlock, Action action)
	{
		if(stack != null)
		{
			MaterialData item = stack.getData();
			System.out.println("ItemStack:ID " + stack.getTypeId() +"  Meta: " + stack.getDurability());
			System.out.println("Class: " + SupportHandler.getItemClass(stack));
		}
		try
		{
			if ((clickedBlock == null) || (clickedBlock.getType() == Material.SNOW))
			{
				HashSet transparentMaterials = new HashSet();
				transparentMaterials.add(Byte.valueOf((byte) Material.AIR.getId()));
				transparentMaterials.add(Byte.valueOf((byte) Material.SNOW.getId()));
				transparentMaterials.add(Byte.valueOf((byte) Material.LONG_GRASS.getId()));
				clickedBlock = player.getTargetBlock(transparentMaterials, 500);
			}
		}
		catch (Exception e)
		{
			//Didn't hit anything so safe?
			return true;
		}
		if (clickedBlock != null)
		{
			if (action == Action.LEFT_CLICK_AIR && plugin.supportHandler.leftClickAir.contains(stack))
			{
				return canBuild(player, clickedBlock);
			}
			else if (action == Action.RIGHT_CLICK_AIR && plugin.supportHandler.rightClickAir.contains(stack))
			{
				return canBuild(player, clickedBlock);
			}
			if (action == Action.LEFT_CLICK_BLOCK && plugin.supportHandler.leftClickBlock.contains(stack))
			{
				return canBuild(player, clickedBlock);
			}
			else if (action == Action.RIGHT_CLICK_BLOCK && plugin.supportHandler.rightClickBlock.contains(stack))
			{
				return canBuild(player, clickedBlock);
			}
		}
		return true;
	}

	public boolean canBuild(Player player, Block block)
	{
		return true;
	}

	public void unload(Player player)
	{
		// TODO Auto-generated method stub

	}

	public boolean onCommand(CommandSender sender, String[] args)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void save()
	{
		File file = new File(plugin.getDataFolder() + File.separator + "worldguard.save");
		if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
		{
			plugin.logger().severe("Failed to make directories");
		}
		else
		{
			try
			{
				FileOutputStream outputStream = new FileOutputStream(file);
				save(outputStream);
				outputStream.flush();
				outputStream.close();
			}
			catch (Exception e)
			{
				plugin.logger().severe("Failed to save " + getName() + " data");
				e.printStackTrace();
			}
		}
	}

	public void save(FileOutputStream stream) throws Exception
	{

	}

	public void load()
	{
		File file = new File(plugin.getDataFolder() + File.separator + getName() + ".save");
		if (file.exists())
		{
			try
			{
				FileInputStream inputStream = new FileInputStream(file);
				load(inputStream);
				inputStream.close();
			}
			catch (Exception e)
			{
				plugin.logger().severe("Failed to load " + getName() + " save");
				e.printStackTrace();
			}

		}
		else if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
		{
			plugin.logger().severe("Failed to make directories");
		}
	}

	public void load(FileInputStream stream) throws Exception
	{

	}

	public void loadConfig(YamlConfiguration config)
	{

	}

	public void createConfig(YamlConfiguration config)
	{

	}

}
