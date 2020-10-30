package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.commands.generic.*;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.modules.reaction.ReactionUtils;
import fr.raksrinana.rsndiscord.modules.reaction.config.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.modules.schedule.config.DeleteMessageScheduleConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.uselessfacts.UselessFactsUtils;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.awt.Color;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@EventListener
@Getter
public class CommandsEventListener extends ListenerAdapter{
	public final static String defaultPrefix = System.getProperty("RSN_DEFAULT_PREFIX", Main.DEVELOPMENT ? "g2?" : "g?");
	private final Set<Command> commands;
	
	/**
	 * Constructor.
	 */
	public CommandsEventListener(){
		this.commands = Utilities.getAllAnnotatedWith(BotCommand.class, clazz -> (Command) clazz.getConstructor().newInstance())
				.peek(c -> Log.getLogger(null).info("Loaded command {}", c.getClass().getName()))
				.collect(Collectors.toSet());
		
		final var counts = new HashMap<String, Integer>();
		this.commands.forEach(command -> command.getCommandStrings()
				.forEach(cmd -> counts.put(cmd, counts.getOrDefault(cmd, 0) + 1)));
		final var clash = counts.keySet().stream()
				.filter(key -> counts.get(key) > 1)
				.collect(Collectors.joining(", "));
		if(!clash.isEmpty()){
			Log.getLogger(null).error("Command clash: {}", clash);
		}
	}
	
