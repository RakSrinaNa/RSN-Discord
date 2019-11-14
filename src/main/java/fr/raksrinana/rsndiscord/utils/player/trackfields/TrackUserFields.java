package fr.raksrinana.rsndiscord.utils.player.trackfields;

import lombok.NonNull;
import java.util.HashMap;
import java.util.Optional;

public class TrackUserFields{
	private final HashMap<String, Object> map;
	
	public TrackUserFields(){
		this.map = new HashMap<>();
	}
	
	public <T> void fill(@NonNull final AudioTrackDataFields<T> field, @NonNull final T value){
		this.map.put(field.getName(), field.valueForField(value));
	}
	
	@NonNull
	public <T> Optional<T> get(@NonNull final AudioTrackDataFields<T> field){
		return Optional.ofNullable(this.map.get(field.getName())).map(field::parseObject);
	}
}
