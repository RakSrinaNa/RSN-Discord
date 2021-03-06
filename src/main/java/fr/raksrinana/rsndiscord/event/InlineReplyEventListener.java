package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static java.util.Objects.isNull;
import static net.dv8tion.jda.api.entities.MessageType.INLINE_REPLY;

@EventListener
@Getter
@Log4j2
public class InlineReplyEventListener extends ListenerAdapter{
	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		
		try(var context = LogContext.with(guild).with(author)){
			var message = event.getMessage();
			
			if(message.getType() != INLINE_REPLY || author.isBot()){
				return;
			}
			
			var reference = message.getReferencedMessage();
			if(isNull(reference)){
				return;
			}
			
			if(Settings.get(guild).getMediaReactionMessages().contains(new MessageConfiguration(reference))){
				var original = Arrays.stream(reference.getContentRaw().split("\n"))
						.filter(line -> Character.isDigit(line.charAt(0)) || line.startsWith("N/A"))
						.map(line -> line.split(" ", 2)[0])
						.collect(Collectors.toList());
				var received = Arrays.stream(message.getContentRaw().split("\n")).collect(Collectors.toList());
				
				if(original.size() == received.size()){
					var content = author.getAsMention() + " replied:\n\n" +
							IntStream.range(0, original.size())
									.mapToObj(index -> original.get(index) + " " + received.get(index))
									.collect(Collectors.joining("\n"));
					
					JDAWrappers.reply(reference, content).submit()
							.thenAccept(sent -> JDAWrappers.delete(message).submit());
				}
				else{
					JDAWrappers.message(event.getChannel(), String.format("Expected %d lines, got %d", original.size(), received.size())).submitAndDelete(2);
				}
			}
		}
		catch(Exception e){
			log.error("Error handling message", e);
		}
	}
}
