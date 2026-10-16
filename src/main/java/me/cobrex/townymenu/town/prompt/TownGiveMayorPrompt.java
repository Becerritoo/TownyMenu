package me.cobrex.townymenu.town.prompt;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.cobrex.townymenu.settings.Localization;
import me.cobrex.townymenu.utils.ComponentPrompt;
import me.cobrex.townymenu.utils.MessageUtils;
import me.cobrex.townymenu.utils.TownAccess;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TownGiveMayorPrompt extends ComponentPrompt {

	private final Resident resident;

	public TownGiveMayorPrompt(Resident resident) {
		this.resident = resident;
	}

	@Override
	protected String getPromptMessage(ConversationContext context) {
		return Localization.TownConversables.Mayor.PROMPT.replace("{player}", resident.getName());
	}

	@Override
	public Prompt accept(@NotNull ConversationContext context, String input) {
		Player player = (Player) context.getForWhom();

		Town town = resident.getTownOrNull();
		if (!TownAccess.canModify(player, town, "towny.command.town.set.mayor")) {
			MessageUtils.send(player, Localization.Error.NO_PERMISSION);
			return Prompt.END_OF_CONVERSATION;
		}

		if (input.trim().equalsIgnoreCase(Localization.confirm(player))) {
			if (town == null) {
				MessageUtils.send(player, Localization.Error.NO_TOWN);
				return Prompt.END_OF_CONVERSATION;
			}

			player.performCommand("town set mayor " + resident.getName());
		} else {
			MessageUtils.send(player, Localization.Error.INVALID);
			return this;
		}

		return Prompt.END_OF_CONVERSATION;
	}
}
