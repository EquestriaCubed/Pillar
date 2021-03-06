package com.hepolite.pillar.settings;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hepolite.pillar.logging.Log;
import com.hepolite.pillar.utility.NBTAPI;
import com.hepolite.pillar.utility.NBTAPI.NBTList;
import com.hepolite.pillar.utility.NBTAPI.NBTTag;

public abstract class Settings
{
	// Control variables
	private final String name;

	private final File configFile;
	private final FileConfiguration config;

	/** Sets up a settings file under the given plugin folder */
	public Settings(final JavaPlugin plugin, final String name)
	{
		this.name = name;
		this.configFile = new File(plugin.getDataFolder(), "config_" + name + ".yml");
		this.config = YamlConfiguration.loadConfiguration(configFile);
	}

	/** Initializes the settings */
	public final void initialize()
	{
		boolean exists = this.configFile.exists();
		if (!exists)
		{
			addDefaults();
			save();
		}
		reload();
	}

	/** Saves the configuration file to disk */
	public final void save()
	{
		try
		{
			onSave();
			config.save(configFile);
		}
		catch (IOException e)
		{
			Log.log("Failed to save configuration file '" + name + "'! ", Level.WARNING);
			Log.log(e.getLocalizedMessage(), Level.WARNING);
		}
	}

	/** Reloads the configuration file from disk */
	public final void reload()
	{
		try
		{
			config.load(configFile);
			onReload();
		}
		catch (IOException | InvalidConfigurationException e)
		{
			Log.log("Failed to read configuration file '" + name + "'! ", Level.WARNING);
			Log.log(e.getLocalizedMessage(), Level.WARNING);
		}
	}

	/** Allows each setting objects to assign all default values that they care about */
	protected void addDefaults()
	{
	}

	/** Invoked whenever the settings are reload; update cached values here */
	public void onReload()
	{
	}

