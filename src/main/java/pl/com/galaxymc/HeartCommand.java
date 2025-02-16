package pl.com.galaxymc;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class HeartCommand implements CommandExecutor {
    private final HeartSystem heartSystem;

    public HeartCommand(HeartSystem heartSystem) {
        this.heartSystem = heartSystem;
    }

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

        // Sprawdź czy gracz ma miejsce w ekwipunku
        PlayerInventory inventory = player.getInventory();
        if (inventory.firstEmpty() == -1) {
            player.sendMessage(Component.text("§cTwój ekwipunek jest pełny!"));
            return true;
        }

        // Utwórz serce
        ItemStack heartItem = heartSystem.createHeartItem();

        // Dodaj serce do ekwipunku
        inventory.addItem(heartItem);
        player.sendMessage(Component.text("§aDodano serce do twojego ekwipunku!"));

        return true;
    }
}