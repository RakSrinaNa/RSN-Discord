package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.AutoRolesConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-12
 */
public class AutoRolesListener extends ListenerAdapter{
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event){
		super.onGuildMemberJoin(event);
		try{
			Actions.giveRole(event.getMember(), new AutoRolesConfig().getAsList(event.getGuild()));
		}
		catch(Exception e){
			getLogger(event.getGuild()).error("Error on user join", e);
		}
	}
}
