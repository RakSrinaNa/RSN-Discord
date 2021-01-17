package fr.raksrinana.rsndiscord.reaction.handler;

import fr.raksrinana.rsndiscord.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import static fr.raksrinana.rsndiscord.reaction.handler.ReactionHandlerResult.PROCESSED_DELETE;
import static java.lang.Integer.MAX_VALUE;

@ReactionHandler
public class DefaultReactionHandler implements IReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return true;
	}
	
	@Override
	public ReactionHandlerResult accept(@NonNull GuildMessageReactionAddEvent event, @NonNull WaitingReactionMessageConfiguration reaction){
		return PROCESSED_DELETE;
	}
	
	@Override
	public int getPriority(){
		return MAX_VALUE;
	}
}