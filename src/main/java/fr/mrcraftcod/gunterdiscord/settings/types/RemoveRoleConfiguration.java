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
import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Optional;

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
	
	public LocalDateTime getEndDate(){
		return date;
	}
	
	public Optional<Role> getRole(){
		return this.role.getRole();
	}
	
	public Optional<User> getUser(){
		return user.getUser();
	}
	
	public long getUserId(){
		return this.user.getUserId();
	}
}
