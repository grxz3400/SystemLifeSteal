package pl.com.galaxymc.heart;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import pl.com.galaxymc.util.InventoryUtils;

// Obsługa komendy /heart
public class HeartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("§cTa komenda może być użyta tylko przez gracza!"));
            return true;
        }

        // Sprawdź uprawnienia
        if (!player.hasPermission("lifesteal.serce")) {
            player.sendMessage(Component.text("§cNie masz uprawnień do użycia tej komendy!"));
            return true;
        }

        // Utwórz serce i sprawdź czy gracz ma miejsce w ekwipunku
        ItemStack heartItem = HeartItem.createHeartItem();

        if (!InventoryUtils.canAddItems(player, heartItem, 1, HeartItem.KEY)) {
            player.sendMessage("§cNie masz wystarczająco miejsca w ekwipunku!");
            return true;
        }

        // Dodaj serce do ekwipunku
        InventoryUtils.addItems(player, heartItem, 1, HeartItem.KEY);
        player.sendMessage(Component.text("§aDodano serce do twojego ekwipunku!"));
        return true;
    }
}