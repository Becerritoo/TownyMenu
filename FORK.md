# TownyMenu — Jarochitoland

Base: cobrex1/TownyMenu tag `2.0.7`, commit
`5b494ac1e54f75bb928adcb51e5249f2a81fecde`.
Branch: `jarochitoland/2.0.7-menu-security`.
Version: `2.0.7-JL.1`. Original authorship and GPL-3.0 license retained.

## Inventory protection fix

Bank/access-denial callbacks call `MenuManager.closeMenu(player)` while leaving
an inventory visible. Upstream protects clicks using only the registration map,
so subsequent clicks can extract menu icons. Additionally, its listener creates
a `ToggleSettingsMenu` before cancelling every click; a constructor failure can
leave the event unprotected. Opening a replacement inventory can also trigger a
close event that removes the replacement registration. Drags were not handled.

The actual top inventory's `MenuHandler` holder now identifies protected views.
All clicks and drags in these views are cancelled before callbacks run, and click
cancellation is restored in `finally`. Only normal left/right clicks on the menu
activate buttons; previously cancelled events do not activate buttons. Normal
inventories remain unaffected. Registration occurs after opening, close cleanup
checks inventory identity, and refresh uses the visible holder.

Build: `mvn -B -ntp clean verify` (JDK 21 used).
Tests cover access denial followed by another click, callback exceptions, all
click types, bottom/outside clicks, drags, and unrelated inventories.

## Deployment and in-game acceptance (pending)

The production plugin has not been replaced. Test on a staging server with the
production Towny/configuration first. As a non-admin, open `/tmenu`, select a
bank/menu action that is denied, and then attempt to take the icon using normal
clicks, shift, number keys, offhand swap, double click, drop and drag. No icon
should leave the panel. Verify permitted buttons, navigation, reopening and
normal chest/player inventory interactions. Also check `/nm` and `/plm`, which
share the same protection. Automated tests do not replace this in-game check.

For deployment, back up the old JAR, stop the server, replace it with the new
JAR (leave only one TownyMenu JAR in plugins), then start and repeat acceptance.
