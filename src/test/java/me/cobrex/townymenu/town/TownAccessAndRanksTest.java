package me.cobrex.townymenu.town;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.cobrex.townymenu.utils.TownAccess;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class TownAccessAndRanksTest {
	private static int tests;

	@Test
	void verifiesTownAccessAndRankDisplay() throws Exception {
		main(new String[0]);
	}

	private static void check(String name, boolean expected, boolean actual) {
		if (expected != actual)
			throw new AssertionError(name + ": expected " + expected + ", got " + actual);
		tests++;
		System.out.println("PASS " + name);
	}

	private static void check(String name, String expected, String actual) {
		if (!expected.equals(actual))
			throw new AssertionError(name + ": expected " + expected + ", got " + actual);
		tests++;
		System.out.println("PASS " + name);
	}

	public static void main(String[] args) throws Exception {
		var config = TownySettings.class.getDeclaredField("config");
		config.setAccessible(true);
		config.set(null, mock(com.palmergames.bukkit.config.CommentedConfiguration.class));

		TownyAPI api = mock(TownyAPI.class);
		var instance = TownyAPI.class.getDeclaredField("instance");
		instance.setAccessible(true);
		instance.set(null, api);

		Player player = mock(Player.class);
		Resident actor = mock(Resident.class);
		Resident target = mock(Resident.class);
		Town home = mock(Town.class);
		Town foreign = mock(Town.class);
		String permission = "towny.command.town.set.perm";

		when(api.getResident(player)).thenReturn(actor);
		when(actor.hasTown()).thenReturn(true);
		when(actor.getTownOrNull()).thenReturn(home);
		when(player.hasPermission(permission)).thenReturn(true);

		check("rank permission works in own town", true, TownAccess.canModify(player, home, permission));
		check("rank permission cannot affect foreign town", false, TownAccess.canModify(player, foreign, permission));
		when(player.hasPermission(permission)).thenReturn(false);
		check("missing permission is denied", false, TownAccess.canModify(player, home, permission));
		when(player.hasPermission(permission)).thenReturn(true);
		when(actor.hasTown()).thenReturn(false);
		check("townless player is denied", false, TownAccess.canModify(player, home, permission));
		when(actor.hasTown()).thenReturn(true);
		when(api.getResident(player)).thenReturn(null);
		check("unregistered player is denied", false, TownAccess.canModify(player, home, permission));

		when(target.getTownOrNull()).thenReturn(home);
		check("target in same town", true, TownAccess.isResidentOf(home, target));
		when(target.getTownOrNull()).thenReturn(foreign);
		check("target in foreign town", false, TownAccess.isResidentOf(home, target));

		when(target.isMayor()).thenReturn(false);
		when(target.getTownRanks()).thenReturn(List.of());
		check("resident label", "resident", TownAccess.getDisplayTownRanks(target));
		when(target.getTownRanks()).thenReturn(List.of("assistant", "sheriff"));
		check("multiple rank labels", "assistant, sheriff", TownAccess.getDisplayTownRanks(target));
		when(target.isMayor()).thenReturn(true);
		check("mayor and assigned ranks", "mayor, assistant, sheriff", TownAccess.getDisplayTownRanks(target));

		System.out.println(tests + " TownAccess/rank display tests passed.");
	}
}
