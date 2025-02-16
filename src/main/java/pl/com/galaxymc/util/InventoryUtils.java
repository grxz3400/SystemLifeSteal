package pl.com.galaxymc.util;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

public class InventoryUtils {

    /**
     * Sprawdza czy w ekwipunku jest miejsce na określoną ilość przedmiotów,
     * uwzględniając istniejące stacki tego samego przedmiotu
     */
    public static boolean  canAddItems(Player player, ItemStack itemToAdd, int amount, NamespacedKey key) {
        PlayerInventory inventory = player.getInventory();
        int remainingAmount = amount;

        // Najpierw sprawdź istniejące stacki tego samego przedmiotu
        for (ItemStack item : inventory.getStorageContents()) {
            if (item == null) continue;

            if (isSameCustomItem(item, itemToAdd, key)) {
                int spaceInStack = item.getMaxStackSize() - item.getAmount();
                if (spaceInStack > 0) {
                    remainingAmount -= spaceInStack;
                    if (remainingAmount <= 0) {
                        return true;
                    }
                }
            }
        }

        // Policz puste sloty
        int emptySlots = 0;
        for (ItemStack item : inventory.getStorageContents()) {
            if (item == null) {
                emptySlots++;
            }
        }

        // Oblicz ile pełnych stacków potrzebujemy
        int maxStackSize = itemToAdd.getMaxStackSize();
        int slotsNeeded = (int) Math.ceil((double) remainingAmount / maxStackSize);

        return emptySlots >= slotsNeeded;
    }

    /**
     * Dodaje określoną ilość przedmiotów do ekwipunku, łącząc je z istniejącymi stackami
     */
    public static void addItems(Player player, ItemStack itemToAdd, int amount, NamespacedKey key) {
        PlayerInventory inventory = player.getInventory();
        int remainingAmount = amount;
        ItemStack[] contents = inventory.getStorageContents();

        // Najpierw próbuj dodać do istniejących stacków
        for (int i = 0; i < contents.length && remainingAmount > 0; i++) {
            ItemStack item = contents[i];
            if (item == null) continue;

            if (isSameCustomItem(item, itemToAdd, key)) {
                int spaceInStack = item.getMaxStackSize() - item.getAmount();
                if (spaceInStack > 0) {
                    int amountToAdd = Math.min(spaceInStack, remainingAmount);
                    item.setAmount(item.getAmount() + amountToAdd);
                    remainingAmount -= amountToAdd;
                }
            }
        }

        // Jeśli coś zostało, dodaj nowe stacki
        while (remainingAmount > 0) {
            int stackSize = Math.min(remainingAmount, itemToAdd.getMaxStackSize());
            ItemStack newStack = itemToAdd.clone();
            newStack.setAmount(stackSize);
            inventory.addItem(newStack);
            remainingAmount -= stackSize;
        }
    }

    /**
     * Sprawdza czy dwa przedmioty mają ten sam customowy klucz
     */
    private static boolean isSameCustomItem(ItemStack item1, ItemStack item2, NamespacedKey key) {
        if (!item1.hasItemMeta() || !item2.hasItemMeta()) return false;

        return item1.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE) &&
                item2.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }
}