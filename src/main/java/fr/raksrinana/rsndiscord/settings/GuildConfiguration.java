package fr.raksrinana.rsndiscord.settings;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.schedule.api.IScheduleHandler;
import fr.raksrinana.rsndiscord.settings.guild.*;
import fr.raksrinana.rsndiscord.settings.guild.anilist.AniListConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.autoroles.LeavingRolesConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.birthday.BirthdaysConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.permission.PermissionsConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.reaction.ReactionsConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.json.converter.DurationDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.DurationSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class GuildConfiguration implements ICompositeConfiguration{
	@JsonProperty("locale")
	@Setter
	private Locale locale;
	@JsonProperty("aniList")
	@Getter
	private final AniListConfiguration aniListConfiguration = new AniListConfiguration();
	@JsonProperty("trakt")
	@Getter
	private final TraktConfiguration traktConfiguration = new TraktConfiguration();
	@JsonProperty("nickname")
	@Getter
	private final NicknameConfiguration nicknameConfiguration = new NicknameConfiguration();
	@JsonProperty("twitchConfiguration")
	@Getter
	private final TwitchConfiguration twitchConfiguration = new TwitchConfiguration();
	@JsonProperty("hermitcraft")
	@Getter
	private final HermitcraftConfiguration hermitcraftConfiguration = new HermitcraftConfiguration();
	@JsonProperty("rainbowSix")
	@Getter
	private final RainbowSixConfiguration rainbowSixConfiguration = new RainbowSixConfiguration();
	@JsonProperty("prefix")
	@Setter
	private String prefix;
	@JsonProperty("autoRoles")
	@Getter
	@Setter
	private Set<RoleConfiguration> autoRoles = new HashSet<>();
	@JsonProperty("moderatorRoles")
	@Getter
	@Setter
	private Set<RoleConfiguration> moderatorRoles = new HashSet<>();
	@JsonProperty("autoThumbChannels")
	@JsonAlias("ideaChannels")
	@Getter
	@Setter
	private Set<ChannelConfiguration> autoThumbsChannels = new HashSet<>();
	@JsonProperty("guildId")
	@Getter
	private long guildId;
	@JsonProperty("announceStartChannel")
	@Setter
	private ChannelConfiguration announceStartChannel;
	@JsonProperty("musicVolume")
	@Getter
	@Setter
	private int musicVolume = 100;
	@JsonProperty("archiveCategory")
	@Setter
	private CategoryConfiguration archiveCategory;
	@JsonProperty("messagesAwaitingReaction")
	@Setter
	private Set<WaitingReactionMessageConfiguration> messagesAwaitingReaction = new ConcurrentSkipListSet<>();
	@JsonProperty("reactions")
	@Getter
	@Setter
	private ReactionsConfiguration reactionsConfiguration = new ReactionsConfiguration();
	@JsonProperty("externalTodos")
	@Getter
	private final ExternalTodosConfiguration externalTodos = new ExternalTodosConfiguration();
	@JsonProperty("leavingRoles")
	@Getter
	private final LeavingRolesConfiguration leavingRolesConfiguration = new LeavingRolesConfiguration();
	@JsonProperty("generalChannel")
	@Setter
	private ChannelConfiguration generalChannel;
	@JsonProperty("logChannel")
	@Setter
	private ChannelConfiguration logChannel;
	@JsonProperty("randomKick")
	@Getter
	private final RandomKickConfiguration randomKick = new RandomKickConfiguration();
	@JsonProperty("birthdays")
	@Getter
	@Setter
	private BirthdaysConfiguration birthdays = new BirthdaysConfiguration();
	@JsonProperty("permissions")
	@Getter
	@Setter
	private PermissionsConfiguration permissionsConfiguration = new PermissionsConfiguration();
	@JsonProperty("event")
	@Setter
	@Getter
	private EventConfiguration eventConfiguration = new EventConfiguration();
	@JsonProperty("discordIncidentsChannel")
	@Setter
	private ChannelConfiguration discordIncidentsChannel;
	@JsonProperty("leaveServerBanDuration")
	@Setter
	@JsonDeserialize(using = DurationDeserializer.class)
	@JsonSerialize(using = DurationSerializer.class)
	private Duration leaveServerBanDuration;
	@JsonProperty("twitter")
	@Getter
	@Setter
	private TwitterConfiguration twitterConfiguration = new TwitterConfiguration();
	@JsonProperty("joinLeave")
	@Getter
	@Setter
	private JoinLeaveConfiguration joinLeaveConfiguration = new JoinLeaveConfiguration();
	@JsonProperty("mediaReactionMessages")
	@Getter
	@Setter
	private List<MessageConfiguration> mediaReactionMessages = new LinkedList<>();
	@JsonProperty("scheduleHandlers")
	@JsonAlias("scheduleActionHandlers")
	@Getter
	private Map<String, IScheduleHandler> scheduleHandlers = new ConcurrentHashMap<>();
	
	GuildConfiguration(long guildId){
		this.guildId = guildId;
	}
	
	public void addMessagesAwaitingReaction(@NotNull WaitingReactionMessageConfiguration reaction){
		messagesAwaitingReaction.add(reaction);
	}
	
	public void removeMessagesAwaitingReaction(@NotNull WaitingReactionMessageConfiguration reaction){
		messagesAwaitingReaction.remove(reaction);
	}
	
	public Collection<WaitingReactionMessageConfiguration> getMessagesAwaitingReaction(@NotNull ReactionTag tag){
		return new HashSet<>(messagesAwaitingReaction).stream()
				.filter(reaction -> Objects.equals(reaction.getTag(), tag))
				.collect(toSet());
	}
	
	public void add(@NotNull IScheduleHandler scheduleHandler){
		this.scheduleHandlers.put(scheduleHandler.getSchedulerId(), scheduleHandler);
	}
	
	public void removeScheduleHandler(@NotNull String id){
		scheduleHandlers.remove(id);
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getAnnounceStartChannel(){
		return ofNullable(announceStartChannel);
	}
	
	@NotNull
	public Optional<CategoryConfiguration> getArchiveCategory(){
		return ofNullable(archiveCategory);
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getDiscordIncidentsChannel(){
		return ofNullable(discordIncidentsChannel);
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getGeneralChannel(){
		return Optional.of(generalChannel);
	}
	
	@NotNull
	public Optional<Duration> getLeaveServerBanDuration(){
		return ofNullable(leaveServerBanDuration);
	}
	
	@NotNull
	public Optional<Locale> getLocale(){
		return ofNullable(locale);
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getLogChannel(){
		return ofNullable(logChannel);
	}
	
	@NotNull
	public Iterator<WaitingReactionMessageConfiguration> getMessagesAwaitingReaction(){
		return messagesAwaitingReaction.iterator();
	}
	
	@NotNull
	public Optional<String> getPrefix(){
		return ofNullable(prefix);
	}
}
