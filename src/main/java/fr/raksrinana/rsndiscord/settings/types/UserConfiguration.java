package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.AtomicConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class UserConfiguration implements AtomicConfiguration{
	@JsonProperty("userId")
	@Setter
	private long userId;
	
	public UserConfiguration(final User user){
		this(user.getIdLong());
	}
	
	public UserConfiguration(final long userId){
		this.userId = userId;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.getUserId()).toHashCode();
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof UserConfiguration)){
			return false;
		}
		final var that = (UserConfiguration) o;
		return new EqualsBuilder().append(this.getUserId(), that.getUserId()).isEquals();
	}
	
	@Override
	public String toString(){
		return this.getUser().map(User::getAsMention).orElse("<Unknown user>");
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return getUser().isPresent();
	}
	
	@NonNull
	public Optional<User> getUser(){
		return Optional.ofNullable(Main.getJda().getUserById(this.getUserId()));
	}
	
	public void setUser(@NonNull final User user){
		this.setUserId(user.getIdLong());
	}
}
