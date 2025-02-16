package pl.com.galaxymc;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SystemLifeSteal extends JavaPlugin implements Listener {
    private HeartSystem heartSystem;
    private DeathSystem deathSystem;

    @Override
    public void onEnable() {
        // Inicjalizacja systemów
        this.heartSystem = new HeartSystem(this);
        this.deathSystem = new DeathSystem(this, heartSystem);

        // Rejestracja listenerów
        Bukkit.getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(heartSystem, this);
        getServer().getPluginManager().registerEvents(deathSystem, this);

        // Rejestracja komendy
        Objects.requireNonNull(getCommand("serce")).setExecutor(new HeartCommand(heartSystem));

        getLogger().info("SystemLifeSteal Plugin zostal wlaczony!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("§aSiema, §f" + event.getPlayer().getName() + "!"));
    }

}