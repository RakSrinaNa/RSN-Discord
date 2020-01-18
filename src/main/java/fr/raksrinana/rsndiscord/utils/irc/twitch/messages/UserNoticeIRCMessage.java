package fr.raksrinana.rsndiscord.utils.irc.twitch.messages;

import fr.raksrinana.rsndiscord.utils.irc.twitch.AbstractTwitchMessageWithId;
import fr.raksrinana.rsndiscord.utils.irc.twitch.IRCTag;
import lombok.Getter;
import java.util.List;

@Getter
public class UserNoticeIRCMessage extends AbstractTwitchMessageWithId{
	private final List<IRCTag> tags;
	private final String ircChannel;
	private final String message;
	
	public UserNoticeIRCMessage(final List<IRCTag> tags, final String ircChannel, final String message){
		this.tags = tags;
		this.ircChannel = ircChannel;
		this.message = message;
	}
}