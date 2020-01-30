package fr.raksrinana.rsndiscord.utils.eslgaming.model.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class ResponseMeta{
	@JsonProperty("totalCount")
	private int totalCount;
	@JsonProperty("pageCount")
	private int pageCount;
	@JsonProperty("currentPage")
	private int currentPage;
	@JsonProperty("perPage")
	private int perPage;
}