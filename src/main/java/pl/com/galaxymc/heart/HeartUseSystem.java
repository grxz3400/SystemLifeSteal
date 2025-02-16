package pl.com.galaxymc.heart;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.inventory.ItemStack;

public class HeartUseSystem implements Listener {
    private static final double MAX_HEARTS = 30.0;

    @EventHandler
    public void onPlayerUseHeart(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
            return;
        }

        // Sprawdzamy czy to nasze serce
        ItemStack item = event.getItem();
        if (!HeartItem.isHeart(item)) {
            return;
        }

        // Zapobiegamy normalnemu użyciu przedmiotu
        event.setCancelled(true);

        AttributeInstance maxHealth = event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }

        // Sprawdzamy limit serc
        if (maxHealth.getBaseValue() >= MAX_HEARTS * 2) {
            event.getPlayer().sendMessage("§cOsiągnięto maksymalną liczbę serc!");
            return;
        }

        // Dodajemy 2 maksymalne punkty zdrowia
        maxHealth.setBaseValue(maxHealth.getBaseValue() + 2);

        // Zmniejszamy ilość serc w ręce gracza
        item.setAmount(item.getAmount() - 1);

        event.getPlayer().sendMessage("§aDodano §fserce! Aktualna liczba: " +
                (int)(maxHealth.getBaseValue() / 2));
    }
}