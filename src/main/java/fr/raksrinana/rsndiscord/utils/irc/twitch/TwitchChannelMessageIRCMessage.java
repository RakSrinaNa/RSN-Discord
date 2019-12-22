package fr.raksrinana.rsndiscord.utils.irc.twitch;

import fr.raksrinana.rsndiscord.utils.irc.IRCTag;
import fr.raksrinana.rsndiscord.utils.irc.messages.ChannelMessageIRCMessage;
import fr.raksrinana.rsndiscord.utils.irc.messages.IRCMessage;
import lombok.Getter;
import lombok.NonNull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TwitchChannelMessageIRCMessage implements IRCMessage{
	@Getter
	private final ChannelMessageIRCMessage parent;
	@Getter
	private final List<TwitchBadge> badges;
	@Getter
	private final TwitchMessageId msgId;
	
	public TwitchChannelMessageIRCMessage(@NonNull ChannelMessageIRCMessage event){
		this.parent = event;
		this.badges = event.getTags().stream().filter(t -> Objects.equals("badges", t.getKey())).map(IRCTag::getValue).flatMap(t -> Arrays.stream(t.split(",")).filter(v -> !v.isBlank()).map(v -> {
			final var split = v.split("/");
			return new TwitchBadge(split[0], split[1]);
		})).collect(Collectors.toList());
		this.msgId = event.getTags().stream().filter(t -> Objects.equals("msg-id", t.getKey())).map(t -> TwitchMessageId.getFromName(t.getValue())).findFirst().orElse(TwitchMessageId.NONE);
	}
	
	public Optional<TwitchBadge> getSub(){
		return getBadges().stream().filter(t -> Objects.equals("subscriber", t.getName())).findFirst();
	}
	
	public Optional<TwitchBadge> getSubGifter(){
		return getBadges().stream().filter(t -> Objects.equals("sub-gifter", t.getName())).findFirst();
	}
	
	public boolean isBroadcaster(){
		return getBadges().stream().anyMatch(t -> Objects.equals("broadcaster", t.getName()));
	}
	
	public boolean isModerator(){
		return getParent().getTags().stream().anyMatch(t -> Objects.equals("moderator", t.getKey()));
	}
	
	public boolean isPartner(){
		return getParent().getTags().stream().anyMatch(t -> Objects.equals("partner", t.getKey()));
	}
}