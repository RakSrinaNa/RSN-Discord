package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.utils.irc.events.ChannelJoinedIRCEvent;
import fr.mrcraftcod.gunterdiscord.utils.irc.events.ChannelMessageIRCEvent;
import fr.mrcraftcod.gunterdiscord.utils.irc.events.IRCEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
public class IRCUtils{
	private static final Logger LOGGER = LoggerFactory.getLogger(IRCUtils.class);
	
	public static IRCEvent buildEvent(String message){
		if(!message.startsWith(":")){
			LOGGER.warn("IRC message doesn't start with ':' => {}", message);
			return null;
		}
		message = message.substring(1);
		final var columnIndex = message.indexOf(':');
		final var infos = message.substring(0, columnIndex == -1 ? message.length() : columnIndex).split(" ");
		final var user = new IRCUser(infos[0]);
		final var eventType = infos[1];
		switch(eventType){
			case "JOIN":
				return new ChannelJoinedIRCEvent(user, eventType, infos[2]);
			case "PRIVMSG":
				if(columnIndex < 0){
					LOGGER.error("Invalid IRC message (no message): {}", message);
					return null;
				}
				return new ChannelMessageIRCEvent(user, eventType, infos[2], message.substring(columnIndex + 1));
			case "001":
			case "002":
			case "003":
			case "004":
			case "353":
			case "366":
			case "372":
			case "375":
			case "376":
				break;
			default:
				LOGGER.warn("Unknown IRC event type {} in {}", eventType, message);
		}
		return null;
	}
}
