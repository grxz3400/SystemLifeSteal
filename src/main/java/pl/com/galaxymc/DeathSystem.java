package pl.com.galaxymc;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.BanList.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DeathSystem implements Listener {

    private static final int BAN_TIME_IN_MINUTES = 30;

    private final HeartSystem heartSystem;

    public DeathSystem(Plugin plugin, HeartSystem heartSystem) {
        this.heartSystem = heartSystem;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        var victimHealth = victim.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (victimHealth == null) {
            return;
        }

        // Zmniejsz maksymalne zdrowie ofiary o 2 (1 serce)
        double newVictimHealth = Math.max(2.0, victimHealth.getBaseValue() - 2.0);
        victimHealth.setBaseValue(newVictimHealth);

        // Sprawdź czy gracz ma 0 serc (2.0 to minimum w Minecraft)
        if (newVictimHealth <= 2.0) {
            handleZeroHearts(victim, victimHealth);
        }

        // Wyślij wiadomości do ofiary
        victim.sendMessage("§c-1 §fserce! Twoje maksymalne zdrowie wynosi teraz: " + newVictimHealth / 2 + " serc");

        if (killer != null) {
            handlePlayerKill(victim, killer);
        }
    }

    private void handleZeroHearts(Player victim, org.bukkit.attribute.AttributeInstance victimHealth) {
        // Zbanuj gracza na BAN_TIME_IN_MINUTES minut
        Date expirationDate = Date.from(Instant.now().plus(BAN_TIME_IN_MINUTES, ChronoUnit.MINUTES));
        Bukkit.getBanList(Type.NAME).addBan(victim.getName(),
                "§cStraciłeś wszystkie serca! Zostałeś zbanowany na " + BAN_TIME_IN_MINUTES + " minut.",
                expirationDate, null);

        // Wyrzuć gracza z serwera
        victim.kick(Component.text("§cStracileś wszystkie serca! Zostałeś zbanowany na " + BAN_TIME_IN_MINUTES + " minut."));

        // Po powrocie gracz dostanie 5 serc (10.0 punktów zdrowia)
        victimHealth.setBaseValue(10.0);
    }

    private void handlePlayerKill(Player victim, Player killer) {
        // Upuść serce na ziemię w miejscu śmierci
        Location deathLocation = victim.getLocation();
        ItemStack heartItem = heartSystem.createHeartItem();
        deathLocation.getWorld().dropItemNaturally(deathLocation, heartItem);

        // Powiadom zabójcę
        killer.sendMessage("§aZabiłeś §f" + victim.getName() + "§a!");
    }
}