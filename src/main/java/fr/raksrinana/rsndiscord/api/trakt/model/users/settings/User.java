package fr.raksrinana.rsndiscord.api.trakt.model.users.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.converter.ISO8601ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class User{
	@JsonProperty("username")
	private String username;
	@JsonProperty("private")
	private boolean userPrivate;
	@JsonProperty("name")
	private String name;
	@JsonProperty("vip")
	private boolean vip;
	@JsonProperty("vip_ep")
	private boolean vipEp;
	@JsonProperty("ids")
	private UserIds ids;
	@JsonProperty("joined_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime joinedAt;
	@JsonProperty("location")
	private String location;
	@JsonProperty("about")
	private String about;
	@JsonProperty("gender")
	private String gender;
	@JsonProperty("age")
	private int age;
	@JsonProperty("images")
	private UserImages images;
	@JsonProperty("vip_og")
	private boolean vipOg;
	@JsonProperty("vip_years")
	private int vipYears;
	
	@Override
	public int hashCode(){
		return Objects.hash(getIds());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		User user = (User) o;
		return Objects.equals(getIds(), user.getIds());
	}
}
