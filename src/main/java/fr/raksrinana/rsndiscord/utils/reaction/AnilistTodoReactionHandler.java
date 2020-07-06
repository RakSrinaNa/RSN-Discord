package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Objects;

public class AnilistTodoReactionHandler extends TodoReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, ReactionTag.ANILIST_TODO);
	}
	
	@Override
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emotes, @NonNull WaitingReactionMessageConfiguration todo){
		todo.getMessage().getMessage().ifPresent(message -> message.getEmbeds().forEach(embed -> Actions.sendPrivateMessage(message.getGuild(), Utilities.RAKSRINANA_ACCOUNT, event.getMember().getUser().getAsMention() + " completed", embed)));
		return super.processTodoCompleted(event, emotes, todo);
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
