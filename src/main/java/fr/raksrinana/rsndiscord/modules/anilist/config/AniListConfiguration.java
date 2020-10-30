package fr.raksrinana.rsndiscord.modules.anilist.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.modules.settings.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.types.UserConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class AniListConfiguration implements ICompositeConfiguration{
	@JsonProperty("notificationsChannel")
	@Setter
	private ChannelConfiguration notificationsChannel;
	@JsonProperty("mediaChangeChannel")
	@Setter
	private ChannelConfiguration mediaChangeChannel;
	@JsonProperty("thaChannel")
	@Setter
	private ChannelConfiguration thaChannel;
	@JsonProperty("thaUser")
	@Setter
	private UserConfiguration thaUser;
	
	@NonNull
	public Optional<ChannelConfiguration> getMediaChangeChannel(){
		return Optional.ofNullable(this.mediaChangeChannel);
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getNotificationsChannel(){
		return Optional.ofNullable(this.notificationsChannel);
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getThaChannel(){
		return Optional.ofNullable(this.thaChannel);
	}
	
	@NonNull
	public Optional<UserConfiguration> getThaUser(){
		return Optional.ofNullable(this.thaUser);
	}
}