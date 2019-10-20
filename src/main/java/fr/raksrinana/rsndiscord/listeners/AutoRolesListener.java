package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.settings.types.RemoveRoleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserRoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-12
 */
public class AutoRolesListener extends ListenerAdapter{
	@Override
	public void onGuildMemberJoin(@Nonnull final GuildMemberJoinEvent event){
		super.onGuildMemberJoin(event);
		try{
			final var now = LocalDateTime.now();
			NewSettings.getConfiguration(event.getGuild()).getAutoRolesAndAddBackRoles(event.getMember()).stream().map(RoleConfiguration::getRole).filter(Optional::isPresent).map(Optional::get).forEach(role -> Actions.giveRole(event.getUser(), List.of(role)));
			NewSettings.getConfiguration(event.getGuild()).getRemoveRoles().stream().filter(b -> Objects.equals(b.getUser().getUserId(), event.getUser().getIdLong())).filter(b -> b.getEndDate().isAfter(now)).map(RemoveRoleConfiguration::getRole).map(RoleConfiguration::getRole).filter(Optional::isPresent).map(Optional::get).forEach(r -> Actions.giveRole(event.getGuild(), event.getUser(), r));
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("Error on user join", e);
		}
	}
	
	@Override
	public void onGuildMemberLeave(@Nonnull final GuildMemberLeaveEvent event){
		super.onGuildMemberLeave(event);
		try{
			NewSettings.getConfiguration(event.getGuild()).getLeaverRole().flatMap(RoleConfiguration::getRole).ifPresent(role -> NewSettings.getConfiguration(event.getGuild()).addAddBackRole(new UserRoleConfiguration(event.getUser(), role)));
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("Error on user leave", e);
		}
	}
}