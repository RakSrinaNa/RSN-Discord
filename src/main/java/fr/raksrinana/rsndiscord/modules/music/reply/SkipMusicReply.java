package fr.raksrinana.rsndiscord.modules.music.reply;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.modules.schedule.config.DeleteMessageScheduleConfiguration;
import fr.raksrinana.rsndiscord.reply.BasicWaitingUserReply;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CHECK_OK;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.concurrent.TimeUnit.SECONDS;

public class SkipMusicReply extends BasicWaitingUserReply{
	private int votesRequired;
	private final AudioTrack audioTrack;
	
	public SkipMusicReply(final GuildMessageReceivedEvent event, final Message message, int votesRequired, AudioTrack audioTrack){
		super(event, event.getAuthor(), event.getChannel(), 20, SECONDS, message);
		this.votesRequired = votesRequired;
		this.audioTrack = audioTrack;
	}
	
	@Override
	protected boolean onExecute(@NonNull final GuildMessageReactionAddEvent event){
		if(Objects.equals(event.getUser(), event.getJDA().getSelfUser())){
			return false;
		}
		
		var guild = event.getGuild();
		var replyEmote = BasicEmotes.getEmote(event.getReactionEmote().getName());
		
		if(replyEmote != CHECK_OK){
			return false;
		}
		
		try{
			if(count(event)){
				Log.getLogger(guild).info("Vote successful, skipping");
				if(isSameTrack(event)){
					RSNAudioManager.skip(guild);
					event.getChannel().sendMessage(translate(guild, "music.skipped", "@everyone"))
							.allowedMentions(List.of())
							.submit();
				}
				else{
					Log.getLogger(guild).info("Music isn't the same anymore, didn't skip");
				}
				return true;
			}
		}
		catch(InterruptedException | ExecutionException | TimeoutException e){
			throw new RuntimeException("Failed to process vote", e);
		}
		return false;
	}
	
	private boolean count(GuildMessageReactionAddEvent event) throws InterruptedException, ExecutionException, TimeoutException{
		var count = event.retrieveMessage().submit()
				.thenApply(message -> message.getReactions().stream()
						.filter(r -> BasicEmotes.getEmote(r.getReactionEmote().getName()) == CHECK_OK)
						.mapToInt(MessageReaction::getCount)
						.sum())
				.get(30, SECONDS);
		Log.getLogger(event.getGuild()).debug("{}/{} votes to skip current music", count, votesRequired);
		return count >= votesRequired;
	}
	
	@NonNull
	private Boolean isSameTrack(@NonNull GuildMessageReactionAddEvent event){
		return RSNAudioManager.currentTrack(event.getGuild())
				.map(track -> Objects.equals(track, audioTrack))
				.orElse(false);
	}
	
	@Override
	public boolean onExpire(){
		stop();
		return true;
	}
	
	private void stop(){
		var channel = this.getWaitChannel();
		var guild = channel.getGuild();
		
		Log.getLogger(guild).info("Vote note successful, music not skipped");
		
		channel.sendMessage(translate(guild, "music.skip.timeout")).submit();
		
		var deleteMessageScheduleConfiguration = new DeleteMessageScheduleConfiguration(channel.getJDA().getSelfUser(),
				ZonedDateTime.now().plusMinutes(5), channel, getOriginalMessageId());
		ScheduleUtils.addSchedule(guild, deleteMessageScheduleConfiguration);
	}
	
	@Override
	public boolean handleEvent(final GuildMessageReactionAddEvent event){
		return Objects.equals(this.getWaitChannel(), event.getChannel()) && Objects.equals(this.getEmoteMessageId(), event.getMessageIdLong());
	}
	
	@Override
	protected boolean onExecute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		return false;
	}
}
