package fr.mrcraftcod.gunterdiscord.utils.irc.events;

import fr.mrcraftcod.gunterdiscord.utils.irc.IRCUser;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
public class ChannelLeftIRCEvent extends AbstractIRCEvent{
	public ChannelLeftIRCEvent(IRCUser user, String eventType, String channel){
		super(user, eventType, channel);
	}
}