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
public class BannedRegexConfig extends ListConfiguration<String>{
	
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	public BannedRegexConfig(final Guild guild){
		super(guild);
	}
	
	@Override
	public String getName(){
		return "bannedRegex";
	}
	
	@Override
	protected BiFunction<MessageReceivedEvent, String, String> getMessageParser(){
		return (event, arg) -> arg;
	}
	
	@Override
	protected Function<String, String> getValueParser(){
		return s -> s;
	}
	
	@Override
	protected Function<String, String> getConfigParser(){
		return s -> s;
	}
}
