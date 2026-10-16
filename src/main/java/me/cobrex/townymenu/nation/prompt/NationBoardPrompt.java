package me.cobrex.townymenu.nation.prompt;

import com.palmergames.bukkit.towny.object.Nation;
import me.cobrex.townymenu.settings.Localization;
import me.cobrex.townymenu.utils.ComponentPrompt;
import me.cobrex.townymenu.utils.MessageUtils;
import me.cobrex.townymenu.utils.NationAccess;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class NationBoardPrompt extends ComponentPrompt {

	private final Nation nation;

	public NationBoardPrompt(Nation nation) {
		this.nation = nation;
	}

	@Override
	protected String getPromptMessage(ConversationContext context) {
		return Localization.NationConversables.Nation_Board.PROMPT;
	}

	@Override
	public Prompt accept(ConversationContext context, String input) {
		Player player = (Player) context.getForWhom();
		String trimmed = input.trim();

		if (!NationAccess.canModify(player, nation, "towny.command.nation.set.board")) {
			MessageUtils.send(player, Localization.Error.NO_PERMISSION);
			return Prompt.END_OF_CONVERSATION;
		}

		player.performCommand("nation set board " + input);
		MessageUtils.send(player, Localization.NationConversables.Nation_Board.RESPONSE);
		return Prompt.END_OF_CONVERSATION;
	}
}
