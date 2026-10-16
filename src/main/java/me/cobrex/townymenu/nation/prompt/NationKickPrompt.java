package me.cobrex.townymenu.nation.prompt;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.TownyAPI;
import me.cobrex.townymenu.settings.Localization;
import me.cobrex.townymenu.utils.ComponentPrompt;
import me.cobrex.townymenu.utils.MessageUtils;
import me.cobrex.townymenu.utils.NationAccess;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class NationKickPrompt extends ComponentPrompt {

	private final Town town;

	public NationKickPrompt(Town town) {
		this.town = town;
	}

	@Override
	protected String getPromptMessage(ConversationContext context) {
		return Localization.NationConversables.Nation_Kick.PROMPT.replace("{town}", town.getName());
	}

	@Override
	public Prompt accept(ConversationContext context, String input) {
		Player player = (Player) context.getForWhom();
		String lowerInput = input.trim();
		Nation nation = TownyAPI.getInstance().getNation(player);

		if (!NationAccess.canModify(player, nation, "towny.command.nation.kick")
				|| nation == null || !nation.equals(town.getNationOrNull())) {
			MessageUtils.send(player, Localization.Error.NO_PERMISSION);
			return Prompt.END_OF_CONVERSATION;
		}

		if (lowerInput.equalsIgnoreCase(Localization.confirm(player))) {
			player.performCommand("nation kick " + town.getName());
		} else {
			MessageUtils.send(player, Localization.Error.INVALID);
//			MessageUtils.send(player, MessageFormatter.format(Localization.Error.INVALID, player));
			return Prompt.END_OF_CONVERSATION;
		}

		return Prompt.END_OF_CONVERSATION;
	}
}
