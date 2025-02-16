package pl.com.galaxymc.heart;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.com.galaxymc.util.Constants;

import java.util.Arrays;
import java.util.List;

public class HeartItem {

    public static NamespacedKey KEY = new NamespacedKey(Constants.PLUGIN_NAME, "serce");

    public static ItemStack createHeartItem() {
        ItemStack heart = new ItemStack(Material.RED_DYE, 1);
        ItemMeta meta = heart.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("§cDodatkowe serce"));
            List<Component> lore = Arrays.asList(
                    Component.text("§7Kliknij PPM aby dodać sobie §aserce!"),
                    Component.text("§7Limit: 30 serc")
            );
            meta.lore(lore);

            meta.getPersistentDataContainer().set(KEY, PersistentDataType.BYTE, (byte)1);
            heart.setItemMeta(meta);
        }
        return heart;
    }

    public static boolean isHeart(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        return item.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.BYTE);
    }
}
