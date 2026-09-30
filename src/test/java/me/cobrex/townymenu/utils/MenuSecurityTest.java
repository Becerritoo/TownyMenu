package me.cobrex.townymenu.utils;

import me.cobrex.townymenu.listeners.MenuListener;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuSecurityTest {
    private final Player player = mock(Player.class);
    private final Inventory top = mock(Inventory.class);
    private final Inventory bottom = mock(Inventory.class);
    private final InventoryView view = mock(InventoryView.class);
    private final MenuHandler menu = mock(MenuHandler.class);
    private final MenuListener listener = new MenuListener();

    MenuSecurityTest() {
        when(view.getTopInventory()).thenReturn(top);
        when(view.getBottomInventory()).thenReturn(bottom);
        when(view.getPlayer()).thenReturn(player);
        when(top.getHolder()).thenReturn(menu);
        when(top.getSize()).thenReturn(27);
        when(bottom.getSize()).thenReturn(36);
    }

    private InventoryClickEvent click(ClickType type, int slot) {
        return new InventoryClickEvent(view, InventoryType.SlotType.CONTAINER,
                slot, type, InventoryAction.PICKUP_ALL);
    }

    @Test void menuRemainsProtectedAfterDenialClearsRegistration() {
        when(player.getUniqueId()).thenReturn(java.util.UUID.randomUUID());
        doAnswer(invocation -> {
            assertTrue(((InventoryClickEvent) invocation.getArgument(0)).isCancelled());
            MenuManager.closeMenu(player); // Existing bank/permission denial callbacks.
            return null;
        }).when(menu).handleClick(any());
        InventoryClickEvent first = click(ClickType.LEFT, 0);
        listener.onInventoryClick(first);
        InventoryClickEvent second = click(ClickType.LEFT, 0);
        listener.onInventoryClick(second);
        assertTrue(first.isCancelled());
        assertTrue(second.isCancelled());
        verify(menu, times(2)).handleClick(any());
    }

    @Test void exceptionCannotUnlockMenuOrNextClick() {
        doAnswer(invocation -> {
            InventoryClickEvent event = invocation.getArgument(0);
            assertTrue(event.isCancelled());
            event.setCancelled(false);
            throw new IllegalStateException("button failed");
        }).when(menu).handleClick(any());
        for (int i = 0; i < 2; i++) {
            InventoryClickEvent event = click(ClickType.LEFT, 0);
            assertThrows(IllegalStateException.class, () -> listener.onInventoryClick(event));
            assertTrue(event.isCancelled());
        }
    }

    @ParameterizedTest @EnumSource(ClickType.class)
    void allClickTypesAreCancelledAndOnlyNormalClicksActivateButtons(ClickType type) {
        InventoryClickEvent event = click(type, 0);
        listener.onInventoryClick(event);
        assertTrue(event.isCancelled());
        verify(menu, times(type == ClickType.LEFT || type == ClickType.RIGHT ? 1 : 0))
                .handleClick(event);
    }

    @Test void bottomInventoryAndOutsideClicksCannotTransferItems() {
        for (int slot : new int[]{27, -999}) {
            InventoryClickEvent event = click(ClickType.SHIFT_LEFT, slot);
            listener.onInventoryClick(event);
            assertTrue(event.isCancelled());
        }
        verifyNoInteractions(menu);
    }

    @Test void dragCannotInsertItemsIntoMenu() {
        InventoryDragEvent event = new InventoryDragEvent(view, null,
                mock(ItemStack.class), false, Collections.emptyMap());
        listener.onInventoryDrag(event);
        assertTrue(event.isCancelled());
    }

    @Test void existingCancellationDoesNotExecuteButton() {
        InventoryClickEvent event = click(ClickType.LEFT, 0);
        event.setCancelled(true);
        listener.onInventoryClick(event);
        assertTrue(event.isCancelled());
        verifyNoInteractions(menu);
    }

    @Test void unrelatedInventoryIsUntouched() {
        when(top.getHolder()).thenReturn(null);
        InventoryClickEvent event = click(ClickType.LEFT, 0);
        listener.onInventoryClick(event);
        assertFalse(event.isCancelled());
        verifyNoInteractions(menu);
    }
}
