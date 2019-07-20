package fr.mrcraftcod.gunterdiscord.commands.config.helpers;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

public abstract class BooleanConfigurationCommand extends ValueConfigurationCommand<Boolean>{
	public BooleanConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Override
	protected String getValueName(){
		return "Boolean";
	}
	
	@Override
	protected Boolean extractValue(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args){
		if(!args.isEmpty()){
			try{
				return Boolean.valueOf(args.pop());
			}
			catch(Exception e){
				Log.getLogger(event.getGuild()).error("Failed to parse boolean", e);
			}
		}
		throw new IllegalArgumentException("Please give a boolean value");
	}
}
