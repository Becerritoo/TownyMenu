package me.cobrex.townymenu.town.prompt;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.permissions.TownyPerms;
import me.cobrex.townymenu.settings.Localization;
import me.cobrex.townymenu.utils.ComponentPrompt;
import me.cobrex.townymenu.utils.MessageUtils;
import me.cobrex.townymenu.utils.TownAccess;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class TownRankPrompt extends ComponentPrompt {

	private final Resident resident;

	public TownRankPrompt(Resident resident) {
		this.resident = resident;
	}

	@Override
	protected String getPromptMessage(ConversationContext context) {
		String ranks = String.join(", ", TownyPerms.getTownRanks());
		return Localization.TownConversables.Rank.PROMPT
				.replace("{player}", resident.getName())
				.replace("{ranks}", ranks)
				.replace("{current}", TownAccess.getDisplayTownRanks(resident));
	}

	@Override
	public Prompt accept(@NotNull ConversationContext context, String input) {
		Player player = (Player) context.getForWhom();

		String trimmed = input.trim();
		String removeWord = Localization.TownConversables.Rank.REMOVE.trim();
		boolean removing = startsWithWord(trimmed, removeWord) || startsWithWord(trimmed, "remove");
		String requestedRank = removing ? trimmed.substring(trimmed.indexOf(' ') + 1).trim() : trimmed;
		String rank = matchRank(requestedRank);

		if (rank == null) {
			String ranks = String.join(", ", TownyPerms.getTownRanks());
			MessageUtils.send(player, Localization.TownConversables.Rank.INVALID.replace("{ranks}", ranks));
			return this;
		}

		Town town = resident.getTownOrNull();
		String permission = "towny.command.town.rank." + rank.toLowerCase(Locale.ROOT);
		if (!TownAccess.canModify(player, town, permission) || !TownAccess.isResidentOf(town, resident)) {
			MessageUtils.send(player, Localization.Error.NO_PERMISSION);
			return END_OF_CONVERSATION;
		}

		if (!removing && resident.hasTownRank(rank)) {
				MessageUtils.send(player, Localization.TownConversables.Rank.ALREADY_HAS_RANK
						.replace("{player}", resident.getName())
						.replace("{input}", rank));
				return this;
		}
		if (removing && !resident.hasTownRank(rank)) {
			MessageUtils.send(player, Localization.TownConversables.Rank.INVALID
					.replace("{ranks}", String.join(", ", TownyPerms.getTownRanks())));
			return this;
		}

		player.performCommand("town rank " + (removing ? "remove " : "add ") + resident.getName() + " " + rank);
		return END_OF_CONVERSATION;
	}

	private static boolean startsWithWord(String input, String word) {
		return !word.isEmpty() && input.regionMatches(true, 0, word + " ", 0, word.length() + 1);
	}

	private static String matchRank(String input) {
		return TownyPerms.getTownRanks().stream()
				.filter(rank -> rank.equalsIgnoreCase(input))
				.findFirst()
				.orElse(null);
	}
}
