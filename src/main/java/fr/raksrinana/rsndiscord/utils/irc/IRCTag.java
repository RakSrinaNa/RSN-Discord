package fr.raksrinana.rsndiscord.utils.irc;

public class IRCTag{
	private final String key;
	private final String value;
	
	public IRCTag(final String key, final String value){
		this.key = key;
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.getKey() + "=" + this.getValue();
	}
	
	public String getKey(){
		return this.key;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public String getTrigram(){
		return this.getValue().substring(0, 3).toUpperCase();
	}
}
