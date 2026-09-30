# TownyMenu inventory protection fix

This fork fixes menu item extraction after a denied action or a button error.
It also protects menu inventories against drag and transfer actions. The fix
applies to the shared menu system, including town, nation and plot menus.

Based on [cobrex1/TownyMenu](https://github.com/cobrex1/TownyMenu) version 2.0.7.
Patched version: `2.0.7-fix.1`. See [fix details and verification](FORK.md).

Build with `mvn -B -ntp clean verify` (validated with JDK 21).

# Author

This was originally written by bennycio  
https://github.com/bennycio/TownyMenu

Cobrex maintains the upstream fork, with additional features, code updates and dependency updates.

# Towny Menu

Spigot Plugin for Towny designed to simplify the ownership and management of Towns and Plots.  
https://www.spigotmc.org/resources/towny-menu-update.103260/

# Commands

/nm | nationmenu - opens the nation management menu  
/tm | /townmenu - open the town management menu  
/plm | /plotmenu - open the plot management menu  
/cv | /chunkview - view the chunk border your standing in using blocks  
/cvp | /chunkviewparticle - view the chunk border your standing in with particles

# Permissions

townymenu.town.use - Permission to use the town management menu  
townymenu.plot.admin - permission to manage any plot in any town  
chunkview.view - Permission to use the chunkview command  
chunkviewparticle.view - Permission to use the chunkviewparticle command

# Translations

If you'd like to help translating TownyMenu into the available languages or add an entirely
new languages just open an issue on Github, or message me on my discord. 
Translations are here -> [crowdin](https://crowdin.com/project/townymenu)

# Support

https://discord.gg/Sgc6hy9CCS - Support discord
