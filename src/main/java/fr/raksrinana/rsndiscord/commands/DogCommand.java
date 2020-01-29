package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.InvalidResponseException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.utils.http.requestssenders.get.JSONGetRequestSender;
import kong.unirest.Unirest;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@BotCommand
public class DogCommand extends BasicCommand{
	private static final int HTTP_OK = 200;
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final var embed = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, ":dog: ** | Here's your random dog:**", null);
		embed.setImage(this.getDogPictureURL(event.getGuild()));
		Actions.reply(event, "", embed.build());
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	private String getDogPictureURL(final Guild guild){
		Log.getLogger(guild).debug("Getting random dog picture");
		final var handler = new JSONGetRequestSender(Unirest.get("https://dog.ceo/api/breeds/image/random")).getRequestHandler();
		if(Objects.equals(handler.getStatus(), HTTP_OK)){
			final var json = handler.getRequestResult().getObject();
			if(json.has("status") && Objects.equals(json.getString("status"), "success")){
				return json.getString("message");
			}
			throw new InvalidResponseException("Error getting dog API, status isn't successful. Json was: " + json.toString());
		}
		throw new InvalidResponseException("Error sending API request, HTTP code " + handler.getStatus() + " => " + handler.getRequestResult().toString());
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Dog";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("dog");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Gets a random picture of a dog";
	}
}
