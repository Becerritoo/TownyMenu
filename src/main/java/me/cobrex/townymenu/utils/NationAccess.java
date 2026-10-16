package me.cobrex.townymenu.utils;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.entity.Player;

/** Keeps nation menu actions scoped to the actor's current nation. */
public final class NationAccess {
	private NationAccess() {
	}

	public static boolean canModify(Player player, Nation nation, String permission) {
		if (player == null || nation == null || permission == null)
			return false;

		Resident resident = TownyAPI.getInstance().getResident(player);
		return resident != null
				&& resident.hasNation()
				&& nation.equals(resident.getNationOrNull())
				&& player.hasPermission(permission);
	}

	public static boolean isResidentOf(Nation nation, Resident resident) {
		return nation != null
				&& resident != null
				&& nation.equals(resident.getNationOrNull());
	}
}
