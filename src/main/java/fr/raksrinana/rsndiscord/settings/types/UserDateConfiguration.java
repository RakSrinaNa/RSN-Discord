package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeSerializer;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-06-23.
 *
 * @author Thomas Couchoud
 * @since 2019-06-23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDateConfiguration{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	@JsonProperty("userId")
	private long userId;
	@JsonProperty("date")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime date;
	
	public UserDateConfiguration(){
	}
	
	public UserDateConfiguration(@Nonnull final User user, @Nonnull final LocalDateTime date){
		this(user.getIdLong(), date);
	}
	
	private UserDateConfiguration(final long userId, @Nonnull final LocalDateTime date){
		this.userId = userId;
		this.date = date;
	}
	
	@Override
	public String toString(){
		return this.getUser().map(User::getAsMention).map(s -> s + " " + this.getDate().format(DF)).orElse("<Unknown date>");
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.getUserId()).append(this.getDate()).toHashCode();
	}
	
	@Nonnull
	public LocalDateTime getDate(){
		return this.date;
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof UserDateConfiguration)){
			return false;
		}
		final var that = (UserDateConfiguration) o;
		return new EqualsBuilder().append(this.getUserId(), that.getUserId()).append(this.getDate(), that.getDate()).isEquals();
	}
	
	public long getUserId(){
		return this.userId;
	}
	
	@Nonnull
	private Optional<User> getUser(){
		return Optional.ofNullable(Main.getJDA().getUserById(this.getUserId()));
	}
	
	public void setDate(@Nonnull final LocalDateTime date){
		this.date = date;
	}
}