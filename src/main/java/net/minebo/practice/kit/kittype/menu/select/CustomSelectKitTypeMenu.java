package net.minebo.practice.kit.kittype.menu.select;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.util.Callback;
import net.minebo.practice.util.InventoryUtils;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Similar to {@link SelectKitTypeMenu} but allows the user to set custom
 * descriptions/item counts for each KitType. For example, this is used by
 * the queue system to show the number of players in each queue prior to joining.
 */
public final class CustomSelectKitTypeMenu extends Menu {

    private final Callback<KitType> callback;
    private final Function<KitType, CustomKitTypeMeta> metaFunc;
    private final boolean ranked;
    private final String title;

    public CustomSelectKitTypeMenu(Callback<KitType> callback, Function<KitType, CustomKitTypeMeta> metaFunc, String title, boolean ranked) {
        setAutoUpdate(true);

        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.metaFunc = Preconditions.checkNotNull(metaFunc, "metaFunc");
        this.ranked = ranked;
        this.title = title;
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryDelayed(player);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + title;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int x = 1;
        int y = 0;

        for (KitType kitType : KitType.getAllTypes()) {
            if (kitType.isHidden()) {
                continue;
            }
            if (ranked && !kitType.isSupportsRanked()) {
                continue;
            }
            CustomKitTypeMeta meta = metaFunc.apply(kitType);
            buttons.put(getSlot(x++, y), new KitTypeButton(kitType, callback, meta.getDescription(), meta.getQuantity()));
            if (x == 8) {
                y++;
                x = 1;
            }
        }

        return buttons;
    }

    @Getter
    @RequiredArgsConstructor
    public static final class CustomKitTypeMeta {

        private final int quantity;
        private final List<String> description;

    }

}