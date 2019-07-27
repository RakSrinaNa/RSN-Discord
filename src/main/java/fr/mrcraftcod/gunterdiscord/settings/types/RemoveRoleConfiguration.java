package fr.mrcraftcod.gunterdiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.mrcraftcod.gunterdiscord.utils.json.LocalDateTimeDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.json.LocalDateTimeSerializer;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.annotation.Nonnull;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemoveRoleConfiguration{
	@JsonProperty("user")
	private UserConfiguration user;
	@JsonProperty("role")
	private RoleConfiguration role;
	@JsonProperty("date")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime date;
	
	public RemoveRoleConfiguration(){
	}
	
	public RemoveRoleConfiguration(@Nonnull User user, @Nonnull Role role, @Nonnull LocalDateTime date){
		this.user = new UserConfiguration(user);
		this.role = new RoleConfiguration(role);
		this.date = date;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(getUser()).append(getRole()).toHashCode();
	}
	
	public LocalDateTime getEndDate(){
		return date;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof RemoveRoleConfiguration)){
			return false;
		}
		RemoveRoleConfiguration that = (RemoveRoleConfiguration) o;
		return new EqualsBuilder().append(getUser(), that.getUser()).append(getRole(), that.getRole()).isEquals();
	}
	
	public UserConfiguration getUser(){
		return user;
	}
	
	public RoleConfiguration getRole(){
		return this.role;
	}
	
	public void setEndDate(@Nonnull LocalDateTime date){
		this.date = date;
	}
}