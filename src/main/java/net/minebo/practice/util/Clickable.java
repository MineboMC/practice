package net.minebo.practice.util;

import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class Clickable {

	private final List<TextComponent> components = new ArrayList<>();

	public Clickable(String msg) {
		TextComponent message = new TextComponent(msg);

		this.components.add(message);
	}

	public Clickable(String msg, String hoverMsg, String clickString) {
		this.add(ChatColor.translate(msg), ChatColor.translate(hoverMsg), clickString);
	}

	public TextComponent add(String msg, String hoverMsg, String clickString) {
		TextComponent message = new TextComponent(ChatColor.translate(msg));

		if (hoverMsg != null) {
			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translate(hoverMsg)).create()));
		}

		if (clickString != null) {
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
		}

		this.components.add(message);

		return message;
	}

	public void add(String message) {
		this.components.add(new TextComponent(ChatColor.translate(message)));
	}

	public void sendToPlayer(Player player) {
		player.sendMessage(this.asComponents());
	}

	public TextComponent[] asComponents() {
		return this.components.toArray(new TextComponent[0]);
	}
}
