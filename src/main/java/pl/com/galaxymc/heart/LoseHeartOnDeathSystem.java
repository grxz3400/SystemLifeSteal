package pl.com.galaxymc.heart;

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

public class LoseHeartOnDeathSystem implements Listener {

    private static final int BAN_TIME_IN_MINUTES = 30;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        var victimHealth = victim.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (victimHealth == null) {
            return;
        }

        // Policz nową liczbę serc
        double newVictimHealth = victimHealth.getBaseValue() - 2;

        // Sprawdź czy gracz miałby 0 serc, jeśli tak to zbanuj
        if (newVictimHealth < 2) {
            handleZeroHearts(victim, victimHealth);
        } else {
            // Zmniejsz maksymalne zdrowie ofiary o 2 (1 serce)
            victimHealth.setBaseValue(newVictimHealth);
            // Wyślij wiadomości do ofiary
            victim.sendMessage("§c-1 §fserce! Twoje maksymalne zdrowie wynosi teraz: " + (int) (newVictimHealth / 2) + " serc");
        }

        // Dodaj serce zabójcy
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
        victim.kick(Component.text("§cStraciłeś wszystkie serca! Zostałeś zbanowany na " + BAN_TIME_IN_MINUTES + " minut."));

        // Po powrocie gracz dostanie 5 serc (10.0 punktów zdrowia)
        victimHealth.setBaseValue(10.0);
    }

    private void handlePlayerKill(Player victim, Player killer) {
        // Upuść serce na ziemię w miejscu śmierci
        Location deathLocation = victim.getLocation();
        ItemStack heartItem = HeartItem.createHeartItem();
        deathLocation.getWorld().dropItemNaturally(deathLocation, heartItem);

        // Powiadom zabójcę
        killer.sendMessage("§aZabiłeś §f" + victim.getName() + "§a!");
    }
}