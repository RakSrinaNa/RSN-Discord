package fr.raksrinana.rsndiscord.button.impl;

import fr.raksrinana.rsndiscord.button.ButtonHandler;
import fr.raksrinana.rsndiscord.button.ButtonResult;
import fr.raksrinana.rsndiscord.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.button.ButtonResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.Utilities.MAIN_RAKSRINANA_ACCOUNT;

@Log4j2
@ButtonHandler
public class ExternalTodoCompletedButtonHandler extends SimpleButtonHandler{
	public ExternalTodoCompletedButtonHandler(){
		super("external-todo-completed");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ButtonResult> handle(@NotNull ButtonClickEvent event){
		var user = event.getUser();
		var message = event.getMessage();
		
		return Optional.ofNullable(event.getJDA().getUserById(MAIN_RAKSRINANA_ACCOUNT))
				.map(User::openPrivateChannel)
				.map(RestAction::submit)
				.map(future -> future.thenCompose(privateChannel -> JDAWrappers.message(privateChannel, user.getAsMention() + " completed").submit()
						.thenCompose(m -> JDAWrappers.message(m.getPrivateChannel(), message).clearActionRows().submit())
						.thenCompose(m -> JDAWrappers.delete(message).submit())
						.thenApply(m -> HANDLED)))
				.orElseGet(() -> CompletableFuture.completedFuture(HANDLED));
	}
	
	@Override
	@NotNull
	public Button asButton(){
		return Button.success(getButtonId(), "Complete").withEmoji(Emoji.fromUnicode("U+1F44C"));
	}
}