	@Override
	public void onGuildMessageReceived(@NonNull final GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			if(isCommand(event.getGuild(), event.getMessage().getContentRaw())){
				if(!event.getAuthor().isBot()){
					var messageDeleted = new AtomicBoolean(false);
					Log.getLogger(event.getGuild()).debug("Processing potential command from {}: {}", event.getAuthor(), event.getMessage().getContentRaw());
					final var args = new LinkedList<>(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
					final var cmdText = args.pop().substring(Settings.get(event.getGuild()).getPrefix().orElse(defaultPrefix).length());
					this.getCommand(cmdText).ifPresentOrElse(command -> {
						if(command.deleteCommandMessageImmediately()){
							Actions.deleteMessage(event.getMessage());
							messageDeleted.set(true);
						}
						try{
							Log.getLogger(event.getGuild()).info("Executing command `{}`({}) from {}, args: {}", cmdText, command.getName(event.getGuild()), event.getAuthor(), args);
							final var executionResult = command.execute(event, args);
							if(executionResult == CommandResult.FAILED){
								Actions.sendPrivateMessage(event.getGuild(), event.getAuthor(), translate(event.getGuild(), "listeners.commands.error"), null);
							}
							else if(executionResult == CommandResult.BAD_ARGUMENTS){
								Actions.sendPrivateMessage(event.getGuild(), event.getAuthor(), translate(event.getGuild(), "listeners.commands.invalid-arguments"), null);
							}
						}
						catch(final NotAllowedException e){
							Log.getLogger(event.getGuild()).error("Error executing command {} (not allowed)", command, e);
							final var builder = new EmbedBuilder();
							builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
							builder.setColor(Color.RED);
							builder.setTitle(translate(event.getGuild(), "listeners.commands.unauthorized"));
							Actions.sendEmbed(event.getChannel(), builder.build());
						}
						catch(final NotHandledException e){
							Log.getLogger(event.getGuild()).warn("Command {} isn't handled for {} ({})", command, event.getAuthor(), e.getMessage());
						}
						catch(final Exception e){
							Log.getLogger(event.getGuild()).error("Error executing command {}", command, e);
							final var builder = new EmbedBuilder();
							builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
							builder.setColor(Color.RED);
							builder.setTitle(translate(event.getGuild(), "listeners.commands.exception.title"));
							builder.addField(translate(event.getGuild(), "listeners.commands.exception.kind"), e.getClass().getName(), false);
							Actions.sendEmbed(event.getChannel(), builder.build());
						}
					}, () -> {
						final var builder = new EmbedBuilder();
						builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
						builder.setColor(Color.ORANGE);
						builder.setTitle(translate(event.getGuild(), "listeners.commands.not-found.title"));
						builder.addField(translate(event.getGuild(), "listeners.commands.exception.command"), cmdText, false);
						Actions.sendEmbed(event.getChannel(), builder.build()).thenAccept(message -> ScheduleUtils.addSchedule(new DeleteMessageScheduleConfiguration(event.getAuthor(), ZonedDateTime.now().plusMinutes(2), message), event.getGuild()));
					});
					if(!messageDeleted.get()){
						Actions.deleteMessage(event.getMessage());
					}
				}
			}
			else if(Settings.get(event.getGuild()).getReactionsConfiguration().getAutoTodoChannels().stream().anyMatch(channelConfiguration -> Objects.equals(channelConfiguration.getChannelId(), event.getChannel().getIdLong()))){
				if(event.getMessage().getType() == MessageType.CHANNEL_PINNED_ADD){
					Actions.deleteMessage(event.getMessage());
				}
				else{
					Actions.addReaction(event.getMessage(), BasicEmotes.CHECK_OK.getValue());
					Actions.addReaction(event.getMessage(), BasicEmotes.PAPERCLIP.getValue());
					Actions.addReaction(event.getMessage(), BasicEmotes.RIGHT_ARROW_CURVING_LEFT.getValue());
					Settings.get(event.getGuild()).addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(event.getMessage(), ReactionTag.TODO, Map.of(ReactionUtils.DELETE_KEY, Boolean.toString(true))));
				}
			}
			else if(Settings.get(event.getGuild()).getExternalTodos().getNotificationChannel().map(channelConfiguration -> Objects.equals(channelConfiguration.getChannelId(), event.getChannel().getIdLong())).orElse(false)){
				Actions.addReaction(event.getMessage(), BasicEmotes.CHECK_OK.getValue());
				Settings.get(event.getGuild()).addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(event.getMessage(), ReactionTag.EXTERNAL_TODO, Map.of(ReactionUtils.DELETE_KEY, Boolean.toString(false))));
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("Error handling message", e);
		}
	}
	
	@Override
	public void onPrivateMessageReceived(@NonNull PrivateMessageReceivedEvent event){
		super.onPrivateMessageReceived(event);
		try{
			if(event.getAuthor().equals(event.getJDA().getSelfUser())){
				return;
			}
			Log.getLogger(null).info("Received private message from {}: {}", event.getAuthor(), event.getMessage());
			UselessFactsUtils.getFact().ifPresentOrElse(fact -> {
				Log.getLogger(null).debug("Sending random fact: {}", fact);
				var builder = Utilities.buildEmbed(event.getJDA().getSelfUser(), Color.GREEN, "Random fact", null);
				fact.fillEmbed(builder);
				Actions.sendPrivateMessage(null, event.getChannel(), "I don't really know what to answer, but here's a random fact for you", builder.build());
			}, () -> Actions.sendPrivateMessage(null, event.getChannel(), "I just farted", null));
		}
		catch(final Exception e){
			Log.getLogger(null).error("Error private message from {}", event.getAuthor(), e);
		}
	}
	
	/**
	 * Tell if this text is a command.
	 *
	 * @param guild The guild.
	 * @param text  The text.
	 *
	 * @return True if a command, false otherwise.
	 */
	private static boolean isCommand(@NonNull final Guild guild, @NonNull final String text){
		return text.startsWith(Settings.get(guild).getPrefix().orElse(defaultPrefix));
	}
	
	/**
	 * get the command associated to this string.
	 *
	 * @param commandText The command text.
	 *
	 * @return The command or null if not found.
	 */
	@NonNull
	private Optional<Command> getCommand(@NonNull final String commandText){
		return this.commands.stream().filter(command -> command.getCommandStrings().contains(commandText)).findFirst();
	}
}