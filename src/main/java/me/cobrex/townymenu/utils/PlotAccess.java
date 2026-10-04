package me.cobrex.townymenu.utils;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.entity.Player;

/** Use Towny's ownership rules, including town-scoped asmayor permissions. */
public final class PlotAccess {
    private PlotAccess() {}

    public static boolean canAccess(Player player, TownBlock plot) {
        if (plot == null || plot.getTownOrNull() == null) return false;
        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return false;
        try {
            TownyAPI.getInstance().testPlotOwnerOrThrow(resident, plot);
            return true;
        } catch (TownyException denied) {
            return false;
        }
    }

    public static boolean canModify(Player player, TownBlock plot, String permission) {
        Resident resident = TownyAPI.getInstance().getResident(player);
        return resident != null && resident.hasPermissionNode(permission) && canAccess(player, plot);
    }
}
