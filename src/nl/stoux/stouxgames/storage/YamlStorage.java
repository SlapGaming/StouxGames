package nl.stoux.stouxgames.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import nl.stoux.stouxgames.util._;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * YamlStorage class from SlapHomebrew. All credits to {@author naithantu}
 * Minor modifications
 * @author naithantu
 */
public class YamlStorage {
	
	private String fileName;
	private String resourcePath;
	private File file;
	private FileConfiguration config;

	/**
	 * Constructor
	 * @param fileName The filename
	 */
	public YamlStorage(String fileName) {
		this.fileName = fileName + ".yml";
		getConfig();
	}
	
	/**
	 * Make a new YamlStorage
	 * @param resourcePath The path to the resource in plugin. Example: /nl/stoux/stouxgames/games/spleef/
	 * @param fileName The filename
	 */
	public YamlStorage(String resourcePath, String fileName) {
		this.fileName = fileName + ".yml";
		this.resourcePath = resourcePath;
		getConfig();
	}

	public void reloadConfig() {
		if (file == null) {
			file = new File(_.getPlugin().getDataFolder(), fileName);
		}
		config = YamlConfiguration.loadConfiguration(file);

		// Look for defaults in the jar
		InputStream defConfigStream;
		if (resourcePath == null) {
			defConfigStream = _.getPlugin().getResource(fileName);
		} else {
			defConfigStream = _.getPlugin().getResource(resourcePath + fileName);
		}
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
		
		
	}
	
	public FileConfiguration getConfig() {
		if (config == null) {
			this.reloadConfig();
		}
		return config;
	}

	public void saveConfig() {
		if (config == null || file == null) {
			return;
		}
		try {
			getConfig().save(file);
		} catch (IOException ex) {
			_.getPlugin().getLogger().log(Level.SEVERE, "Could not save config to " + config, ex);
		}
	}

	public void saveDefaultConfig() {
		if (file == null) {
			file = new File(_.getPlugin().getDataFolder(), "customConfig.yml");
		}
		if (!file.exists()) {
			_.getPlugin().saveResource(fileName, false);
		}
	}
}
