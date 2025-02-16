package pl.com.galaxymc.heart;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.inventory.ItemStack;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import pl.com.galaxymc.util.InventoryUtils;

public class WithdrawHeartsCommand implements CommandExecutor {
    private static final double MIN_HEARTS = 10.0;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cTa komenda może być użyta tylko przez gracza!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§cUżycie: /wyplacserca <liczba>");
            return true;
        }

        int heartsToWithdraw;
        try {
            heartsToWithdraw = Integer.parseInt(args[0]);
            if (heartsToWithdraw <= 0) {
                player.sendMessage("§cLiczba serc musi być większa od 0!");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§cPodaj prawidłową liczbę serc!");
            return true;
        }

        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth == null) {
            return true;
        }

        // Sprawdź czy gracz ma wystarczająco serc
        double currentHearts = maxHealth.getBaseValue() / 2;
        if (currentHearts - heartsToWithdraw < MIN_HEARTS) {
            player.sendMessage("§cNie możesz wypłacić tylu serc! Musisz zachować minimum " + MIN_HEARTS + " serc.");
            return true;
        }

        // Utwórz przedmiot serca do sprawdzenia miejsca
        ItemStack heartItem = HeartItem.createHeartItem();

        // Sprawdź czy jest miejsce w ekwipunku
        if (!InventoryUtils.canAddItems(player, heartItem, heartsToWithdraw, HeartItem.KEY)) {
            player.sendMessage("§cNie masz wystarczająco miejsca w ekwipunku!");
            return true;
        }

        // Wypłać serca
        maxHealth.setBaseValue(maxHealth.getBaseValue() - (heartsToWithdraw * 2));

        // Dodaj serca do ekwipunku
        InventoryUtils.addItems(player, heartItem, heartsToWithdraw, HeartItem.KEY);

        player.sendMessage("§aWypłacono §f" + heartsToWithdraw + " §aserc! Pozostało ci §f" +
                (int)(maxHealth.getBaseValue() / 2) + " §aserc.");

        return true;
    }
}