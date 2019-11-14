package fr.raksrinana.rsndiscord.utils.player.trackfields;

import fr.raksrinana.rsndiscord.Main;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;

public class RequesterTrackDataField implements AudioTrackDataFields<User>{
	public User parseObject(@NonNull final Object value) throws IllegalArgumentException{
		return Main.getJda().getUserById((long) value);
	}
	
	@NonNull
	@Override
	public Object valueForField(@NonNull User value){
		return value.getIdLong();
	}
	
	@Override
	@NonNull
	public String getName(){
		return "requester";
	}
}
