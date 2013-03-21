package yt.codebukkit.scoreboardapi;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class Helper {
	
	//Helper: here are simple (little!) operations (we need no other .class file)
	
    private final ScoreboardAPI plugin;
    
	//constructor - init plugin
    public Helper(final ScoreboardAPI plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }

        this.plugin = plugin;
    }
    
	//checks if you have a certain permission
	/*public boolean havePermission(Player p, String permission) {
		if (p.hasPermission(permission) || p.isOp()) {
			return true;
		}
		return false;
	}

	public boolean havePermission(Player p, String permission, boolean ignoreOp) {
		if (ignoreOp) {
			return p.hasPermission(permission);
		} else {
			return havePermission(p, permission);
		}
	}*/
	//end permission check
	
	//sender check: console or player
	public boolean isConsole(CommandSender s) {
		return s instanceof ConsoleCommandSender;
	}
	
	//print command help
	public String getHelp(String cmd, String description){
		return ChatColor.GOLD + cmd + ChatColor.RESET + " - " + ChatColor.ITALIC + description;
	}
	
	//print multiline header
	public String getHeader(String head) {
		return "§1[]-------§6" + ScoreboardAPI.NAME + " " + head + "§r§1------- []";
	}
	
	
	
}
