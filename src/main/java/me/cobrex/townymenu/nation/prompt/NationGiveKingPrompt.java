package me.cobrex.townymenu.nation.prompt;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import me.cobrex.townymenu.settings.Localization;
import me.cobrex.townymenu.utils.ComponentPrompt;
import me.cobrex.townymenu.utils.MessageUtils;
import me.cobrex.townymenu.utils.NationAccess;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class NationGiveKingPrompt extends ComponentPrompt {

		private final Resident resident;

	public NationGiveKingPrompt(Resident resident) {
		this.resident = resident;
	}

	@Override
	protected String getPromptMessage(ConversationContext context) {
		return Localization.NationConversables.Nation_King.PROMPT.replace("{player}", resident.getName());
	}

	@Override
	public Prompt accept(ConversationContext context, String input) {
		Player player = (Player) context.getForWhom();

		Nation nation = resident.getNationOrNull();
		if (!NationAccess.canModify(player, nation, "towny.command.nation.set.king")
				|| !NationAccess.isResidentOf(nation, resident)) {
			MessageUtils.send(player, Localization.Error.NO_PERMISSION);
			return Prompt.END_OF_CONVERSATION;
		}

		if (input.trim().equalsIgnoreCase(Localization.confirm(player))) {
			player.performCommand("nation set king " + resident.getName());
		} else {
			MessageUtils.send(player, Localization.Error.INVALID);
			return Prompt.END_OF_CONVERSATION;

		}

		return Prompt.END_OF_CONVERSATION;
	}
}
