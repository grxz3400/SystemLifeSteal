package pl.com.galaxymc;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class HeartSystem implements Listener {
    private static final double MAX_HEARTS = 30.0;

    private final NamespacedKey heartKey;

    public HeartSystem(Plugin plugin) {
        this.heartKey = new NamespacedKey(plugin, "serce");
    }

    public ItemStack createHeartItem() {
        ItemStack heart = new ItemStack(Material.RED_DYE, 1);
        ItemMeta meta = heart.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("§cDodatkowe serce"));
            List<Component> lore = Arrays.asList(
                    Component.text("§7Kliknij PPM aby dodać sobie §aserce!"),
                    Component.text("§7Limit: 30 serc")
            );
            meta.lore(lore);

            meta.getPersistentDataContainer().set(heartKey, PersistentDataType.BYTE, (byte)1);
            heart.setItemMeta(meta);
        }
        return heart;
    }

    @EventHandler
    public void onPlayerUseHeart(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        // Sprawdzamy czy to nasze serce
        if (!item.getItemMeta().getPersistentDataContainer().has(heartKey, PersistentDataType.BYTE)) {
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

        // Dodajemy serce (2 punkty zdrowia)
        maxHealth.setBaseValue(maxHealth.getBaseValue() + 2);

        // Zmniejszamy ilość serc w ręce gracza
        item.setAmount(item.getAmount() - 1);

        event.getPlayer().sendMessage("§aDodano §fserce! Aktualna liczba: " +
                (int)(maxHealth.getBaseValue() / 2));
    }
}