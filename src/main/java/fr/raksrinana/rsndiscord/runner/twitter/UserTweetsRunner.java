package fr.raksrinana.rsndiscord.runner.twitter;

import fr.raksrinana.rsndiscord.api.twitter.TwitterApi;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import fr.raksrinana.rsndiscord.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import twitter4j.Status;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class UserTweetsRunner implements IScheduledRunner{
	private final JDA jda;
	
	public UserTweetsRunner(@NonNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		jda.getGuilds().forEach(guild -> {
			var conf = Settings.get(guild).getTwitterConfiguration();
			conf.getUsersChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel ->
					conf.getUserIds().forEach(userId -> conf.getLastUserTweet(userId)
							.map(lastTweet -> TwitterApi.getUserLastTweets(userId, lastTweet))
							.orElseGet(() -> TwitterApi.getUserLastTweets(userId)).stream()
							.sorted(comparing(Status::getCreatedAt))
							.forEach(tweet -> {
								channel.sendMessage(String.format("https://twitter.com/%s/status/%s",
										URLEncoder.encode(tweet.getUser().getScreenName(), UTF_8),
										tweet.getId())
								).submit()
										.thenAccept(ScheduleUtils.deleteMessage(date -> date.plusHours(6)));
								conf.setLastUserTweet(userId, tweet.getId());
							})));
		});
	}
	
	@Override
	public long getDelay(){
		return 2;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Twitter user last tweets";
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
}