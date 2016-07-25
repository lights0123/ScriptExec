package com.lights0123.ScriptExec;
import java.io.IOException;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;

public final class ScriptExec extends JavaPlugin {
	static boolean hasPerms = false;
	static Permission perms;
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
		if (!setupPermissions()) {
			if (getConfig().getBoolean("require-vault")) {
				getLogger().severe("Vault not found. Shutting down...");
				getServer().getPluginManager().disablePlugin(this);
				return;
			} else {
				getLogger().warning("Vault not found. All players will have permission to execute scripts!");
			}
			hasPerms = false;
		} else {
			hasPerms = true;
		}
		try {
			Metrics metrics = new Metrics(this);
			Graph VersionDemographics = metrics.createGraph("Version Demographics");
			VersionDemographics.addPlotter(new Metrics.Plotter(getDescription().getVersion()) {

				@Override
				public int getValue() {
					return 1;
				}

			});
			metrics.start();
		} catch (IOException e) {
			//Metrics not available :-(
		}
		this.getCommand("se").setExecutor(new CommandExecutor(this));
	}

	public void onDisable() {
		getLogger().info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
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