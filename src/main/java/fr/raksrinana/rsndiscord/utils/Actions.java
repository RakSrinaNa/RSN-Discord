package fr.raksrinana.rsndiscord.utils;

import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.requests.RestAction;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;

/**
 * Actions to do with JDA, logging them.
 */
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
	 */
	@NonNull
	public static CompletableFuture<Message> sendMessage(@NonNull final TextChannel channel, @NonNull final CharSequence message, MessageEmbed embed){
		Log.getLogger(channel.getGuild()).info("Sending message to {} : {}", channel, message);
		return new MessageBuilder().sendTo(channel).append(message).embed(embed).submit();
	}
	
	/**
	 * Send a message to a user.
	 *
	 * @param guild   The guild that initiated the event.
	 * @param user    The user to send to.
	 * @param message The message to send.
	 *
	 * @return A completable future of a message.
	 *
	 * @see #sendPrivateMessage(Guild, PrivateChannel, CharSequence, MessageEmbed)
	 */
	@NonNull
	public static CompletableFuture<Message> replyPrivate(final Guild guild, @NonNull final User user, @NonNull final CharSequence message, MessageEmbed embed){
		return user.openPrivateChannel().submit().thenCompose(channel -> sendPrivateMessage(guild, channel, message, embed));
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
	public static CompletableFuture<Message> sendPrivateMessage(final Guild guild, @NonNull final PrivateChannel channel, @NonNull final CharSequence message, MessageEmbed embed) throws IllegalArgumentException{
		if(channel.getUser().isBot()){
			throw new IllegalArgumentException(MessageFormat.format("Cannot send private message to other bot {0} : {1}", channel, message));
		}
		Log.getLogger(guild).info("Sending private message to {} : {}", channel, message);
		return new MessageBuilder().sendTo(channel).append(message).embed(embed).submit();
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
	public static CompletableFuture<Void> deleteMessage(@NonNull final Message message){
		Log.getLogger(message.getGuild()).info("Deleting message {}", message);
		return message.delete().submit();
	}
	
	/**
	 * Send a file to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param path    The file to send.
	 *
	 * @return A completable future of a message.
	 *
	 * @see #sendFile(TextChannel, InputStream, String)
	 */
	@NonNull
	public static CompletableFuture<Message> sendFile(final TextChannel channel, final Path path) throws IOException{
		Log.getLogger(channel.getGuild()).debug("Sending file {} to {}", path, channel);
		return sendFile(channel, Files.newInputStream(path), path.getFileName().toString());
	}
	
	/**
	 * Send a file to a channel.
	 * Closes the stream when done.
	 *
	 * @param channel     The channel to send to.
	 * @param inputStream The file data to send.
	 * @param name        The name of the file.
	 *
	 * @return A completable future of a message (see {@link RestAction#submit()}).
	 */
	@NonNull
	public static CompletableFuture<Message> sendFile(final TextChannel channel, final InputStream inputStream, final String name) throws IOException{
		Log.getLogger(channel.getGuild()).info("Sending file {} to {}", name, channel);
		try(final var is = inputStream){
			return new MessageBuilder().sendTo(channel).addFile(is, name).submit();
		}
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
	public static CompletableFuture<Void> giveRole(@NonNull final Member member, @NonNull final Role role){
		Log.getLogger(member.getGuild()).info("Adding role {} to {}", role, member);
		return member.getGuild().addRoleToMember(member, role).submit();
	}
	
	/**
	 * Set the deaf status of a member.
	 *
	 * @param member The member to set the deaf status.
	 * @param status True if deaf, false is not deaf.
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	@NonNull
	public static CompletableFuture<Void> deafen(@NonNull final Member member, final boolean status){
		Log.getLogger(member.getGuild()).info("Setting deaf status to {} for {}", status, member);
		return member.getGuild().deafen(member, status).submit();
	}
	
	/**
	 * Set the mute status of a member.
	 *
	 * @param member The member to set the mute status.
	 * @param status True if muted, false is not muted.
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	public static CompletableFuture<Void> mute(@NonNull Member member, boolean status){
		Log.getLogger(member.getGuild()).info("Setting muted status to {} for {}", status, member);
		return member.getGuild().mute(member, status).submit();
	}
	
	/**
	 * Add a reaction to a message.
	 *
	 * @param message The message to add the reaction to.
	 * @param emote   The reaction emote to add.
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	public static CompletableFuture<Void> addReaction(@NonNull Message message, @NonNull String emote){
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
	public static CompletableFuture<Void> removeReaction(@NonNull MessageReaction reaction, @NonNull User user){
		Log.getLogger(reaction.getGuild()).info("Removing reaction {} from {}", reaction.getReactionEmote().getName(), user);
		return reaction.removeReaction(user).submit();
	}
	
	/**
	 * Pin a message.
	 *
	 * @param message The message to pin.
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	public static CompletableFuture<Void> pin(Message message){
		Log.getLogger(message.getGuild()).info("Pinning {}", message);
		return message.pin().submit();
	}
	
	/**
	 * Change the username of a member.
	 *
	 * @param member   The member to change the nickname.
	 * @param nickname The name to set (null to reset).
	 *
	 * @return A completable future (see {@link RestAction#submit()}).
	 */
	public static CompletableFuture<Void> changeNickname(@NonNull Member member, String nickname){
		return member.getGuild().modifyNickname(member, nickname).submit();
	}
}
