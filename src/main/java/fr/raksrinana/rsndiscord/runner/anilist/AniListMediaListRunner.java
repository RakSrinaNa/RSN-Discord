package fr.raksrinana.rsndiscord.runner.anilist;

import fr.raksrinana.rsndiscord.api.anilist.AniListApi;
import fr.raksrinana.rsndiscord.api.anilist.data.list.MediaList;
import fr.raksrinana.rsndiscord.api.anilist.data.media.IMedia;
import fr.raksrinana.rsndiscord.api.anilist.query.MediaListPagedQuery;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.anilist.AniListConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.ANILIST_TODO;
import static fr.raksrinana.rsndiscord.reaction.ReactionUtils.DELETE_KEY;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CHECK_OK;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.stream.Collectors.toList;

@ScheduledRunner
public class AniListMediaListRunner implements IAniListRunner<MediaList, MediaListPagedQuery>{
	private static final Collection<String> acceptedThaLists = Set.of("Tha");
	@Getter
	private final JDA jda;
	
	public AniListMediaListRunner(@NotNull JDA jda){
		this.jda = jda;
	}
	
	private static Collection<WaitingReactionMessageConfiguration> getSimilarWaitingReactions(@NotNull TextChannel channel, @NotNull IMedia media){
		var footer = "ID: " + media.getId();
		return Settings.get(channel.getGuild()).getMessagesAwaitingReaction(ANILIST_TODO).stream()
				.filter(reaction -> {
					if(Objects.equals(reaction.getMessage().getChannel().getChannelId(), channel.getIdLong())){
						return reaction.getMessage().getMessage()
								.map(message -> isSameMedia(footer, reaction, message))
								.orElse(false);
					}
					return false;
				}).collect(toList());
	}
	
	@NotNull
	private static Boolean isSameMedia(String footer, WaitingReactionMessageConfiguration reaction, Message message){
		var isDeleteMode = ofNullable(reaction.getData().get(DELETE_KEY)).map(Boolean::parseBoolean).orElse(false);
		var isSameMedia = message.getEmbeds().stream()
				.anyMatch(embed -> Objects.equals(embed.getTitle(), "User list information")
						&& Objects.equals(ofNullable(embed.getFooter()).map(MessageEmbed.Footer::getText).orElse(null), footer));
		return isSameMedia && isDeleteMode;
	}
	
	private void sendMediaList(TextChannel channel, Member member, MediaList mediaList){
		JDAWrappers.message(channel, member.getAsMention())
				.embed(buildMessage(channel.getGuild(), member.getUser(), mediaList))
				.submit()
				.thenAccept(message -> {
					JDAWrappers.addReaction(message, CHECK_OK).submit();
					
					var reactionMessageConfiguration = new WaitingReactionMessageConfiguration(message, ANILIST_TODO,
							Map.of(DELETE_KEY, Boolean.toString(true)));
					Settings.get(channel.getGuild()).addMessagesAwaitingReaction(reactionMessageConfiguration);
				});
	}
	
	@Override
	public void execute(){
		runQueryOnDefaultUsersChannels();
	}
	
	@NotNull
	@Override
	public String getName(){
		return "AniList media list";
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return HOURS;
	}
	
	@Override
	@NotNull
	public Set<TextChannel> getChannels(){
		return getJda().getGuilds().stream()
				.flatMap(g -> Settings.get(g).getAniListConfiguration()
						.getMediaChangeChannel()
						.flatMap(ChannelConfiguration::getChannel)
						.stream())
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}
	
	@NotNull
	@Override
	public String getFetcherID(){
		return "medialist";
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@Override
	public void sendMessages(@NotNull Set<TextChannel> channels, @NotNull Map<User, Set<MediaList>> userElements){
		IAniListRunner.super.sendMessages(channels, userElements);
		var thaChannels = getJda().getGuilds().stream()
				.map(Settings::get)
				.map(GuildConfiguration::getAniListConfiguration)
				.map(AniListConfiguration::getThaChannel)
				.flatMap(Optional::stream)
				.map(ChannelConfiguration::getChannel)
				.flatMap(Optional::stream)
				.collect(Collectors.toSet());
		var mediaListsToSend = userElements.values().stream().flatMap(Set::stream)
				.filter(mediaList -> mediaList.getCustomLists()
						.entrySet().stream()
						.filter(Map.Entry::getValue)
						.map(Map.Entry::getKey)
						.anyMatch(acceptedThaLists::contains))
				.collect(toList());
		thaChannels.forEach(channelToSend -> Settings.get(channelToSend.getGuild())
				.getAniListConfiguration()
				.getThaUser()
				.flatMap(UserConfiguration::getUser)
				.map(channelToSend.getGuild()::retrieveMember)
				.map(RestAction::complete)
				.ifPresent(memberToSend -> mediaListsToSend.stream()
						.sorted()
						.forEach(mediaListToSend -> sendMediaList(channelToSend, memberToSend, mediaListToSend))));
	}
	
	@NotNull
	@Override
	public MediaListPagedQuery initQuery(@NotNull Member member){
		return new MediaListPagedQuery(AniListApi.getUserId(member).orElseThrow());
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@Override
	public boolean isKeepOnlyNew(){
		return true;
	}
}
