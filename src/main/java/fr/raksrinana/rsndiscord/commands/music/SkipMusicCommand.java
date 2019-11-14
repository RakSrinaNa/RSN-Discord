package fr.raksrinana.rsndiscord.commands.music;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.player.RSNAudioManager;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		switch(RSNAudioManager.skip(event.getGuild())){
			case NO_MUSIC:
				Actions.reply(event, MessageFormat.format("{0}, no music currently playing", event.getAuthor().getAsMention()), null);
				break;
			case OK:
				Actions.reply(event, MessageFormat.format("{0} skipped the music", event.getAuthor().getAsMention()), null);
				break;
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public boolean isAllowed(final Member member){
		return Objects.nonNull(member) && (Utilities.isTeam(member) || RSNAudioManager.isRequester(member.getGuild(), member.getUser()));
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Skip";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("skip");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Skips the current music";
	}
}
