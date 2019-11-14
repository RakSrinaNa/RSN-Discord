package fr.raksrinana.rsndiscord.utils;

import lombok.NonNull;

public enum BasicEmotes implements Comparable<BasicEmotes>{
	A("\uD83C\uDDE6", "a"), B("\uD83C\uDDE7", "b"), C("\uD83C\uDDE8", "c"), D("\uD83C\uDDE9", "d"), E("\uD83C\uDDEA", "e"), F("\uD83C\uDDEB", "f"), G("\uD83C\uDDEC", "g"), H("\uD83C\uDDED", "h"), I("\uD83C\uDDEE", "i"), J("\uD83C\uDDEF", "j"), K("\uD83C\uDDF0", "k"), L("\uD83C\uDDF1", "l"), M("\uD83C\uDDF2", "m"), N("\uD83C\uDDF3", "n"), O("\uD83C\uDDF4", "o"), P("\uD83C\uDDF5", "p"), Q("\uD83C\uDDF6", "q"), R("\uD83C\uDDF7", "r"), S("\uD83C\uDDF8", "s"), T("\uD83C\uDDF9", "t"), U("\uD83C\uDDFA", "u"), V("\uD83C\uDDFB", "v"), W("\uD83C\uDDFC", "w"), X("\uD83C\uDDFD", "x"), Y("\uD83C\uDDFE", "y"), Z("\uD83C\uDDFF", "z"), THUMB_UP("\uD83D\uDC4D"), THUMB_DOWN("\uD83D\uDC4E"), ERROR(""), CHECK_OK("✅"), CROSS_NO("❌"), OK_HAND("👌");
	private final String name;
	private final String[] others;
	
	/**
	 * Constructor.
	 *
	 * @param name The representation of the emote.
	 */
	BasicEmotes(@NonNull final String name){
		this.name = name;
		this.others = new String[]{};
	}
	
	/**
	 * Constructor.
	 *
	 * @param name   The representation of the emote.
	 * @param others The other representations of the emote.
	 */
	BasicEmotes(@NonNull final String name, @NonNull final String... others){
		this.name = name;
		this.others = others;
	}
	
	/**
	 * Get an emote by a representation.
	 *
	 * @param text The representation.
	 *
	 * @return The emote, or null if non were found.
	 */
	public static BasicEmotes getEmote(@NonNull final String text){
		for(final var emote : BasicEmotes.values()){
			if(emote.getValue().equalsIgnoreCase(text)){
				return emote;
			}
			for(final var other : emote.getOthers()){
				if(other.equalsIgnoreCase(text)){
					return emote;
				}
			}
		}
		return ERROR;
	}
	
	/**
	 * Get the value of this emote.
	 *
	 * @return The emote's value.
	 */
	@NonNull
	public String getValue(){
		return this.name;
	}
	
	/**
	 * Get the other representations.
	 *
	 * @return The other representations.
	 */
	@NonNull
	private String[] getOthers(){
		return this.others;
	}
}

