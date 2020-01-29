package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.roster.GamePlayerRole;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.roster.Info;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.roster.Player;
import fr.raksrinana.rsndiscord.utils.json.ISO8601DateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Roster{
	@JsonProperty("createdAt")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime createdAt;
	@JsonProperty("updatedAt")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime updatedAt;
	@JsonProperty("key")
	private String key;
	@JsonProperty("player")
	private Player player;
	@JsonProperty("gamePlayerRole")
	private GamePlayerRole gamePlayerRole;
	@JsonProperty("matchContestantKey")
	private String matchContestantKey;
	@JsonProperty("info")
	private Info info;
}
