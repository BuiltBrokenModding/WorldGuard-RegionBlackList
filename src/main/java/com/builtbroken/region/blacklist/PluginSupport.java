package com.builtbroken.region.blacklist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.builtbroken.region.api.IBlackListRegion;

public class PluginSupport implements IBlackListRegion
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

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void update(Player player, Location location)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void unload(Player player)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
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

	@Override
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

	@Override
	public void loadConfig(YamlConfiguration config)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void createConfig(YamlConfiguration config)
	{
		// TODO Auto-generated method stub

	}

}
