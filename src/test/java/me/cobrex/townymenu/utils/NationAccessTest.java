package me.cobrex.townymenu.utils;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class NationAccessTest {
	private static int tests;

	@Test
	void verifiesNationAccessRules() throws Exception {
		main(new String[0]);
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
		Nation home = mock(Nation.class);
		Nation foreign = mock(Nation.class);
		String permission = "towny.command.nation.toggle.public";

		when(api.getResident(player)).thenReturn(actor);
		when(actor.hasNation()).thenReturn(true);
		when(actor.getNationOrNull()).thenReturn(home);
		when(player.hasPermission(permission)).thenReturn(true);

		check("nation permission works in own nation", true, NationAccess.canModify(player, home, permission));
		check("nation permission cannot affect foreign nation", false, NationAccess.canModify(player, foreign, permission));
		when(player.hasPermission(permission)).thenReturn(false);
		check("missing nation permission is denied", false, NationAccess.canModify(player, home, permission));
		when(player.hasPermission(permission)).thenReturn(true);
		when(actor.hasNation()).thenReturn(false);
		check("nationless player is denied", false, NationAccess.canModify(player, home, permission));
		when(actor.hasNation()).thenReturn(true);
		when(api.getResident(player)).thenReturn(null);
		check("unregistered nation player is denied", false, NationAccess.canModify(player, home, permission));

		when(target.getNationOrNull()).thenReturn(home);
		check("target in same nation", true, NationAccess.isResidentOf(home, target));
		when(target.getNationOrNull()).thenReturn(foreign);
		check("target in foreign nation", false, NationAccess.isResidentOf(home, target));

		System.out.println(tests + " NationAccess tests passed.");
	}

	private static void check(String label, boolean expected, boolean actual) {
		if (expected != actual)
			throw new AssertionError(label + ": expected " + expected + ", got " + actual);
		tests++;
		System.out.println("PASS " + label);
	}
}
