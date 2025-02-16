package pl.com.galaxymc;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.com.galaxymc.heart.HeartCommand;
import pl.com.galaxymc.heart.HeartUseSystem;
import pl.com.galaxymc.heart.LoseHeartOnDeathSystem;
import pl.com.galaxymc.heart.WithdrawHeartsCommand;

import java.util.Objects;

public class SystemLifeSteal extends JavaPlugin implements Listener {
    private HeartUseSystem heartUseSystem;
    private LoseHeartOnDeathSystem loseHeartOnDeathSystem;

    @Override
    public void onEnable() {
        // Inicjalizacja systemów
        this.heartUseSystem = new HeartUseSystem();
        this.loseHeartOnDeathSystem = new LoseHeartOnDeathSystem();

        // Rejestracja listenerów
        Bukkit.getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(heartUseSystem, this);
        getServer().getPluginManager().registerEvents(loseHeartOnDeathSystem, this);

        // Rejestracja komend
        Objects.requireNonNull(getCommand("serce")).setExecutor(new HeartCommand());
        Objects.requireNonNull(getCommand("wyplacserca")).setExecutor(new WithdrawHeartsCommand());

        getLogger().info("SystemLifeSteal Plugin zostal wlaczony!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("§aSiema, §f" + event.getPlayer().getName() + "!"));
    }

}