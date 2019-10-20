package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
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
@BotCommand
public class InfosCommand extends BasicCommand{
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		Actions.reply(event, Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Bot infos").addField("Bot version", Main.getRSNBotVersion(), false).addField("Last start (local time):", Main.bootTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), false).addField("Time elapsed", Duration.between(Main.bootTime, ZonedDateTime.now()).toString(), false).build());
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Bot infos";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("info");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Sends infos about the bot";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}