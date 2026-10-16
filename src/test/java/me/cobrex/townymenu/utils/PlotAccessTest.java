package me.cobrex.townymenu.utils;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.*;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class PlotAccessTest {
    static int tests;

    @Test
    void verifiesPlotAccessRules() throws Exception {
        main(new String[0]);
    }
    static void check(String name, boolean expected, boolean actual) {
        if (expected != actual) throw new AssertionError(name + ": " + actual);
        tests++; System.out.println("PASS " + name);
    }
    public static void main(String[] args) throws Exception {
        var config = com.palmergames.bukkit.towny.TownySettings.class.getDeclaredField("config");
        config.setAccessible(true);
        config.set(null, mock(com.palmergames.bukkit.config.CommentedConfiguration.class));
        Translation.addTranslations(java.util.Map.of(Translation.getDefaultLocale().toString(), java.util.Map.of(
            "msg_area_not_own", "not owner", "msg_not_mayor_ass", "not mayor or assistant", "msg_err_not_part_town", "not town member")));
        TownyAPI api = mock(TownyAPI.class);
        var field = TownyAPI.class.getDeclaredField("instance");
        field.setAccessible(true); field.set(null, api);
        doCallRealMethod().when(api).testPlotOwnerOrThrow(any(), any());
        Player player = mock(Player.class);
        Resident resident = mock(Resident.class);
        Town home = mock(Town.class), foreign = mock(Town.class);
        TownBlock plot = mock(TownBlock.class);
        when(api.getResident(player)).thenReturn(resident);
        when(plot.getTownOrNull()).thenReturn(home);
        when(plot.hasResident()).thenReturn(true);
        when(resident.hasTown()).thenReturn(true);
        when(resident.getTownOrNull()).thenReturn(home);
        when(home.hasResident(resident)).thenReturn(true);
        String perm = "towny.command.plot.set.perm";
        when(resident.hasPermissionNode(perm)).thenReturn(true);
        when(plot.hasResident(resident)).thenReturn(true);
        check("owner private plot", true, PlotAccess.canModify(player, plot, perm));
        when(plot.hasResident(resident)).thenReturn(false);
        check("ordinary resident other owner", false, PlotAccess.canModify(player, plot, perm));
        when(resident.hasPermissionNode("towny.claimed.owntown.build.*")).thenReturn(true);
        when(resident.hasPermissionNode("towny.claimed.owntown.destroy.*")).thenReturn(true);
        check("constructor other owner", false, PlotAccess.canModify(player, plot, perm));
        when(resident.hasPermissionNode("towny.command.plot.asmayor")).thenReturn(true);
        check("assistant other owner same town", true, PlotAccess.canModify(player, plot, perm));
        when(plot.getTownOrNull()).thenReturn(foreign);
        check("assistant foreign private plot", false, PlotAccess.canModify(player, plot, perm));
        when(plot.hasResident()).thenReturn(false);
        check("assistant foreign municipal plot", false, PlotAccess.canModify(player, plot, perm));
        when(plot.getTownOrNull()).thenReturn(home);
        check("assistant own municipal plot", true, PlotAccess.canModify(player, plot, perm));
        when(resident.hasPermissionNode(perm)).thenReturn(false);
        check("missing action node", false, PlotAccess.canModify(player, plot, perm));
        when(resident.hasPermissionNode(perm)).thenReturn(true);
        when(resident.hasPermissionNode("towny.command.plot.asmayor")).thenReturn(false);
        check("rank revoked while menu open", false, PlotAccess.canModify(player, plot, perm));
        when(resident.hasPermissionNode("towny.command.plot.asmayorinunowned")).thenReturn(true);
        check("municipal-only role municipal", true, PlotAccess.canModify(player, plot, perm));
        when(plot.hasResident()).thenReturn(true);
        check("municipal-only role private", false, PlotAccess.canModify(player, plot, perm));
        check("wilderness", false, PlotAccess.canModify(player, null, perm));
        when(plot.getTownOrNull()).thenReturn(null);
        check("unclaimed plot", false, PlotAccess.canModify(player, plot, perm));
        when(api.getResident(player)).thenReturn(null);
        check("unregistered player", false, PlotAccess.canModify(player, plot, perm));
        System.out.println(tests + " permission tests passed using installed Towny ownership implementation.");
    }
}
