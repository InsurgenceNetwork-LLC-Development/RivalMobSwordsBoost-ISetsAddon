package com.insurgencedev.rivalsworsdboost.listeners;

import me.rivaldev.mobsword.rivalmobswords.api.SwordEssenceReceivePreEnchantEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.insurgencedev.insurgencesets.api.ISetsAPI;
import org.insurgencedev.insurgencesets.api.contracts.IArmorSet;
import org.insurgencedev.insurgencesets.api.contracts.IPlayer;
import org.insurgencedev.insurgencesets.data.ArmorSetData;
import org.insurgencedev.insurgencesets.libs.fo.remain.nbt.NBTItem;
import org.insurgencedev.insurgencesets.models.upgrade.Boost;
import org.insurgencedev.insurgencesets.models.upgrade.Upgrade;

public final class RivalSwordsEventListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onReceive(SwordEssenceReceivePreEnchantEvent event) {
        Player player = event.getPlayer();
        IPlayer cache = ISetsAPI.getCache(player);
        double totalAmount = 0;

        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null || item.getType().isAir()) {
                continue;
            }

            NBTItem nbtItem = new NBTItem(item);
            if (!nbtItem.hasTag("armorSet")) {
                continue;
            }

            String armorSetName = nbtItem.getString("armorSet");
            if (!ISetsAPI.getArmorSetManager().isArmorSetValid(armorSetName)) {
                continue;
            }

            String itemType = item.getType().name().split("_")[1];
            ArmorSetData data = cache.getArmorSetDataManager().getArmorSetData(armorSetName);
            if (data == null) {
                continue;
            }

            IArmorSet armorSet = ISetsAPI.getArmorSetManager().getArmorSet(armorSetName);
            Upgrade upgrade = armorSet != null ? armorSet.getArmorPieceManager().findPieceLevels(itemType, getLevel(itemType, data)) : null;
            if (upgrade == null) {
                continue;
            }

            for (Boost boost : upgrade.getBoosts()) {
                if (boost.getNamespace().equals("RIVAL_SWORDS") && boost.getType().equals("Essence")) {
                    double boostAmount = boost.getBOOST_SETTINGS().getDouble("Boost_Amount");
                    totalAmount += calcAmount(event.getEssence(), boost.isPercent(), boostAmount);
                }
            }
        }

        if (totalAmount > 0) {
            event.setEssence(totalAmount);
        }
    }

    private double calcAmount(double amountFromEvent, boolean isPercent, double boostAmount) {
        if (isPercent) {
            return amountFromEvent * (1 + boostAmount / 100);
        } else {
            return amountFromEvent * (boostAmount < 1 ? 1 + boostAmount : boostAmount);
        }
    }

    private int getLevel(String type, ArmorSetData armorSetData) {
        return switch (type) {
            case "HEAD", "HELMET" -> armorSetData.getHelmetLevels();
            case "CHESTPLATE" -> armorSetData.getChestplateLevels();
            case "LEGGINGS" -> armorSetData.getLeggingsLevels();
            case "BOOTS" -> armorSetData.getBootsLevels();
            default -> 0;
        };
    }
}
