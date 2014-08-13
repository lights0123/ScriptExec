package com.lights0123.ScriptExec;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
public final class ScriptExec extends JavaPlugin{
	public static boolean hasPerms=false;
	public static Permission perms;
	@Override
	public void onEnable(){
		this.saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
		if(!setupPermissions()){
			if(getConfig().getBoolean("require-vault")){
				getLogger().severe("Vault not found. Shutting down...");
				getServer().getPluginManager().disablePlugin(this);
	            return;
			}else{
				getLogger().severe("Vault not found. All players will have permission to execute scripts!");
			}
			hasPerms=false;
		}else{
			hasPerms=true;
		}
		this.getCommand("se").setExecutor(new SeCommand(this));
	}
	public void onDisable(){
		String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion());
	}
    private boolean setupPermissions() {
    	if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }
        perms = rsp.getProvider();
        return perms != null;
    }
}