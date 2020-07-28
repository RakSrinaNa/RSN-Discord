package fr.raksrinana.rsndiscord.utils.thecatapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.net.URL;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Cat{
	@JsonProperty("breeds")
	private Set<String> breeds;
	@JsonProperty("categories")
	private Set<String> categories;
	@JsonProperty("id")
	private String id;
	@JsonProperty("url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL url;
}
