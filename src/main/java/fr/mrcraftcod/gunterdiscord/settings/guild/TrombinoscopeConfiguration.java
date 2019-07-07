package fr.mrcraftcod.gunterdiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.settings.guild.trombinoscope.PhotoEntryConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.RoleConfiguration;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrombinoscopeConfiguration{
	@JsonProperty("photoChannel")
	private ChannelConfiguration photoChannel;
	@JsonProperty("participantRole")
	private RoleConfiguration participantRole;
	@JsonProperty("photos")
	private List<PhotoEntryConfiguration> photos = new ArrayList<>();
	
	public List<PhotoEntryConfiguration> getPhotos(User user){
		return this.photos.stream().filter(p -> Objects.equals(p.getUserId(), user.getIdLong())).collect(Collectors.toList());
	}
	
	public void removePhoto(@Nonnull User user, @Nonnull String photo){
		this.photos.removeIf(p -> Objects.equals(p.getUserId(), user.getIdLong()) && Objects.equals(p.getPhoto(), photo));
	}
	
	public void removePhoto(@Nonnull User user){
		this.photos.removeIf(p -> Objects.equals(p.getUserId(), user.getIdLong()));
	}
	
	@Nonnull
	public Optional<RoleConfiguration> getParticipantRole(){
		return Optional.ofNullable(this.participantRole);
	}
	
	public void setParticipantRole(@Nullable RoleConfiguration value){
		this.participantRole = value;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getPhotoChannel(){
		return Optional.ofNullable(this.photoChannel);
	}
	
	public void setPhotoChannel(@Nullable ChannelConfiguration value){
		this.photoChannel = value;
	}
}
