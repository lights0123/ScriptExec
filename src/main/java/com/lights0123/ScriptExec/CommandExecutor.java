package com.lights0123.ScriptExec;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExecutor implements org.bukkit.command.CommandExecutor{
	private final ScriptExec plugin;
	 
	public CommandExecutor(ScriptExec plugin) {
		this.plugin = plugin;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("scriptexec")) {
			if(args.length==0){
				if(perm(sender,"ScriptExec.info")){
					sender.sendMessage(ChatColor.GOLD+"ScriptExec - a plugin by lights0123 to execute\nconsole commands on the server by in-game commands.\nView more information at "+ChatColor.BLUE+ChatColor.UNDERLINE+"http://dev.bukkit.org/bukkit-plugins/scriptexec"+ChatColor.GOLD+"\n/se reload: reload the configuration.\n/se execute <name>: execute a script.");
				}else{
					sender.sendMessage(ChatColor.RED+"Sorry, but you do not have permission!");
				}
				return true;
			}else if(args.length==1&&args[0].equals("reload")){
				if(perm(sender,"ScriptExec.reload")){
					plugin.reloadConfig();
					sender.sendMessage(ChatColor.GREEN+"Reloaded!");
				}else{
					sender.sendMessage(ChatColor.RED+"Sorry, but you do not have permission!");
				}
				return true;
			}else if(args.length>1&&args[0].equals("execute")){
				if(perm(sender,"ScriptExec.execute.*")||perm(sender,"ScriptExec.info"+args[1])){
					String path=plugin.getConfig().getString("scripts."+args[1]+".path");
					if(!(path==null)){
						boolean params=args.length>2;
						if(params&&!(perm(sender,"ScriptExec.execute.*.params")&&!perm(sender,"ScriptExec.execute."+args[1]+".params"))){
							sender.sendMessage("You do not have permission to use parameters for the command\n"+plugin.getConfig().getString("scripts."+args[1]+".path"));
							return true;
						}
						Runtime r = Runtime.getRuntime();
						boolean success=true;
						if(params){
							for(int i=2;i<=args.length-1;i++){
								path+=" "+args[i];
								if(args[i].equals("|")||args[i].equals("&")||args[i].equals("&&")||args[i].equals("||")||args[i].equals(">")||args[i].equals(">>")){
									sender.sendMessage(ChatColor.RED+"Sorry, but you cannot run multiple commands or output into a file.");
									return true;
								}
								if(args[i].contains(" ")){
									sender.sendMessage(ChatColor.RED+"Sorry, but you cannot put commands in quotes.");
									return true;
								}
							}
						}
						try {
							r.exec(path);
						} catch (IOException e) {
							e.printStackTrace();
							success=false;
						}
						if(success){
							sender.sendMessage(ChatColor.GREEN+"Success!");
						}else{
							sender.sendMessage(ChatColor.RED+"Uh oh. Something bad happened. Maybe the command/script\n"+plugin.getConfig().getString("scripts."+args[1]+".path")+" does not exist.");
						}
					}else{
						sender.sendMessage(ChatColor.RED+"Uh oh. That script is not found! If you added it, run /scriptexec reload.");
					}
				}else{
					sender.sendMessage(ChatColor.RED+"Sorry, but you do not have permission!");
				}
				return true;
			}else{
				sender.sendMessage(ChatColor.RED+"Sorry, but that is a non-existant command. Available Commands:"+ChatColor.GREEN+"\n/se reload: reload the configuration.\n/se execute <name>: execute a script.");
				return true;
			}
		}
		return true;
	}
	private boolean perm(CommandSender player,String perm){
		if(!(player instanceof Player)||!ScriptExec.hasPerms){
			return true;
		}
		Player fplayer = (Player) player;
		if(ScriptExec.perms.has(fplayer,perm)){
			return true;
		}else{
			if(ScriptExec.perms.has(fplayer,"ScriptExec.*")){
				return true;
			}
		}
		return false;
	}
}