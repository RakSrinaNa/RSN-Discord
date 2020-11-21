package fr.raksrinana.rsndiscord.utils;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.modules.schedule.config.UnbanScheduleConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.entities.UserById;
import java.awt.Color;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

/**
 * Actions to do with JDA, logging them.
 */
@Slf4j
public class Actions{
	/**
	 * Reply to a message event.
	 *
	 * @param event   The message event to reply to.
	 * @param message The message to send.
	 * @param embed   The embed to attach along the message (see {{@link #sendMessage(TextChannel, CharSequence, MessageEmbed)}}).
	 *
	 * @return A completable future of a message.
	 *
	 * @see #sendMessage(TextChannel, CharSequence, MessageEmbed)
	 */
	@NonNull
	@Deprecated
	public static CompletableFuture<Message> reply(@NonNull final GenericGuildMessageEvent event, @NonNull final CharSequence message, MessageEmbed embed){
		return sendMessage(event.getChannel(), message, embed);
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send the message to.
	 * @param message The message to send.
	 * @param embed   The embed to attach along the message (see {@link net.dv8tion.jda.api.requests.restaction.MessageAction#embed(MessageEmbed)}).
	 *
	 * @return A completable future of a message (see {@link RestAction#submit()}).
	 *
	 * @see #sendMessage(TextChannel, CharSequence, MessageEmbed, boolean)
	 */
	@NonNull
	@Deprecated
	public static CompletableFuture<Message> sendMessage(@NonNull final TextChannel channel, @NonNull final CharSequence message, MessageEmbed embed){
		return sendMessage(channel, message, embed, false);
	}
	
	/**
	 * Send a message to a channel.
	 * If the message is too long (greater than {@link Message#MAX_CONTENT_LENGTH}) and that allowSplitting is true, it'll be split into several messages and only the last future will be returned.
	 *
	 * @param channel        The channel to send the message to.
	 * @param message        The message to send.
	 * @param embed          The embed to attach along the message (see {@link net.dv8tion.jda.api.requests.restaction.MessageAction#embed(MessageEmbed)}).
	 * @param allowSplitting Tell if the message can be split when too long.
	 *
	 * @return A completable future of a message (see {@link RestAction#submit()}).
	 */
	@NonNull
	@Deprecated
	public static CompletableFuture<Message> sendMessage(@NonNull final TextChannel channel, @NonNull final CharSequence message, MessageEmbed embed, boolean allowSplitting){
		return sendMessage(channel, message, embed, allowSplitting, messageAction -> messageAction);
	}
	
	/**
	 * Send a message to a channel.
	 * If the message is too long (greater than {@link Message#MAX_CONTENT_LENGTH}) and that allowSplitting is true, it'll be split into several messages and only the last future will be returned.
	 *
	 * @param channel               The channel to send the message to.
	 * @param message               The message to send.
	 * @param embed                 The embed to attach along the message (see {@link net.dv8tion.jda.api.requests.restaction.MessageAction#embed(MessageEmbed)}).
	 * @param allowSplitting        Tell if the message can be split when too long.
	 * @param messageActionFunction A function to add more elements to the message.
	 *
	 * @return A completable future of a message (see {@link RestAction#submit()}).
	 */
	@NonNull
	@Deprecated
	public static CompletableFuture<Message> sendMessage(@NonNull final TextChannel channel, @NonNull final CharSequence message, MessageEmbed embed, boolean allowSplitting, Function<MessageAction, MessageAction> messageActionFunction){
		Log.getLogger(channel.getGuild()).info("Sending message to {} : {}", channel, message);
		var buildUnique = false;
		final var actionsToSend = new ArrayList<MessageAction>();
		if(allowSplitting && message.length() >= Message.MAX_CONTENT_LENGTH){
			Log.getLogger(channel.getGuild()).info("Message is too long ({} >= {}), splitting it if possible", message.length(), Message.MAX_CONTENT_LENGTH);
			final var newLine = "\n";
			final var lines = new LinkedList<>(Arrays.asList(message.toString().split(newLine)));
			while(!lines.isEmpty()){
				var currentMessage = new StringBuilder();
				while(!lines.isEmpty() && currentMessage.length() + lines.peek().length() + newLine.length() < Message.MAX_CONTENT_LENGTH){
					currentMessage.append(lines.pop()).append(newLine);
				}
				if(currentMessage.length() > 0){
					final var messageBuilder = channel.sendMessage(currentMessage);
					if(lines.isEmpty()){
						actionsToSend.add(messageBuilder.embed(embed));
					}
					else{
						actionsToSend.add(messageBuilder);
					}
				}
				else if(!lines.isEmpty() && lines.peek().length() + newLine.length() >= Message.MAX_CONTENT_LENGTH){
					Log.getLogger(channel.getGuild()).warn("This message has a line that is too long, can't process it and is therefore removed: {}", lines.pop());
				}
				else{
					Log.getLogger(channel.getGuild()).error("Got in a state that shouldn't be possible, lines: {}", lines);
					break;
				}
			}
		}
		else{
			buildUnique = true;
		}
		if(buildUnique || actionsToSend.isEmpty()){
			actionsToSend.add(channel.sendMessage(message).embed(embed));
		}
		var lastSent = new CompletableFuture<Message>();
		for(int i = 0; i < actionsToSend.size(); i++){
			var action = actionsToSend.get(i);
			if(i == actionsToSend.size() - 1){
				action = messageActionFunction.apply(action);
			}
			lastSent = action.submit();
		}
		return lastSent;
	}
	
	@NonNull
	@Deprecated
	public static CompletableFuture<Message> forwardMessage(Message source, TextChannel toChannel){
		Log.getLogger(toChannel.getGuild()).info("Forwarding message {} to channel {}", source, toChannel);
		var action = toChannel.sendMessage(source);
		for(Message.Attachment attachment : source.getAttachments()){
			final var finalAction = action;
			try{
				action = attachment.retrieveInputStream()
						.thenApply(is -> finalAction.addFile(is, attachment.getFileName()))
						.get(30, TimeUnit.SECONDS);
			}
			catch(InterruptedException | ExecutionException | TimeoutException e){
				Log.getLogger(toChannel.getGuild()).error("Failed to forward attachment {}", attachment, e);
				action = action.append("\n<Attachment ")
						.append(attachment.getFileName())
						.append(".")
						.append(attachment.getFileExtension() == null ? "" : attachment.getFileExtension())
						.append(">");
			}
		}
		return action.submit();
	}
	
	/**
	 * Remove a role from a user.
	 *
	 * @param member The user to remove the role from.
	 * @param role   The role to remove.
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	@NonNull
	@Deprecated
	public static CompletableFuture<Void> removeRole(@NonNull final Member member, @NonNull final Role role){
		Log.getLogger(member.getGuild()).info("Removing role {} from {}", role, member);
		return member.getGuild().removeRoleFromMember(member, role).submit();
	}
	
	/**
	 * Delete a message.
	 *
	 * @param message The message to delete.
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	@NonNull
	@Deprecated
	public static CompletableFuture<Void> deleteMessage(@NonNull final Message message){
		Log.getLogger(message.getGuild()).info("Deleting message {}", message);
		return message.delete().submit();
	}
	
	/**
	 * Delete a channel.
	 *
	 * @param channel The channel to delete.
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	@NonNull
	@Deprecated
	public static CompletableFuture<Void> deleteChannel(@NonNull final GuildChannel channel){
		Log.getLogger(channel.getGuild()).info("Deleting channel {}", channel);
		return channel.delete().submit();
	}
	
	/**
	 * Give roles to the user.
	 *
	 * @param member The member to add the role to.
	 * @param role   The role to add.
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	@NonNull
	@Deprecated
	public static CompletableFuture<Void> giveRole(@NonNull final Member member, @NonNull final Role role){
		Log.getLogger(member.getGuild()).info("Adding role {} to {}", role, member);
		return member.getGuild().addRoleToMember(member, role).submit();
	}
	
	/**
	 * Add a reaction to a message.
	 *
	 * @param message The message to add the reaction to.
	 * @param emote   The reaction emote to add.
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	@Deprecated
	public static CompletableFuture<Void> addReaction(@NonNull Message message, @NonNull String emote){
		Log.getLogger(message.getGuild()).info("Adding reaction {} to {}", emote, message);
		return message.addReaction(emote).submit();
	}
	
	/**
	 * Add a reaction to a message.
	 *
	 * @param message The message to add the reaction to.
	 * @param emote   The reaction emote to add.
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	@Deprecated
	public static CompletableFuture<Void> addReaction(@NonNull Message message, @NonNull Emote emote){
		Log.getLogger(message.getGuild()).info("Adding reaction {} to {}", emote, message);
		return message.addReaction(emote).submit();
	}
	
	/**
	 * Modifies the content of a message.
	 *
	 * @param message    The message to edit.
	 * @param newMessage The new content for the message.
	 *
	 * @return A completable future of a message (see {@link RestAction#submit()}).
	 */
	@Deprecated
	public static CompletableFuture<Message> editMessage(@NonNull Message message, @NonNull String newMessage){
		Log.getLogger(message.getGuild()).info("Editing message {} with {}", message, newMessage);
		return message.editMessage(newMessage).submit();
	}
	
	/**
	 * Modifies the content of a message.
	 *
	 * @param message  The message to edit.
	 * @param newEmbed The new embed for the message.
	 *
	 * @return A completable future of a message (see {@link RestAction#submit()}).
	 */
	@Deprecated
	public static CompletableFuture<Message> editMessage(@NonNull Message message, @NonNull MessageEmbed newEmbed){
		Log.getLogger(message.getGuild()).info("Editing message {} with {}", message, newEmbed);
		return message.editMessage(newEmbed).submit();
	}
	
	/**
	 * Remove all reactions from a message.
	 *
	 * @param message The message to clear the reactions from.
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	@Deprecated
	public static CompletableFuture<Void> clearReactions(@NonNull Message message){
		Log.getLogger(message.getGuild()).info("Clearing reactions on {}", message);
		return message.clearReactions().submit();
	}
	
	/**
	 * Unpin a message.
	 *
	 * @param message The message to unpin.
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	@Deprecated
	public static CompletableFuture<Void> unpin(@NonNull Message message){
		Log.getLogger(message.getGuild()).info("Unpinning {}", message);
		return message.unpin().submit();
	}
	
	/**
	 * Remove a reaction from a message.
	 *
	 * @param reaction The reaction to delete.
	 * @param user     The user that put this reaction.
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	@Deprecated
	public static CompletableFuture<Void> removeReaction(@NonNull MessageReaction reaction, @NonNull User user){
		Log.getLogger(reaction.getGuild()).info("Removing reaction {} from {}", reaction.getReactionEmote().getName(), user);
		return reaction.removeReaction(user).submit();
	}
	
	/**
	 * Sets the category of a channel and sync its permissions.
	 *
	 * @param channel  The channel to move.
	 * @param category The category to move it into and sync with.
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	@Deprecated
	public static CompletableFuture<Void> setCategoryAndSync(@NonNull GuildChannel channel, @NonNull Category category){
		Log.getLogger(channel.getGuild()).info("Archiving channel {} to {} and syncing it", channel, category);
		return channel.getManager().setParent(category).sync(category).submit();
	}
	
	/**
	 * Send a message to a private channel.
	 *
	 * @param guild   The guild issuing the request.
	 * @param userId  The userId to send the message to.
	 * @param message The message to send.
	 * @param embed   The embed to attach along the message (see {@link net.dv8tion.jda.api.requests.restaction.MessageAction#embed(MessageEmbed)}).
	 *
	 * @return A completable future of a message (see {@link RestAction#submit()}).
	 *
	 * @throws IllegalArgumentException If trying to send a message to another bot.
	 */
	@Deprecated
	public static CompletableFuture<Message> sendPrivateMessage(Guild guild, long userId, String message, MessageEmbed embed){
		return Main.getJda().openPrivateChannelById(userId).submit().thenCompose(channel -> sendPrivateMessage(guild, channel, message, embed));
	}
	
	/**
	 * Send a message to a private channel.
	 *
	 * @param guild   The guild that initiated the event.
	 * @param channel The channel to send to.
	 * @param message The message to send.
	 * @param embed   The embed to attach along the message (see {@link net.dv8tion.jda.api.requests.restaction.MessageAction#embed(MessageEmbed)}).
	 *
	 * @return A completable future of a message (see {@link RestAction#submit()}).
	 *
	 * @throws IllegalArgumentException If trying to send a message to another bot.
	 */
	@NonNull
	@Deprecated
	public static CompletableFuture<Message> sendPrivateMessage(final Guild guild, @NonNull final PrivateChannel channel, @NonNull final CharSequence message, MessageEmbed embed) throws IllegalArgumentException{
		if(channel.getUser().isBot()){
			throw new IllegalArgumentException(MessageFormat.format("Cannot send private message to other bot {0} : {1}", channel, message));
		}
		Log.getLogger(guild).info("Sending private message to {} : {}", channel, message);
		return channel.sendMessage(message).embed(embed).submit();
	}
	
	/**
	 * Send a message to a private channel.
	 *
	 * @param guild   The guild issuing the request.
	 * @param user    The user to send the message to.
	 * @param message The message to send.
	 * @param embed   The embed to attach along the message (see {@link net.dv8tion.jda.api.requests.restaction.MessageAction#embed(MessageEmbed)}).
	 *
	 * @return A completable future of a message (see {@link RestAction#submit()}).
	 *
	 * @throws IllegalArgumentException If trying to send a message to another bot.
	 */
	@NonNull
	@Deprecated
	public static CompletableFuture<Message> sendPrivateMessage(Guild guild, User user, String message, MessageEmbed embed){
		return user.openPrivateChannel().submit().thenCompose(privateChannel -> sendPrivateMessage(guild, privateChannel, message, embed));
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel        The channel to send the message to.
	 * @param messageBuilder The message to send.
	 *
	 * @return A completable future of a message (see {@link RestAction#submit()}).
	 */
	@NonNull
	@Deprecated
	public static CompletableFuture<Message> sendMessage(@NonNull final TextChannel channel, @NonNull MessageBuilder messageBuilder){
		final var action = channel.sendMessage(messageBuilder.build());
		Log.getLogger(channel.getGuild()).info("Sending message to {} : {}", channel, action);
		return action.submit();
	}
	
	/**
	 * Soft bans a member from its server.
	 *
	 * @param author The author of the ban.
	 * @param member The member to ban.
	 * @param reason The reason of the ban.
	 *
	 * @return A completable future.
	 */
	@NonNull
	@Deprecated
	public static CompletableFuture<Void> softBan(@NonNull TextChannel textChannel, @NonNull User author, @NonNull Member member, @NonNull String reason, @NonNull Duration duration){
		var guild = member.getGuild();
		var builder = Utilities.buildEmbed(member.getUser(), Color.RED, "Soft ban", null);
		builder.addField(translate(guild, "log-action.user"), member.getUser().getAsTag(), true);
		builder.addField(translate(guild, "log-action.moderator"), author.getAsMention(), true);
		builder.addField(translate(guild, "log-action.reason"), reason, true);
		builder.addField(translate(guild, "log-action.length"), Utilities.durationToString(duration), true);
		logAction(guild, builder.build());
		
		ScheduleUtils.addSchedule(guild, new UnbanScheduleConfiguration(member.getUser(), textChannel, ZonedDateTime.now().plus(duration), "Banned for: " + reason, member.getId()));
		return member.ban(0, reason).submit();
	}
	
	@NonNull
	@Deprecated
	public static Optional<CompletableFuture<Message>> logAction(@NonNull Guild guild, MessageEmbed embed){
		return Settings.get(guild)
				.getLogChannel()
				.flatMap(ChannelConfiguration::getChannel)
				.map(textChannel -> Actions.sendEmbed(textChannel, embed));
	}
	
	/**
	 * Send an embed to a channel.
	 *
	 * @param channel The channel to send the message to.
	 * @param embed   The embed to attach along the message (see {@link net.dv8tion.jda.api.requests.restaction.MessageAction#embed(MessageEmbed)}).
	 *
	 * @return A completable future of a message (see {@link RestAction#submit()}).
	 */
	@NonNull
	@Deprecated
	public static CompletableFuture<Message> sendEmbed(@NonNull final TextChannel channel, @NonNull MessageEmbed embed){
		Log.getLogger(channel.getGuild()).info("Sending embed to {} : {}", channel, embed);
		return channel.sendMessage(embed).submit();
	}
	
	/**
	 * Unbans a member from its server.
	 *
	 * @param guild  The guild to unban the user from.
	 * @param userId The user to unban.
	 *
	 * @return A completable future.
	 */
	@Deprecated
	public static CompletableFuture<Void> unban(@NonNull Guild guild, @NonNull String userId){
		var builder = Utilities.buildEmbed(guild.getJDA().getSelfUser(), Color.GREEN, "Unban", null);
		builder.addField(translate(guild, "log-action.user"), new UserById(Long.parseLong(userId)).getAsMention(), true);
		builder.addField(translate(guild, "log-action.reason"), "Auto", true);
		logAction(guild, builder.build());
		
		return guild.unban(userId).submit();
	}
	
	/**
	 * Deletes a message.
	 *
	 * @param channel   The channel the message is in.
	 * @param messageId The id of the message to delete.
	 *
	 * @return A completable future.
	 */
	@Deprecated
	public static CompletableFuture<Void> deleteMessage(@NonNull TextChannel channel, long messageId){
		Log.getLogger(channel.getGuild()).info("Deleting message with id {} in channel {}", messageId, channel);
		
		return channel.deleteMessageById(messageId).submit();
	}
}
