package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.YoutubeChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.YoutubeRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class YoutubeCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Message", "Le message à dire", false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(event.getMessage().getMentionedMembers().size() > 0){
			args.poll();
			final var member = event.getMessage().getMentionedMembers().get(0);
			if(Utilities.hasRole(member, new YoutubeRoleConfig(event.getGuild()).getObject())){
				final var strUrl = args.poll();
				if(strUrl != null){
					if(Utilities.isAdmin(event.getMember())){
						try{
							final var url = new URL(strUrl);
							new YoutubeChannelConfig(event.getGuild()).addValue(member.getUser().getIdLong(), url.toString());
						}
						catch(final Exception e){
							Log.getLogger(event.getGuild()).warn("Provided YouTube link isn't valid {}", strUrl);
							Actions.reply(event, "Le lien n'est pas valide");
						}
					}
					else{
						Actions.reply(event, "Vous ne pouvez pas modifier la chaine d'un utilisateur");
					}
				}
				else{
					Actions.reply(event, "La chaine de %s est disponible ici: %s", member.getAsMention(), new YoutubeChannelConfig(event.getGuild()).getValue(member.getUser().getIdLong()));
				}
			}
			else{
				Actions.reply(event, "Cet utilisateur n'est pas YouTubeur");
			}
		}
		else{
			Actions.reply(event, "Merci de mentionner un utilisateur");
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@utilisateur> [URL]";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "YouTube";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("youtube", "yt");
	}
	
	@Override
	public String getDescription(){
		return "Obtient la chaine d'un YouTubeur";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
