package fr.raksrinana.rsndiscord.components.impl.button;

import fr.raksrinana.rsndiscord.components.ButtonHandler;
import fr.raksrinana.rsndiscord.components.ComponentResult;
import fr.raksrinana.rsndiscord.components.base.SimpleButtonHandler;
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
import static fr.raksrinana.rsndiscord.components.ComponentResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.Utilities.MAIN_RAKSRINANA_ACCOUNT;

@Log4j2
@ButtonHandler
public class AniListMediaCompletedButtonHandler extends SimpleButtonHandler{
	public AniListMediaCompletedButtonHandler(){
		super("anilist-media-completed");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handle(@NotNull ButtonClickEvent event){
		var user = event.getUser();
		var message = event.getMessage();
		
		return Optional.ofNullable(event.getJDA().getUserById(MAIN_RAKSRINANA_ACCOUNT))
				.map(User::openPrivateChannel)
				.map(RestAction::submit)
				.map(future -> future.thenCompose(privateChannel -> JDAWrappers.message(privateChannel, message)
								.content(user.getAsMention() + " completed")
								.clearActionRows()
								.submit())
						.thenCompose(m -> JDAWrappers.delete(message).submit())
						.thenApply(m -> HANDLED))
				.orElseGet(() -> CompletableFuture.completedFuture(HANDLED));
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.success(getComponentId(), "Complete").withEmoji(Emoji.fromUnicode("U+1F44C"));
	}
}
