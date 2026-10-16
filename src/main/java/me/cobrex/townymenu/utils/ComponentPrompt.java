package me.cobrex.townymenu.utils;

import me.cobrex.townymenu.TownyMenuPlugin;
import me.cobrex.townymenu.settings.Localization;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public abstract class ComponentPrompt extends StringPrompt {

	public static void startPrompt(Player player, ComponentPrompt prompt) {

		ConversationFactory factory = new ConversationFactory(TownyMenuPlugin.instance)
				.withFirstPrompt(prompt)
				.withLocalEcho(false)
				.withPrefix(context -> "")
//				.withEscapeSequence("cancel")
//				.withEscapeSequence(Localization.cancel(player))
				.thatExcludesNonPlayersWithMessage("Console cannot use this.");

		Conversation conversation = factory.buildConversation(player);
		conversation.begin();
	}

	@Override
	public String getPromptText(ConversationContext context) {
		Player player = (Player) context.getForWhom();
		MessageUtils.send(player, getPromptMessage(context));
		return "";
	}

	public void show(Player player) {
//		System.out.println("[DEBUG CompPrompt 17] show() called");
		ComponentPrompt.startPrompt(player, this);
	}

	protected Player getPlayer(ConversationContext context) {
		Object forWhom = context.getForWhom();
		if (forWhom instanceof Player) {
			return (Player) forWhom;
		}
		throw new IllegalStateException("Prompt was not started by a player.");
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String input) {
		Player player = getPlayer(context);
		String trimmed = input.trim();

		if (isCancelInput(trimmed, Localization.cancel(player))) {
			MessageUtils.send(player, Localization.Error.CANCELLED);
			return Prompt.END_OF_CONVERSATION;
		}

		return accept(context, input);
	}

	static boolean isCancelInput(String input, String localizedCancel) {
		return input != null
				&& (input.equalsIgnoreCase("cancel")
				|| localizedCancel != null && input.equalsIgnoreCase(localizedCancel));
	}

	protected abstract Prompt accept(ConversationContext context, String input);

	protected abstract String getPromptMessage(ConversationContext context);
}
