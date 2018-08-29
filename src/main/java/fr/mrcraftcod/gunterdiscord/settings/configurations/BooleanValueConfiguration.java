package fr.mrcraftcod.gunterdiscord.settings.configurations;

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
public abstract class BooleanValueConfiguration extends ValueConfiguration<Boolean>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected BooleanValueConfiguration(final Guild guild){
		super(guild);
	}
	
	@Override
	protected BiFunction<MessageReceivedEvent, String, String> getMessageParser(){
		return (event, arg) -> arg;
	}
	
	@Override
	protected Function<String, Boolean> getConfigParser(){
		return Boolean::parseBoolean;
	}
	
	@Override
	protected Function<Boolean, String> getValueParser(){
		return Object::toString;
	}
}