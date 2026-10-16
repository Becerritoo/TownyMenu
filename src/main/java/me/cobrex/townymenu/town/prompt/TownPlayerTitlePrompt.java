package me.cobrex.townymenu.town.prompt;


import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.TownySettings;
import me.cobrex.townymenu.settings.Localization;
import me.cobrex.townymenu.utils.ComponentPrompt;
import me.cobrex.townymenu.utils.MessageFormatter;
import me.cobrex.townymenu.utils.MessageUtils;
import me.cobrex.townymenu.utils.TownAccess;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TownPlayerTitlePrompt extends ComponentPrompt {

	private final Resident resident;

	public TownPlayerTitlePrompt(Resident resident) {
		this.resident = resident;
	}

	@Override
	protected String getPromptMessage(ConversationContext context) {
		return Localization.TownConversables.Title.PROMPT
				.replace("{player}", resident.getName());
	}

	@Override
	public Prompt accept(@NotNull ConversationContext context, @NotNull String input) {
		Player player = getPlayer(context);
		String trimmed = input.trim();

		if (!TownAccess.canModify(player, resident.getTownOrNull(), "towny.command.town.set.title")) {
			MessageUtils.send(player, MessageFormatter.format(Localization.Error.NO_PERMISSION, player));
			return Prompt.END_OF_CONVERSATION;
		}

		if (trimmed.length() > TownySettings.getMaxTitleLength()) {
			MessageUtils.send(player, Localization.Error.INVALID);
			return this;
		}

		player.performCommand("town set title " + resident.getName() + " " + trimmed);

		return Prompt.END_OF_CONVERSATION;
	}
}
