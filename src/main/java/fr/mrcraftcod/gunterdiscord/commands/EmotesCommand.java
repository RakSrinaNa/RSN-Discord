package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.EmotesParticipationConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class EmotesCommand extends BasicCommand{
	public static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyww");
	public static final DateTimeFormatter DFD = DateTimeFormatter.ofPattern("ww");
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		sendInfos(event.getGuild(), LocalDate.now(), event.getAuthor(), event.getTextChannel());
		return CommandResult.SUCCESS;
	}
	
	public static boolean sendInfos(final Guild guild, final LocalDate localDate, final User author, final TextChannel channel){
		final var weekKey = getKey(localDate);
		final var date = localDate.format(DFD);
		final var stats = new EmotesParticipationConfig(guild).getValue(weekKey);
		if(Objects.nonNull(stats)){
			final var i = new AtomicInteger(1);
			final var builder = Utilities.buildEmbed(author, Color.MAGENTA, "Participation of the week " + date + " (UTC)");
			stats.entrySet().stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).map(e -> guild.getEmotesByName(e.getKey(), true).stream().findFirst().map(e2 -> Map.entry(e2, e.getValue())).orElse(null)).filter(Objects::nonNull).limit(10).forEachOrdered(e -> builder.addField("#" + i.getAndIncrement(), e.getKey().getAsMention() + " Use count: " + e.getValue(), false));
			Actions.sendMessage(channel, builder.build());
			return true;
		}
		return false;
	}
	
	public static String getKey(final LocalDate localDate){
		return localDate.format(DF);
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Override
	public String getName(){
		return "Utilisation emotes";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("emote");
	}
	
	@Override
	public String getDescription(){
		return "Gets the usage of the emotes for the current week";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
