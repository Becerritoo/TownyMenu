package me.cobrex.townymenu.town.prompt;

import com.palmergames.bukkit.towny.object.Resident;
import me.cobrex.townymenu.settings.Localization;
import me.cobrex.townymenu.utils.ComponentPrompt;
import me.cobrex.townymenu.utils.MessageUtils;
import me.cobrex.townymenu.utils.TownAccess;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TownKickPrompt extends ComponentPrompt {

	private final Resident resident;

	public TownKickPrompt(Resident resident) {
		this.resident = resident;
	}

	@Override
	protected String getPromptMessage(ConversationContext context) {
		return Localization.TownConversables.Kick.PROMPT.replace("{player}", resident.getName());
	}

	@Override
	public Prompt accept(@NotNull ConversationContext context, @NotNull String input) {
		Player player = (Player) context.getForWhom();
		String confirmation = input.trim();

		if (!TownAccess.canModify(player, resident.getTownOrNull(), "towny.command.town.kick")) {
			MessageUtils.send(player, Localization.Error.NO_PERMISSION);
//			MessageUtils.send(player, MessageFormatter.format(Localization.Error.NO_PERMISSION, player));
			return Prompt.END_OF_CONVERSATION;
		}

		if (resident.isMayor()) {
			MessageUtils.send(player, Localization.TownConversables.Kick.MAYOR);
//			MessageUtils.send(player, MessageFormatter.format(Localization.TownConversables.Kick.MAYOR, player));
			return Prompt.END_OF_CONVERSATION;
		}

		if (confirmation.equalsIgnoreCase(Localization.confirm(player))) {
			player.performCommand("town kick " + resident.getName());
		} else {
			MessageUtils.send(player, Localization.Error.INVALID);
//			MessageUtils.send(player, MessageFormatter.format(Localization.Error.INVALID, player));
			return Prompt.END_OF_CONVERSATION;
		}

		return Prompt.END_OF_CONVERSATION;
	}
}
