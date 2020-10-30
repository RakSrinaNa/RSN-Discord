package fr.raksrinana.rsndiscord.modules.music.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class SeekMusicCommand extends BasicCommand{
	private static final Pattern TIME_PATTERN = Pattern.compile("((\\d{1,2}):)?((\\d{1,2}):)?(\\d{1,2})");
	private static final long SECOND_PER_MINUTE = 60;
	private static final long SECOND_PER_HOUR = 3600;
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	SeekMusicCommand(@NonNull final Command parent){
		super(parent);
	}
	
	@Override
	public boolean isAllowed(final @NonNull Member member){
		return RSNAudioManager.isRequester(member.getGuild(), member.getUser()) || Utilities.isModerator(member);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.music.seek", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		else{
			final var time = SeekMusicCommand.parseTime(event.getGuild(), args.pop());
			if(time < 0){
				Actions.reply(event, translate(event.getGuild(), "music.invalid-format"), null);
			}
			else{
				switch(RSNAudioManager.seek(event.getGuild(), time)){
					case NO_MUSIC -> Actions.reply(event, translate(event.getGuild(), "music.nothing-playing"), null);
					case OK -> Actions.reply(event, translate(event.getGuild(), "music.seeked", event.getAuthor().getAsMention(), NowPlayingMusicCommand.getDuration(time)), null);
					case IMPOSSIBLE -> Actions.reply(event, translate(event.getGuild(), "music.seek-error"), null);
				}
			}
		}
		return CommandResult.SUCCESS;
	}
	
	private static long parseTime(@NonNull final Guild guild, @NonNull final String time){
		final var matcher = TIME_PATTERN.matcher(time);
		if(!matcher.matches()){
			return -1;
		}
		var duration = 0L;
		duration += SeekMusicCommand.getAsLong(guild, matcher.group(2)) * SECOND_PER_HOUR;
		duration += SeekMusicCommand.getAsLong(guild, matcher.group(4)) * SECOND_PER_MINUTE;
		duration += SeekMusicCommand.getAsLong(guild, matcher.group(5));
		return duration * 1000;
	}
	
	private static long getAsLong(final Guild guild, final String str){
		if(Objects.isNull(str) || str.isBlank()){
			return 0;
		}
		try{
			return Long.parseLong(str);
		}
		catch(final Exception e){
			Log.getLogger(guild).error("Error parsing {} into long", str, e);
		}
		return 0;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + "<time>";
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Time", translate(guild, "command.music.seek.help.time"), false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.music.seek.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("seek");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.music.seek.description");
	}
}