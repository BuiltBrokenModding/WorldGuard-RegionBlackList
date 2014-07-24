package com.builtbroken.region.blacklist;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class SupportHandler implements Listener
{
	private static int CHANGE_IN_DISTANCE = 10;
	private static int SECONDS_BETWEEN_UPDATES = 10;
	private static long MILLS_BETWEEN_UPDATES = SECONDS_BETWEEN_UPDATES * 1000;

	PluginRegionBlacklist plugin;

	private HashMap<String, Location> lastPlayerUpdateLocation = new LinkedHashMap<String, Location>();
	private HashMap<String, Long> lastPlayerUpateTime = new LinkedHashMap<String, Long>();

	private HashMap<String, PluginSupport> regionSupportListeners = new HashMap<String, PluginSupport>();

	public ItemList rightClickBlock = new ItemList();
	public ItemList leftClickBlock = new ItemList();
	public ItemList rightClickAir = new ItemList();
	public ItemList leftClickAir = new ItemList();
	public ItemList globalBannedItems = new ItemList();
	public ItemList globalBannedArmors = new ItemList();

	public SupportHandler(PluginRegionBlacklist plugin)
	{
		this.plugin = plugin;
	}

	/** Registers a new support class */
	public void register(PluginSupport support)
	{
		String name = support.getName();
		if (!regionSupportListeners.containsKey(name))
		{
			regionSupportListeners.put(name, support);
		}
	}

	/** Updates item data for player */
	public void update(Player player)
	{
		if (player != null)
		{
			Location lastLocation = getLastLocation(player);
			Location loc = player.getLocation();
			boolean distance_flag = lastLocation == null || lastLocation.distance(loc) >= CHANGE_IN_DISTANCE;
			boolean time_flag = !lastPlayerUpateTime.containsKey(player.getName()) || System.currentTimeMillis() - lastPlayerUpateTime.get(player.getName()) >= MILLS_BETWEEN_UPDATES;

			if (distance_flag || time_flag)
			{
				this.lastPlayerUpdateLocation.put(player.getName(), loc.clone());
				this.lastPlayerUpateTime.put(player.getName(), System.currentTimeMillis());
				for (PluginSupport support : regionSupportListeners.values())
					support.update(player, loc);
			}
		}
	}

	/** Asks each supporting class to unload the player */
	public void unload(Player player)
	{
		for (PluginSupport support : regionSupportListeners.values())
			support.unload(player);
	}

	/** Player's last location when we ran an update */
	public Location getLastLocation(Player player)
	{
		if (lastPlayerUpdateLocation.containsKey(player.getName()))
		{
			return lastPlayerUpdateLocation.get(player.getName());
		}
		return null;
	}

	/********************************
	 * Events that dump the player's data
	 ********************************/

	@EventHandler
	public void onDeath(EntityDeathEvent event)
	{
		if (event.getEntity() instanceof Player)
			unload((Player) event.getEntity());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		unload(event.getPlayer());
	}

	@EventHandler
	public void onKicked(PlayerKickEvent evt)
	{
		unload(evt.getPlayer());
	}

	/********************************
	 * Events that trigger updates
	 ********************************/

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event)
	{
		update(event.getPlayer());
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		update(event.getPlayer());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		update(event.getPlayer());
	}

	// @EventHandler
	public void onPickUpItem(PlayerPickupItemEvent event)
	{
		Item item = event.getItem();
		ItemStack stack = item.getItemStack();
		if (this.globalBannedItems.contains(stack) || this.globalBannedArmors.contains(stack))
		{
			event.setCancelled(true);
			item.remove();
		}
	}

	@EventHandler
	public void onInteraction(PlayerInteractEvent event)
	{
		for (PluginSupport support : regionSupportListeners.values())
		{
			if (!support.canUse(event.getPlayer(), event.getItem(), event.getClickedBlock(), event.getAction()))
			{
				event.getPlayer().sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You're not allowed to use that here.").toString());
				event.setCancelled(true);
				return;
			}
		}
	}

	/** Loads the config from file */
	public void loadConfig(YamlConfiguration config)
	{
		int version = config.getInt("version");
		if (version == 1 || version == 0)
		{
			plugin.enabledMessages = config.getBoolean("messages.enable", true);

			plugin.enabledItemMessages = config.getBoolean("messages.items.enable", true);
			plugin.messageItemTakeTemp = config.getString("messages.items.temp.take", "Some items were temp removed");
			plugin.messageItemTakeReturn = config.getString("messages.items.temp.return", "Your items were returned");
			plugin.messageItemTakeBan = config.getString("messages.items.ban", "Some items was permanently remove");

			plugin.enabledWarningMessages = config.getBoolean("messages.armor.enable", true);
			plugin.messageArmorTakeTemp = config.getString("messages.armor.temp.take", "Armor your wearing was temp remove");
			plugin.messageArmorTakeReturn = config.getString("messages.armor.temp.return", "Your armor was returned");
			plugin.messageArmorTakeBan = config.getString("messages.armor.ban", "Armor your wearing was permanently remove");

			String rightClickBlock = config.getString("protection.edit.rightclick.block", "");
			String leftClickBlock = config.getString("protection.edit.leftclick.block", "");
			String rightClickAir = config.getString("protection.edit.rightclick.air", "");
			String leftClickAir = config.getString("protection.edit.leftclick.air", "");
			String bannedItems = config.getString("inventory.global.items.ban", "");
			String bannedArmors = config.getString("inventory.global.armors.ban", "");
			if (!rightClickBlock.contains("Replace:"))
			{
				this.rightClickBlock = loadItemString(rightClickBlock);
			}
			if (!leftClickBlock.contains("Replace:"))
			{
				this.leftClickBlock = loadItemString(leftClickBlock);
			}
			if (!rightClickAir.contains("Replace:"))
			{
				this.rightClickAir = loadItemString(rightClickAir);
			}
			if (!leftClickAir.contains("Replace:"))
			{
				this.leftClickAir = loadItemString(leftClickAir);
			}
			if (!bannedItems.contains("Replace:"))
			{
				this.globalBannedItems = loadItemString(bannedItems);
			}
			if (!bannedArmors.contains("Replace:"))
			{
				this.globalBannedArmors = loadItemString(bannedArmors);
			}
		}

		for (PluginSupport support : regionSupportListeners.values())
			support.loadConfig(config);
	}

	/** Creates the config */
	public void createConfig(YamlConfiguration config)
	{
		// Version 1 config
		config.set("messages.enable", true);

		config.set("messages.items.enable", true);
		config.set("messages.items.temp.take", "Some items were temp removed");
		config.set("messages.items.temp.return", "Your items were returned");
		config.set("messages.items.ban", "Some items was permanently removed");

		config.set("messages.armor.enable", true);
		config.set("messages.armor.temp.take", "Armor your wearing was temp removed");
		config.set("messages.armor.temp.return", "Your armor was returned");
		config.set("messages.armor.ban", "Armor your wearing was permanently removed");

		config.set("protection.edit.rightclick.block", "Replace: Add items that can grief when right clicking a block");
		config.set("protection.edit.rightclick.air", "Replace: Add items that can grief when right clicking in the air");
		config.set("protection.edit.leftclick.block", "Replace: Add items that can grief when left clicking a block");
		config.set("protection.edit.lefttclick.air", "Replace: Add items that can grief when left click in the air");

		config.set("inventory.global.items.ban", "Replace: Items that are always banned");
		config.set("inventory.global.armors.ban", "Replace: Armors that are always banned");

		for (PluginSupport support : regionSupportListeners.values())
			support.createConfig(config);
	}

	public void save()
	{
		for (PluginSupport support : regionSupportListeners.values())
			support.save();
	}

	public void load()
	{
		for (PluginSupport support : regionSupportListeners.values())
			support.load();
	}

	public static String getItemClass(ItemStack stack)
	{
		int id = stack.getTypeId();
		try
		{
			if (id >= 0 && id < 4096)
			{
				Class<?> clazz = Class.forName("net.minecraft.block.Block");
				if (clazz != null)
				{
					Object[] blocks = (Object[]) clazz.getField("field_71973_m").get(clazz);
					if (blocks != null)
					{
						Object block = blocks[id];
						return "" + block.getClass();
					}
				}
			}
			else if (id >= 4096)
			{
				Class<?> clazz = Class.forName("net.minecraft.item.Item");
				if (clazz != null)
				{
					Object[] items = (Object[]) clazz.getField("field_77698_e").get(clazz);
					if (items != null)
					{
						Object item = items[id];
						return "" + item.getClass();
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return stack.toString();
	}

	/** Loads a string that contains item ids and meta */
	public static ItemList loadItemString(String arg0)
	{
		try
		{
			ItemList itemList = new ItemList();
			String newString = arg0.replace(" ", "");
			String[] stacks = newString.split(",");
			for (String stack : stacks)
			{
				if (stack != null && !stack.isEmpty() && !stack.equalsIgnoreCase(""))
				{
					String[] data = stack.split(":");
					int id = Integer.parseInt(data[0]);
					int meta = -1;
					boolean allMeta = false;
					if (data.length > 1)
					{
						if (data[1].equalsIgnoreCase("all"))
						{
							allMeta = true;
						}
						else if (data[1] != null && !data[1].isEmpty() && !data[1].equalsIgnoreCase(""))
						{
							meta = Integer.parseInt(data[1]);
						}
					}
					if (meta == -1)
					{
						allMeta = true;
					}
					itemList.add(new ItemData(new ItemStack(id, 1, (short) meta), allMeta));
				}
			}
			return itemList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return new ItemList();
		}

	}
}