	/** Invoked whenever the settings are save; useful for storing various values here */
	public void onSave()
	{
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// GET/SET DATA // GET/SET DATA // GET/SET DATA // GET/SET DATA // GET/SET DATA // GET/SET DATA //
	// ///////////////////////////////////////////////////////////////////////////////////////////////

	/** Assigns one default value to the calling configuration class */
	public final void set(String propertyName, Object value)
	{
		if (value instanceof SoundSetting)
			set(propertyName, (SoundSetting) value);
		else if (value instanceof PotionEffectSetting)
			set(propertyName, (PotionEffectSetting) value);
		else if (value instanceof PotionEffectSetting[])
			set(propertyName, (PotionEffectSetting[]) value);
		else if (value instanceof ItemStack)
			set(propertyName, (ItemStack) value);
		else if (value instanceof ItemStack[])
			set(propertyName, (ItemStack[]) value);
		else if (value instanceof PotionEffectType)
			set(propertyName, (PotionEffectType) value);
		else if (value instanceof PotionEffectType[])
			set(propertyName, (PotionEffectType[]) value);
		else if (value instanceof Enchantment)
			set(propertyName, (Enchantment) value);
		else if (value instanceof Enchantment[])
			set(propertyName, (Enchantment[]) value);
		else if (value instanceof EntityType)
			set(propertyName, (EntityType) value);
		else if (value instanceof EntityType[])
			set(propertyName, (EntityType[]) value);
		else if (value instanceof NBTTag)
			set(propertyName, (NBTTag) value);
		else if (value instanceof NBTList)
			set(propertyName, (NBTList) value);
		else
			config.set(propertyName, value);
	}

	private final void set(String propertyName, SoundSetting setting)
	{
		config.set(propertyName + ".enable", setting.enable);
		config.set(propertyName + ".sound", setting.sound.toString().toLowerCase());
		config.set(propertyName + ".volume", setting.volume);
		config.set(propertyName + ".pitch", setting.pitch);
	}

	private final void set(String propertyName, PotionEffectSetting setting)
	{
		config.set(propertyName + ".type", setting.type.getName().toLowerCase());
		config.set(propertyName + ".duration", setting.duration);
		config.set(propertyName + ".amplifier", setting.amplifier);
	}

	private final void set(String propertyName, PotionEffectSetting[] setting)
	{
		for (int i = 0; i < setting.length; i++)
			set(propertyName + ".Effect " + i, setting[i]);
	}

	private final void set(String propertyName, ItemStack item)
	{
		config.set(propertyName + ".type", item.getType().toString().toLowerCase());
		config.set(propertyName + ".amount", item.getAmount());
		config.set(propertyName + ".meta", item.getDurability());

		ItemMeta meta = item.getItemMeta();
		if (meta != null)
		{
			config.set(propertyName + ".name", meta.getDisplayName());
			config.set(propertyName + ".lore", meta.getLore());
		}

		NBTTag tag = NBTAPI.getTag(item);
		if (tag != null)
			set(propertyName + ".nbt", tag);
	}

	private final void set(String propertyName, ItemStack[] items)
	{
		for (int i = 0; i < items.length; i++)
			set(propertyName + "Item " + i, items[i]);
	}

	private final void set(String propertyName, PotionEffectType setting)
	{
		config.set(propertyName, setting.getName().toLowerCase());
	}

	private final void set(String propertyName, PotionEffectType[] setting)
	{
		List<String> types = new LinkedList<String>();
		for (PotionEffectType type : setting)
			types.add(type.getName().toLowerCase());
		config.set(propertyName, types);
	}

	private final void set(String propertyName, Enchantment setting)
	{
		config.set(propertyName, setting.getName().toLowerCase());
	}

	private final void set(String propertyName, Enchantment[] setting)
	{
		List<String> types = new LinkedList<String>();
		for (Enchantment enchantment : setting)
			types.add(enchantment.getName().toLowerCase());
		config.set(propertyName, types);
	}

	private final void set(String propertyName, EntityType setting)
	{
		config.set(propertyName, setting.toString().toLowerCase());
	}

	private final void set(String propertyName, EntityType[] setting)
	{
		List<String> types = new LinkedList<String>();
		for (EntityType entityType : setting)
			types.add(entityType.toString().toLowerCase());
		config.set(propertyName, types);
	}

	public final void set(String field, NBTTag tag)
	{
		set(field, (Object) null);
		for (String key : tag.getKeys())
		{
			String type = tag.format(key);

			set(field + "." + key + ".type", type);
			if (type.equals("tag"))
				set(field + "." + key + ".value", tag.getTag(key));
			else if (type.equals("list"))
				set(field + "." + key + ".value", tag.getList(key));
			else if (type.equals("string"))
				set(field + "." + key + ".value", tag.getString(key));
			else if (type.equals("int"))
				set(field + "." + key + ".value", tag.getInt(key));
			else if (type.equals("long"))
				set(field + "." + key + ".value", tag.getLong(key));
			else if (type.equals("short"))
				set(field + "." + key + ".value", tag.getShort(key));
			else if (type.equals("byte"))
				set(field + "." + key + ".value", tag.getByte(key));
			else if (type.equals("float"))
				set(field + "." + key + ".value", tag.getFloat(key));
			else if (type.equals("double"))
				set(field + "." + key + ".value", tag.getDouble(key));
		}
	}

	public final void set(String field, NBTList tag)
	{
		set(field, (Object) null);
		for (int i = 0; i < tag.size(); i++)
		{
			String format = tag.format(i);

			set(field + "." + i + ".format", format);
			if (format.equals("tag"))
				set(field + "." + i + ".value", tag.getTag(i));
			else if (format.equals("list"))
				set(field + "." + i + ".value", tag.getList(i));
			else if (format.equals("string"))
				set(field + "." + i + ".value", tag.getString(i));
			else if (format.equals("int"))
				set(field + "." + i + ".value", tag.getInt(i));
			else if (format.equals("long"))
				set(field + "." + i + ".value", tag.getLong(i));
			else if (format.equals("short"))
				set(field + "." + i + ".value", tag.getShort(i));
			else if (format.equals("byte"))
				set(field + "." + i + ".value", tag.getByte(i));
			else if (format.equals("float"))
				set(field + "." + i + ".value", tag.getFloat(i));
			else if (format.equals("double"))
				set(field + "." + i + ".value", tag.getDouble(i));
		}
	}

	/** Returns true if the given property exists in the config file */
	public final boolean has(String propertyName)
	{
		return config.contains(propertyName);
	}

	/** Removes the given field from the config */
	public final void remove(String propertyName)
	{
		config.set(propertyName, null);
	}

	/** Returns a boolean value from the config file */
	public final boolean getBool(String propertyName)
	{
		return config.getBoolean(propertyName);
	}

	/** Returns a long value from the config file */
	public final long getLong(String propertyName)
	{
		return config.getLong(propertyName);
	}

	/** Returns an integer value from the config file */
	public final int getInt(String propertyName)
	{
		return config.getInt(propertyName);
	}

	/** Returns a short value from the config file */
	public final short getShort(String propertyName)
	{
		return (short) config.getInt(propertyName);
	}

	/** Returns a byte value from the config file */
	public final byte getByte(String propertyName)
	{
		return (byte) config.getInt(propertyName);
	}

	/** Returns a floating point value from the config file */
	public final float getFloat(String propertyName)
	{
		return (float) config.getDouble(propertyName);
	}

	/** Returns a double point value from the config file */
	public final double getDouble(String propertyName)
	{
		return config.getDouble(propertyName);
	}

	/** Returns a string from the config file */
	public final String getString(String propertyName)
	{
		return config.getString(propertyName);
	}

	/** Returns a list of string from the config file */
	public final List<String> getStringList(String propertyName)
	{
		return config.getStringList(propertyName);
	}

	/** Returns a set of property keys from the config file */
	public final Set<String> getKeys(String propertyName)
	{
		ConfigurationSection section = config.getConfigurationSection(propertyName);
		if (section == null)
			return new HashSet<String>();
		return section.getKeys(false);
	}

	/** Returns a sound setting from the config file */
	public final SoundSetting getSound(String propertyName)
	{
		boolean enable = getBool(propertyName + ".enable");
		Sound sound = Sound.valueOf(getString(propertyName + ".sound").toUpperCase());
		float volume = getFloat(propertyName + ".volume");
		float pitch = getFloat(propertyName + ".pitch");
		return new SoundSetting(enable, sound, volume, pitch);
	}

	/** Returns a potion effect setting from the config file */
	public final PotionEffectSetting getPotionEffect(String propertyName)
	{
		PotionEffectType type = PotionEffectType.getByName(getString(propertyName + ".type").toUpperCase());
		int duration = getInt(propertyName + ".duration");
		int amplifier = getInt(propertyName + ".amplifier");
		return new PotionEffectSetting(type, duration, amplifier);
	}

	/** Returns multiple a potion effect setting from the config file */
	public final List<PotionEffectSetting> getPotionEffects(String propertyName)
	{
		List<PotionEffectSetting> list = new LinkedList<PotionEffectSetting>();
		Set<String> effects = getKeys(propertyName);
		for (String effect : effects)
			list.add(getPotionEffect(propertyName + "." + effect));
		return list;
	}

	/** Returns a potion effect type from the config file */
	public final PotionEffectType getPotionEffectType(String propertyName)
	{
		return PotionEffectType.getByName(getString(propertyName).toUpperCase());
	}

	/** Returns multiple potion effect types from the config file */
	public final List<PotionEffectType> getPotionEffectTypes(String propertyName)
	{
		List<PotionEffectType> types = new LinkedList<PotionEffectType>();
		for (String string : getStringList(propertyName))
		{
			PotionEffectType type = PotionEffectType.getByName(string.toUpperCase());
			if (type != null)
				types.add(type);
		}
		return types;
	}

	/** Returns an enchantment type from the config file */
	public final Enchantment getEnchantmentType(String propertyName)
	{
		return Enchantment.getByName(getString(propertyName).toUpperCase());
	}

	/** Returns multiple enchantment types from the config file */
	public final List<Enchantment> getEnchantmentTypes(String propertyName)
	{
		List<Enchantment> types = new LinkedList<Enchantment>();
		for (String string : getStringList(propertyName))
		{
			Enchantment type = Enchantment.getByName(string.toUpperCase());
			if (type != null)
				types.add(type);
		}
		return types;
	}

	/** Returns an entity type from the config file */
	public final EntityType getEntityType(String propertyName)
	{
		EntityType type = null;
		try
		{
			type = EntityType.valueOf(getString(propertyName).toUpperCase());
		}
		catch (Exception e)
		{
		}
		return type;
	}

	/** Returns multiple entity types from the config file */
	public final List<EntityType> getEntityTypes(String propertyName)
	{
		List<EntityType> types = new LinkedList<EntityType>();
		for (String string : getStringList(propertyName))
		{
			EntityType type = null;
			try
			{
				type = EntityType.valueOf(string.toUpperCase());
			}
			catch (Exception e)
			{
			}
			if (type != null)
				types.add(type);
		}
		return types;
	}

	/** Returns a full item with lore, type and display name */
	public final ItemStack getItem(String propertyName)
	{
		// Load up all the data
		Material material = null;
		try
		{
			material = Material.getMaterial(config.getString(propertyName + ".type").toUpperCase());
			if (material == null)
				throw new NullPointerException("Invalid material!");
		}
		catch (Exception exception)
		{
			Log.log("Failed to parse item '" + propertyName + "'!", Level.WARNING);
			Log.log(exception.getMessage(), Level.WARNING);
			return null;
		}
		int amount = config.contains(propertyName + ".amount") ? config.getInt(propertyName + ".amount") : 1;
		short meta = (short) config.getInt(propertyName + ".meta");
		String name = config.getString(propertyName + ".name");
		List<String> lore = config.getStringList(propertyName + ".lore");

		// Actually create the item
		ItemStack item = NBTAPI.getItemStack(material, amount, meta);

		// Load additional information
		if (has(propertyName + ".nbt"))
			NBTAPI.setTag(item, getNBTTag(propertyName + ".nbt"));

		ItemMeta itemMeta = item.getItemMeta();
		if (name != null)
			itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		if (lore != null)
		{
			List<String> list = new LinkedList<String>();
			for (String line : lore)
				list.add(ChatColor.translateAlternateColorCodes('&', line));
			itemMeta.setLore(list);
		}
		item.setItemMeta(itemMeta);
		return item;
	}

	/** Returns a list of full items with lore, type and display name */
	public final List<ItemStack> getItems(String propertyName)
	{
		List<ItemStack> items = new LinkedList<ItemStack>();
		Set<String> keys = getKeys(propertyName);
		for (String key : keys)
			items.add(getItem(propertyName + "." + key));
		return items;
	}

	/** Loads up a NBT Tag from the configuration */
	public final NBTTag getNBTTag(String field)
	{
		NBTTag tag = new NBTTag();

		Set<String> keys = getKeys(field);
		for (String key : keys)
		{
			String type = getString(field + "." + key + ".type");
			if (type.equals("tag"))
				tag.setTag(key, getNBTTag(field + "." + key + ".value"));
			else if (type.equals("list"))
				tag.setList(key, getNBTList(field + "." + key + ".value"));
			else if (type.equals("string"))
				tag.setString(key, getString(field + "." + key + ".value"));
			else if (type.equals("int"))
				tag.setInt(key, getInt(field + "." + key + ".value"));
			else if (type.equals("long"))
				tag.setLong(key, getLong(field + "." + key + ".value"));
			else if (type.equals("short"))
				tag.setShort(key, getShort(field + "." + key + ".value"));
			else if (type.equals("byte"))
				tag.setByte(key, getByte(field + "." + key + ".value"));
			else if (type.equals("float"))
				tag.setFloat(key, getFloat(field + "." + key + ".value"));
			else if (type.equals("double"))
				tag.setDouble(key, getDouble(field + "." + key + ".value"));
		}
		return tag;
	}

	/** Loads up a NBT List from the configuration */
	public final NBTList getNBTList(String field)
	{
		NBTList tag = new NBTList();

		for (String key : getKeys(field))
		{
			String format = getString(field + "." + key + ".format");
			if (format.equals("tag"))
				tag.addTag(getNBTTag(field + "." + key + ".value"));
			else if (format.equals("list"))
				tag.addList(getNBTList(field + "." + key + ".value"));
			else if (format.equals("string"))
				tag.addString(getString(field + "." + key + ".value"));
			else if (format.equals("int"))
				tag.addInt(getInt(field + "." + key + ".value"));
			else if (format.equals("long"))
				tag.addLong(getLong(field + "." + key + ".value"));
			else if (format.equals("short"))
				tag.addShort(getShort(field + "." + key + ".value"));
			else if (format.equals("byte"))
				tag.addByte(getByte(field + "." + key + ".value"));
			else if (format.equals("float"))
				tag.addFloat(getFloat(field + "." + key + ".value"));
			else if (format.equals("double"))
				tag.addDouble(getDouble(field + "." + key + ".value"));
		}
		return tag;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////

	/** Converts an itemstack to a simple itemstack string, used for storage. Returns null if item is null */
	public final String writeSimpleItem(ItemStack item)
	{
		return writeSimpleItem(item, true);
	}
	
	/** Converts an itemstack to a simple itemstack string, used for storage. Returns null if item is null */
	public final String writeSimpleItem(ItemStack item, boolean writeName)
	{
		if (item == null)
			return null;
		ItemMeta itemMeta = item.getItemMeta();

		String itemString = item.getType().toString().toLowerCase();
		if (item.getDurability() != 0)
			itemString += "-" + item.getDurability();
		if (itemMeta != null && itemMeta.hasDisplayName() && writeName)
			itemString += ";" + itemMeta.getDisplayName().replaceAll(String.valueOf(ChatColor.COLOR_CHAR), "&");
		if (item.getAmount() != 1)
			itemString += "=" + item.getAmount();
		return itemString;
	}

	/** Returns a simple itemstack, which is stored on the form MATERIAL-META;NAME=AMOUNT. Meta, name and amount are optional parameters */
	public final ItemStack parseSimpleItem(String itemString)
	{
		Material material = Material.AIR;
		short meta = 0;
		int amount = 1;
		String name = "";

		// Parse the string
		try
		{
			String[] parts = itemString.split("=");
			if (parts.length == 2)
				amount = Integer.parseInt(parts[1]);
			parts = itemString.split(";");
			if (parts.length == 2)
				name = parts[1];
			parts = parts[0].split("-");
			if (parts.length == 2)
				meta = Short.parseShort(parts[1]);
			material = Material.getMaterial(parts[0].toUpperCase());
			if (material == null)
				throw new NullPointerException("Invalid material!");
		}
		catch (Exception exception)
		{
			Log.log("Failed to parse simple item '" + itemString + "'!", Level.WARNING);
			Log.log(exception.getLocalizedMessage(), Level.WARNING);
			return null;
		}
		ItemStack item = new ItemStack(material, amount, meta);
		ItemMeta itemMeta = item.getItemMeta();
		if (!name.equals(""))
			itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		item.setItemMeta(itemMeta);
		return item;
	}

	/** Converts an location to a simple location string, used for storage. Returns null if the location is null */
	public final String writeSimpleLocation(Location location)
	{
		if (location == null || location.getWorld() == null)
			return null;
		return String.format("%s=%.0f=%.0f=%.0f", location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
	}

	/** Returns a simple location, which is stored on the form WORLD_NAME=X=Y=Z */
	public final Location parseSimpleLocation(String locationString)
	{
		String parts[] = locationString.split("=");
		World world = null;
		int x = 0, y = 0, z = 0;
		try
		{
			world = Bukkit.getWorld(parts[0]);
			x = Integer.parseInt(parts[1]);
			y = Integer.parseInt(parts[2]);
			z = Integer.parseInt(parts[3]);
		}
		catch (Exception e)
		{
			Log.log("Failed to parse simple location '" + locationString + "'!", Level.WARNING);
			Log.log(e.getLocalizedMessage(), Level.WARNING);
			return null;
		}
		return new Location(world, x, y, z);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////

	/** Sound data node, used to store a sound object to the config file */
	public final static class SoundSetting
	{
		public boolean enable;
		public Sound sound;
		public float volume;
		public float pitch;

		public SoundSetting(boolean enable, Sound sound, float volume, float pitch)
		{
			this.enable = enable;
			this.sound = sound;
			this.volume = volume;
			this.pitch = pitch;
		}

		/** Plays the sound at the given location, if the sound is enabled */
		public final void play(Location location)
		{
			if (enable && sound != null && location != null)
				location.getWorld().playSound(location, sound, volume, pitch);
		}
	}

	/** Potion effect data node, used to store a potion effect object to the config file */
	public final static class PotionEffectSetting
	{
		public PotionEffectType type;
		public int duration;
		public int amplifier;
		public float chance;

		public PotionEffectSetting(PotionEffectType type, int duration, int amplifier)
		{
			this.type = type;
			this.duration = duration;
			this.amplifier = amplifier;
		}

		/** Creates a new potion effect object from the potion effect data */
		public final PotionEffect create()
		{
			if (type != null)
				return new PotionEffect(type, duration, amplifier - 1);
			return null;
		}
	}
}
