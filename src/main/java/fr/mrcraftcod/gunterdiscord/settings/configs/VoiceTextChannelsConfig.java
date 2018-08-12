package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.settings.configurations.ListConfiguration;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class VoiceTextChannelsConfig extends ListConfiguration<Long>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	public VoiceTextChannelsConfig(Guild guild){
		super(guild);
	}
	
	@Override
	protected Function<Long, String> getValueParser(){
		return l -> "" + l;
	}
	
	@Override
	protected BiFunction<MessageReceivedEvent, String, String> getMessageParser(){
		return (event, arg) -> arg;
	}
	
	@Override
	protected Function<String, Long> getConfigParser(){
		return Long::parseLong;
	}
	
	@Override
	public String getName(){
		return "voiceTextChannels";
	}
}