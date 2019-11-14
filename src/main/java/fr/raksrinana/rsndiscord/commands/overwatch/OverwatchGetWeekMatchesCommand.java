package fr.raksrinana.rsndiscord.commands.overwatch;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.listeners.reply.EmoteWaitingUserReply;
import fr.raksrinana.rsndiscord.listeners.reply.ReplyMessageListener;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.overwatch.OverwatchUtils;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.OverwatchStage;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.match.OverwatchScore;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.week.OverwatchWeek;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.awt.Color;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class OverwatchGetWeekMatchesCommand extends BasicCommand{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	private static final BiConsumer<GuildMessageReactionAddEvent, OverwatchWeek> onWeek = (event, week) -> {
		final var builder = Utilities.buildEmbed(event.getUser(), Color.GREEN, week.getName(), null);
		week.getMatches().forEach(m -> {
			final var message = (m.hasEnded() || m.inProgress()) ? (m.getScores().stream().map(OverwatchScore::getValue).map(Object::toString).collect(Collectors.joining(" - ")) + (m.inProgress() ? " (In progress)" : "")) : ("On the " + m.getStartDate().atZone(ZoneId.of("Europe/Paris")).format(FORMATTER) + " (Europe/Paris)");
			builder.addField(m.getVsCompetitorsNames(), message, false);
		});
		builder.setFooter("ID: " + week.getId());
		Actions.reply(event, "", builder.build());
	};
	private static final BiConsumer<GuildMessageReactionAddEvent, OverwatchStage> onStage = (event, stage) -> {
		if(!stage.getWeeks().isEmpty()){
			if(stage.getWeeks().size() == 1){
				onWeek.accept(event, stage.getWeeks().get(0));
			}
			else{
				final var counter = new AtomicInteger('a');
				final var options = new HashMap<BasicEmotes, OverwatchWeek>();
				final var currentWeek = stage.getCurrentWeek();
				final var nextWeek = stage.getNextWeek();
				final var builder = Utilities.buildEmbed(event.getUser(), Color.GREEN, "Available weeks", null);
				stage.getWeeks().forEach(w -> {
					final var emote = BasicEmotes.getEmote(String.valueOf((char) counter.getAndIncrement()));
					options.put(emote, w);
					builder.addField(emote.getValue() + ": " + w.getName(), currentWeek.map(w::equals).orElse(false) ? "Current" : (nextWeek.map(w::equals).orElse(false) ? "Next" : ""), false);
				});
				builder.setFooter("ID: " + stage.getId());
				Actions.reply(event, "", builder.build()).thenAccept(message -> {
					options.keySet().stream().sorted().forEachOrdered(e -> Actions.addReaction(message, e.getValue()));
					ReplyMessageListener.handleReply(new EmoteWaitingUserReply<>(options, event, event.getUser(), message, OverwatchGetWeekMatchesCommand.onWeek));
				});
			}
		}
		else{
			Actions.reply(event, "No weeks found", null);
		}
	};
	
	public OverwatchGetWeekMatchesCommand(final Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		OverwatchUtils.getData().ifPresentOrElse(overwatchResponse -> {
			if(!overwatchResponse.getData().getStages().isEmpty()){
				final var counter = new AtomicInteger('a');
				final var options = new HashMap<BasicEmotes, OverwatchStage>();
				final var currentStage = overwatchResponse.getData().getCurrentStage();
				final var nextStage = overwatchResponse.getData().getNextStage();
				final var builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Available stages", null);
				overwatchResponse.getData().getStages().forEach(s -> {
					final var emote = BasicEmotes.getEmote(String.valueOf((char) counter.getAndIncrement()));
					options.put(emote, s);
					builder.addField(emote.getValue() + ": " + s.getName(), currentStage.map(s::equals).orElse(false) ? "Current" : (nextStage.map(s::equals).orElse(false) ? "Next" : ""), false);
				});
				Actions.reply(event, "", builder.build()).thenAccept(message -> {
					options.keySet().stream().sorted().forEachOrdered(e -> Actions.addReaction(message, e.getValue()));
					ReplyMessageListener.handleReply(new EmoteWaitingUserReply<>(options, event, event.getAuthor(), message, onStage));
				});
			}
			else{
				Actions.reply(event, "No stages found", null);
			}
		}, () -> Actions.reply(event, "Error while getting data from Overwatch", null));
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Current week matches";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("w", "week");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Get the matches of a week";
	}
}
