package net.minebo.practice.util.menu.pagination;

import net.minebo.practice.util.menu.buttons.BackButton;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.menu.Menu;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ViewAllPagesMenu extends Menu {

    @NonNull @Getter PaginatedMenu menu;


    @Override
    public String getTitle(Player player) {
        return "Jump to page";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        HashMap<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new BackButton(menu));

        int index = 10;


        for (int i = 1; i <= menu.getPages(player); i++) {
            buttons.put(index++, new JumpToPageButton(i,this.menu));

            if ((index - 8) % 9 == 0) {
                index += 2;
            }

        }


        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

}
