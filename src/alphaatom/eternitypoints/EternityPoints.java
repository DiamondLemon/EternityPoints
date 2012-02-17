package alphaatom.eternitypoints;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EternityPoints extends JavaPlugin implements Listener {
	
	public final Logger logger = Logger.getLogger("Minecraft");
	public static FileConfiguration config;
	public static FileConfiguration customconfig;
	public static String name;
	public static String punisher;
	public static int etpoints;
	private FileConfiguration customConfig = null;
	private File customConfigurationFile = null;

	@Override
	public void onDisable() {
		PluginDescriptionFile pdffile = this.getDescription();
		this.logger.info(pdffile.getName() + " successfully disabled!");
	}

	@Override
	public void onEnable() {
		PluginDescriptionFile pdffile = this.getDescription();
		this.logger.info(pdffile.getName() + " v" + pdffile.getVersion() + " successfully enabled!");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		try{
			config = getConfig();
			customconfig = getCustomConfig();
			File EternityPoints = new File("plugins" + File.separator + "EternityPoints" + File.separator + "config.yml");
			File EternityPoints_1 = new File("plugins" + File.separator + "EternityPoints" + File.separator + "users.yml");
			EternityPoints.mkdir();
			EternityPoints_1.mkdir();
			if (!config.contains("Plugin.name")){
				config.set("Plugin.name", "Eternity Point");
				saveConfig();
			}
			name = config.getString("Plugin.name");
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
	
		public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
			if (sender instanceof Player) {
				if (cmd.getName().equalsIgnoreCase("punish")) {
					if (sender.hasPermission("eternitypoints.punish")) {
					if (args.length == 2) {
								Player p = (Player) sender;
								String punished = args[0];
								int points = Integer.valueOf(args[1]);
								String pointsname = name;
								String pointsnames = name + "s";
								if (points > 1) {
									pointsname = pointsnames;
								}
								ChatColor GOLD = ChatColor.GOLD;
								ChatColor AQUA = ChatColor.AQUA;
								int currpoints = getUserConfigPoints(punished);
								int newpoints = currpoints + points;
								if (newpoints < 0) {
									newpoints = 0;
								}
								String newpoint = String.valueOf(newpoints);
								String point = String.valueOf(points);
								addToUserConfig(punished, points);
								p.sendMessage(AQUA + point + " " + GOLD + pointsname + AQUA + " added to " + punished + " They now have: " + GOLD + newpoint + " " + GOLD + pointsnames);
								if (Bukkit.getServer().getPlayer(args[0]) != null) {
								Player punish = Bukkit.getServer().getPlayer(punished);
								punish.sendMessage(AQUA + point + " " + GOLD + pointsname + AQUA + " added. You now have: " + GOLD + newpoint + " " + GOLD + pointsnames);
								return true;
								} else {
						return false;
							}
					} else {
						sender.sendMessage("Not enough arguments!");
						return false;
					}
				}
			} else {
				return false;
			}
			return false;
			}
			return false;
		}
		
		public void reloadCustomConfig() {
			if (customConfigurationFile == null) {
				customConfigurationFile = new File(getDataFolder(), "users.yml");
			customConfig = YamlConfiguration.loadConfiguration(customConfigurationFile);
			
			InputStream defConfigStream = getResource("users.yml");
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				customConfig.setDefaults(defConfig);
			}
			}
		}
		
		public FileConfiguration getCustomConfig() {
			if (customConfig == null) {
				reloadCustomConfig();
			}
			return customConfig;
			}
		
		public void saveCustomConfig() {
			if (customConfig == null || customConfigurationFile == null) {
				return;
			}
			try {
				customConfig.save(customConfigurationFile);
			} catch (IOException ex) {
				Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + customConfigurationFile, ex);
			}
		}
		
		public void createUserConfig(String input) {
			customconfig = getCustomConfig();
			String path = "users." + input + ".points";
			String path_1 = path + ".log";
			String path_2 = "users." + input + ".punishment";
			if (!customconfig.contains(path)) {
				customconfig.set(path, 0);
				etpoints = customconfig.getInt(path);
			}
			if (!customconfig.contains(path_1)) {
				customconfig.set(path_1, null);
			}
			if (!customconfig.contains(path_2)) {
				customconfig.set(path_2, "none");
			}
			saveCustomConfig();
		}
	
		public void addToUserConfig(String user, int points) {
			reloadCustomConfig();
			customconfig = getCustomConfig();
			String path = "users." + user + ".points";
			if (!customconfig.contains(path)) {
				createUserConfig(user);
				reloadCustomConfig();
			}
			int cpoints = customconfig.getInt(path);
			int npoints = cpoints + points;
			if (npoints < 0) {
				npoints = 0;
			}
			customconfig.set(path, npoints);
			if (points > 0) {
				checkAddPunishment(npoints, user);
			}
			reloadCustomConfig();
			saveCustomConfig();
		}
		
		public int getUserConfigPoints(String user) {
			reloadCustomConfig();
			String path = "users." + user + ".points";
			if (!customconfig.contains(path)) {
				createUserConfig(user);
				reloadCustomConfig();
			}
			return customconfig.getInt(path);
		}
		
		public void checkAddPunishment(int points, String user) {
			if (points >= 9) {
				if (Bukkit.getServer().getPlayer(user) != null) {
				String command = "ban -e " + user;
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
				setPunish("none", user);
				return;
				}
				else {
					setPunish("permaban", user);
				}
			}
			if (points >= 6) {
				if (Bukkit.getServer().getPlayer(user) != null) {
				String command = "ban -e -t 1d " + user;
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
				setPunish("none", user);
				return;
				}
				else {
					setPunish("tempban", user);
				}
			}
			if (points >= 3) {
				if (Bukkit.getServer().getPlayer(user) != null) {
				String command = "jail " + user + " 1h";
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
				setPunish("none", user);
				return;
				}
				else {
					setPunish("jail", user);
				}
			}
		}
		
		public void setPunish(String punishment, String user) {
			String path = "users." + user + ".points";
			String path_2 = "users." + user + ".punishment";
			if (!customconfig.contains(path)) {
				createUserConfig(user);
			}
			customconfig.set(path_2, punishment);
			saveCustomConfig();
		}
		
		public void checkAndPerformPunish(String user) {
			String path = "users." + user + ".points";
			String path_2 = "users." + user + ".punishment";
			if (!customconfig.contains(path)) {
				createUserConfig(user);
				saveCustomConfig();
				return;
			}
			punisher = customconfig.getString(path_2);
			if (punisher.equals("jail")) {
				String command = "jail " + user + " 1h";
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
				saveCustomConfig();
				return;
			}
			if (punisher.equals("tempban")) {
				String command = "ban -e -t 1d " + user;
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
				saveCustomConfig();
				return;
			}
			if (punisher.equals("permaban")) {
				String command = "ban -e " + user;
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
				saveCustomConfig();
				return;
			}
			if (punisher.equals("none")) {
				return;
			}
			return;
		}
		
		@EventHandler
		public void JoinListener(PlayerJoinEvent event) {
			reloadCustomConfig();
			ChatColor GOLD = ChatColor.GOLD;
			ChatColor AQUA = ChatColor.AQUA;
			String user = event.getPlayer().getName();
			checkAndPerformPunish(user);
			String path = "users." + user + ".points";
			int currentpoints = customconfig.getInt(path);
			String pointsname = name;
			String pointsnames = name + "s";
			if (currentpoints > 1 || currentpoints == 0) {
				pointsname = pointsnames;
			}
			event.getPlayer().sendMessage(AQUA + "You currently have: " + currentpoints + " " + GOLD + pointsname);
		}
}

