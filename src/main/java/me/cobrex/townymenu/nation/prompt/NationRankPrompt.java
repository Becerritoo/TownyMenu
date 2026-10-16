package me.cobrex.townymenu.nation.prompt;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.permissions.TownyPerms;
import me.cobrex.townymenu.settings.Localization;
import me.cobrex.townymenu.utils.ComponentPrompt;
import me.cobrex.townymenu.utils.MessageUtils;
import me.cobrex.townymenu.utils.NationAccess;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.Locale;

public class NationRankPrompt extends ComponentPrompt {

	private final Resident resident;

	public NationRankPrompt(Resident resident) {
		this.resident = resident;
	}

	@Override
	protected String getPromptMessage(ConversationContext context) {
		String ranks = String.join(", ", TownyPerms.getNationRanks());
		String current = resident.getNationRanks().isEmpty()
				? "none"
				: String.join(", ", resident.getNationRanks());
		return Localization.NationConversables.Nation_Rank.PROMPT
				.replace("{player}", resident.getName())
				.replace("{ranks}", ranks)
				.replace("{current}", current);
	}

	@Override
	public Prompt accept(ConversationContext context, String input) {
		Player player = (Player) context.getForWhom();

		String trimmed = input.trim();
		String localizedRemove = Localization.NationConversables.Nation_Rank.REMOVE;
		boolean removing = trimmed.toLowerCase(Locale.ROOT).startsWith("remove ")
				|| localizedRemove != null && trimmed.toLowerCase(Locale.ROOT)
				.startsWith(localizedRemove.toLowerCase(Locale.ROOT) + " ");
		String rank = removing ? trimmed.substring(trimmed.indexOf(' ') + 1).trim() : trimmed;
		Nation nation = resident.getNationOrNull();
		String permission = "towny.command.nation.rank." + rank.toLowerCase(Locale.ROOT);

		if (!NationAccess.canModify(player, nation, permission)
				|| !NationAccess.isResidentOf(nation, resident)) {
			MessageUtils.send(player, Localization.Error.NO_PERMISSION);
			return END_OF_CONVERSATION;
		}

		if (!isValidRank(rank)
				|| removing && !resident.hasNationRank(rank)
				|| !removing && resident.hasNationRank(rank)) {
			String ranks = String.join(", ", TownyPerms.getNationRanks());
			MessageUtils.send(player, Localization.NationConversables.Nation_Rank.INVALID.replace("{ranks}", ranks));
			return this;
		}

		player.performCommand("nation rank " + (removing ? "remove " : "add ")
				+ resident.getName() + " " + rank);
		if (removing) {
			MessageUtils.send(player, Localization.NationConversables.Nation_Rank.REMOVED_ALL
					.replace("{player}", resident.getName()).replace("{input}", rank));
		} else {
			MessageUtils.send(player, Localization.NationConversables.Nation_Rank.RESPONSE
					.replace("{player}", resident.getName())
					.replace("{input}", rank));
		}
		return END_OF_CONVERSATION;
	}

	private boolean isValidRank(String input) {
		return TownyPerms.getNationRanks().stream()
				.anyMatch(rank -> rank.equalsIgnoreCase(input));
	}
}
