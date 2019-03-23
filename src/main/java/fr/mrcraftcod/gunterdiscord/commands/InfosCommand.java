package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class InfosCommand extends BasicCommand{
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		Actions.sendMessage(event.getTextChannel(), Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Bot infos").addField("Bot version", Main.getRSNBotVersion(), false).addField("Last start (local time):", Main.bootTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), false).addField("Time elapsed", Duration.between(Main.bootTime, ZonedDateTime.now()).toString(), false).build());
		return CommandResult.SUCCESS;
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Bot infos";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("info");
	}
	
	@Override
	public String getDescription(){
		return "Sends infos about the bot";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}