package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils.asset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601DateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class PublishDetails{
	@JsonProperty("environment")
	private String environment;
	@JsonProperty("locale")
	private String locale;
	@JsonProperty("time")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime time;
	@JsonProperty("user")
	private String user;
}
