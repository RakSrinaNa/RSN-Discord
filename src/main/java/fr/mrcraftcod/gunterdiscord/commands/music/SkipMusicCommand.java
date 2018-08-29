package fr.mrcraftcod.gunterdiscord.commands.music;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class SkipMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	SkipMusicCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		switch(GunterAudioManager.skip(event.getGuild())){
			case NO_MUSIC:
				Actions.reply(event, "%s, aucune musique n'est en cours", event.getAuthor().getAsMention());
				break;
			case OK:
				Actions.reply(event, "%s a passé la musique en cours", event.getAuthor().getAsMention());
				break;
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public boolean isAllowed(final Member member){
		return Utilities.isTeam(member) || GunterAudioManager.isRequester(member.getGuild(), member.getUser());
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Passer musique";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("skip");
	}
	
	@Override
	public String getDescription(){
		return "Passe la musique actuelle";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}