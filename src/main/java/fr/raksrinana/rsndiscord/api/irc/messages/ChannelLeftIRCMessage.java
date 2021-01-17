package fr.raksrinana.rsndiscord.api.irc.messages;

import fr.raksrinana.rsndiscord.api.irc.twitch.IRCUser;
import lombok.Getter;

@Getter
public class ChannelLeftIRCMessage implements IIRCMessage{
	private final String channel;
	private final IRCUser user;
	
	public ChannelLeftIRCMessage(final IRCUser user, final String channel){
		this.user = user;
		this.channel = channel;
	}
}