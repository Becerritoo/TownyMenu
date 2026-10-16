package me.cobrex.townymenu.nation.prompt;

import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import me.cobrex.townymenu.settings.Localization;
import me.cobrex.townymenu.utils.ComponentPrompt;
import me.cobrex.townymenu.utils.MessageFormatter;
import me.cobrex.townymenu.utils.MessageUtils;
import me.cobrex.townymenu.utils.NationAccess;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NationPlayerTitlePrompt extends ComponentPrompt {

	private final Resident resident;

	public NationPlayerTitlePrompt(Resident resident) {
		this.resident = resident;
	}

	@Override
	protected String getPromptMessage(ConversationContext context) {
		return Localization.NationConversables.Nation_Title.PROMPT
				.replace("{player}", resident.getName());
	}

	@Override
	public Prompt accept(@NotNull ConversationContext context, @NotNull String input) {
		Player player = getPlayer(context);
		String trimmed = input.trim();

		Nation nation = resident.getNationOrNull();
		if (!NationAccess.canModify(player, nation, "towny.command.nation.set.title")
				|| !NationAccess.isResidentOf(nation, resident)) {
			MessageUtils.send(player, MessageFormatter.format(Localization.Error.NO_PERMISSION, player));
			return Prompt.END_OF_CONVERSATION;
		}

		if (trimmed.length() > TownySettings.getMaxTitleLength()) {
			MessageUtils.send(player, Localization.Error.INVALID);
			return this;
		}

		player.performCommand("nation set title " + resident.getName() + " " + trimmed);

		return Prompt.END_OF_CONVERSATION;
	}
}
