package net.minebo.practice.kit.menu.kits;

import net.minebo.practice.Practice;
import net.minebo.practice.kit.Kit;
import net.minebo.practice.kit.KitHandler;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.kit.kittype.menu.select.SelectKitTypeMenu;
import net.minebo.practice.util.InventoryUtils;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.menu.Menu;

import net.minebo.practice.util.menu.buttons.BackButton;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class KitsMenu extends Menu {

    private final KitType kitType;

    public KitsMenu(KitType kitType) {
        setPlaceholder(true);
        setAutoUpdate(true);

        this.kitType = kitType;
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryDelayed(player);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.RED + "Viewing " + kitType.getDisplayName() + " kits";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        KitHandler kitHandler = Practice.getInstance().getKitHandler();
        Map<Integer, Button> buttons = new HashMap<>();

        // kit slots are 1-indexed
        for (int kitSlot = 1; kitSlot <= KitHandler.KITS_PER_TYPE; kitSlot++) {
            Optional<Kit> kitOpt = kitHandler.getKit(player, kitType, kitSlot);
            int column = kitSlot + 1; // + 1 to compensate for this being 0-indexed

            if (kitOpt.isPresent()) {
                buttons.put(getSlot(column, 1), new KitEditButton(kitOpt, kitType, kitSlot));
            } else {
                buttons.put(getSlot(column, 1), new KitIconButton(kitOpt, kitType, kitSlot));
            }
        }

        buttons.put(getSlot(8, 2), new BackButton(new SelectKitTypeMenu(kitType -> new KitsMenu(kitType).openMenu(player), "Select a kit type...")));

        return buttons;
    }


    @Override
    public int size(Player player) {
        return 9 * 3;
    }

}