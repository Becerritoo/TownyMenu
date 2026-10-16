package me.cobrex.townymenu.utils;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps menu actions scoped to the player's current town while allowing
 * Towny's effective permission attachments to decide which ranks may act.
 */
public final class TownAccess {
	private TownAccess() {
	}

	public static boolean canModify(Player player, Town town, String permission) {
		if (player == null || town == null || permission == null)
			return false;

		Resident resident = TownyAPI.getInstance().getResident(player);
		return resident != null
				&& resident.hasTown()
				&& town.equals(resident.getTownOrNull())
				&& player.hasPermission(permission);
	}

	public static boolean isResidentOf(Town town, Resident resident) {
		return town != null
				&& resident != null
				&& town.equals(resident.getTownOrNull());
	}

	public static String getDisplayTownRanks(Resident resident) {
		List<String> ranks = new ArrayList<>();
		if (resident.isMayor())
			ranks.add("mayor");
		ranks.addAll(resident.getTownRanks());
		return ranks.isEmpty() ? "resident" : String.join(", ", ranks);
	}
}
