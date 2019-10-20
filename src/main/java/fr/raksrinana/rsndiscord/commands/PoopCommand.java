package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.settings.types.RemoveRoleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@BotCommand
public class PoopCommand extends BasicCommand{
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Role", "The role to poop on", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(!event.getMessage().getMentionedRoles().isEmpty()){
			final var removeDate = LocalDateTime.now().plusMinutes(10);
			final var poopRole = NewSettings.getConfiguration(event.getGuild()).getPoopRole().flatMap(RoleConfiguration::getRole);
			event.getMessage().getMentionedRoles().stream().findFirst().ifPresent(r -> event.getGuild().getMembersWithRoles(r).forEach(m -> {
				Actions.replyFormatted(event, "%s you poop", m.getAsMention());
				poopRole.ifPresent(pr -> {
					Actions.giveRole(event.getGuild(), m.getUser(), pr);
					NewSettings.getConfiguration(event.getGuild()).addRemoveRole(new RemoveRoleConfiguration(event.getAuthor(), pr, removeDate));
				});
			}));
		}
		else{
			Actions.reply(event, "Please mention a role");
		}
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <role>";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Poop";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("poop");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Poops on people";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}