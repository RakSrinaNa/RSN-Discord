package fr.raksrinana.rsndiscord.api.dog;

import fr.raksrinana.rsndiscord.api.dog.data.DogResponse;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.InvalidResponseException;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DogApi{
	@NotNull
	public static String getDogPictureURL(@Nullable Guild guild){
		Log.getLogger(guild).debug("Getting random dog picture");
		
		var response = Unirest.get("https://dog.ceo/api/breeds/image/random").asObject(DogResponse.class);
		if(!response.isSuccess()){
			throw new InvalidResponseException("Error sending API request, HTTP code " + response.getStatus());
		}
		
		var dog = response.getBody();
		if(!dog.isSuccess()){
			throw new InvalidResponseException("Error getting dog API, status isn't successful. " + dog);
		}
		
		return dog.getMessage();
	}
}
