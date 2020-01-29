package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.roundstats.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Scores{
	@JsonProperty("win")
	private int win;
	@JsonProperty("loss")
	private int loss;
	@JsonProperty("total")
	private int total;
	@JsonProperty("winPercentage")
	private float winPercentage;
}
