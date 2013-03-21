package yt.codebukkit.scoreboardapi;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ScoreboardAPI extends JavaPlugin {
	
	//Plugin Definitions
	public static String NAME;
	public static String VERSION;
	public static String AUTHORS;
	public static String CMD_PREFIX;
	public static String PREFIX;
	public static String NO_PERMISSIONS;
	public static String FOLDER;
	public static String CONFIG_FILE;
	//End
	
	//Global Definitions:
	public static String PREFIX_COLOR = ChatColor.AQUA + "";
	public static String CONFIG_NAME = "config.yml";
	public static boolean ENABLE_METRICS = false;
	//End

    private List<Scoreboard> scoreboards = new LinkedList<Scoreboard>();

    public List<Scoreboard> getScoreboards() {
        return scoreboards;
    }

	Format format = new Format(this);
	Helper helper = new Helper(this);
	//End

    public static ScoreboardAPI getInstance() {
        Plugin pl = Bukkit.getPluginManager().getPlugin("ScoreboardAPI");
        if (pl == null) {
            return null;
        }
        if (!(pl instanceof ScoreboardAPI)) {
            return null;
        }
        return (ScoreboardAPI) pl;
    }

    public Scoreboard createScoreboard(String name, int priority) {
        for (Scoreboard s : scoreboards) {
            if (s.getName() == name) {
                return null;
            }
        }
        Scoreboard s = new Scoreboard(name, priority, this);
        scoreboards.add(s);
        return s;
    }

    public Scoreboard getScoreboard(String name) {
        for (Scoreboard s : scoreboards) {
            if (s.getName() == name) {
                return s;
            }
        }
        return null;
    }

    public void updateForPlayer(Player p) {
        for (Scoreboard s : scoreboards) {
            s.checkIfNeedsToBeDisabledForPlayer(p);
            s.checkIfNeedsToBeEnabledForPlayer(p);
        }
    }

    public boolean isPlayerReceivingScoreboard(Player p) {
        for (Scoreboard s: scoreboards) {
            if (s.hasPlayerAdded(p)) {
                return true;
            }
        }
        return false;
    }

    public void updateForAllPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            updateForPlayer(p);
        }
    }

	//initialize all static constants
	private void initialize() {
		NAME = getDescription().getName();
		VERSION = getDescription().getVersion();
		AUTHORS = "";
		for(String s:getDescription().getAuthors())
			AUTHORS = AUTHORS + s + ", ";
		CMD_PREFIX = "[" + NAME + "] ";
		PREFIX = "[" + PREFIX_COLOR + NAME + ChatColor.RESET + "] ";
		NO_PERMISSIONS = PREFIX + ChatColor.DARK_RED + "You don't have permissions!";
		FOLDER = "plugins/" + NAME;
		CONFIG_FILE = FOLDER + "/" + CONFIG_NAME;
	}

	//Event: on plugin startup
	@Override
	public void onEnable() {
		initialize();
//		runMetrics();
	}
	
	//Event on plugin shutdown (or reload!)
	@Override
	public void onDisable() {
	}
	
//	public void runMetrics() {
//		if (ENABLE_METRICS == true) {
//			try {
//			    metrics metrics = new metrics(this);
//			    metrics.start();
//			} catch (IOException e) {
//			    // Failed to submit the stats :-(
//			}
//		}
//	}
	
}
