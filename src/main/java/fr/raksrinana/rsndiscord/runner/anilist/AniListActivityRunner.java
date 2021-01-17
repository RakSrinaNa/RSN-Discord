package fr.raksrinana.rsndiscord.runner.anilist;

import fr.raksrinana.rsndiscord.api.anilist.AniListApi;
import fr.raksrinana.rsndiscord.api.anilist.data.list.ListActivity;
import fr.raksrinana.rsndiscord.api.anilist.query.ActivityPagedQuery;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static java.util.concurrent.TimeUnit.HOURS;

public class AniListActivityRunner implements IAniListRunner<ListActivity, ActivityPagedQuery>{
	@Getter
	private final JDA jda;
	
	public AniListActivityRunner(@NonNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public Set<TextChannel> getChannels(){
		return getJda().getGuilds().stream()
				.flatMap(guild -> Settings.get(guild).getAniListConfiguration()
						.getMediaChangeChannel()
						.flatMap(ChannelConfiguration::getChannel)
						.stream())
				.collect(Collectors.toSet());
	}
	
	@NonNull
	@Override
	public ActivityPagedQuery initQuery(@NonNull Member member){
		return new ActivityPagedQuery(AniListApi.getUserId(member).orElseThrow(),
				Settings.getGeneral().getAniList().getLastAccess(getFetcherID(), member.getIdLong())
						.map(UserDateConfiguration::getDate)
						.orElse(AniListApi.getDefaultDate()));
	}
	
	@Override
	public boolean isKeepOnlyNew(){
		return true;
	}
	
	@NonNull
	@Override
	public String getFetcherID(){
		return "listActivity";
	}
	
	@Override
	public long getDelay(){
		return 6;
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return HOURS;
	}
	
	@Override
	public void execute(){
		runQueryOnDefaultUsersChannels();
	}
	
	@NonNull
	@Override
	public String getName(){
		return "AniList list activity";
	}
}