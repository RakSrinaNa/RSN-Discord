package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.Settings;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class ReportCommand extends BasicCommand
{
	
	@Override
	public int getScope()
	{
		return -5;
	}
	
	@Override
	public String getName()
	{
		return "Report";
	}
	
	@Override
	public String getCommand()
	{
		return "report";
	}
	
	@Override
	public boolean execute(Settings settings, MessageReceivedEvent event, LinkedList<String> args)
	{
		if(!super.execute(settings, event, args))
			return false;
		if(event.getChannel().getType() != ChannelType.PRIVATE)
		{
			event.getMessage().delete().complete();
			return true;
		}
		long reportChannel = settings.getReportChannel();
		if(reportChannel < 0)
		{
			event.getChannel().sendMessage("Cette fonctionnalité doit encore être configuré. Veuillez en avertir un modérateur.").complete();
		}
		else
		{
			event.getChannel().sendMessage("Votre message a bien été transféré. Merci à vous.").complete();
			event.getJDA().getTextChannelById(reportChannel).sendMessageFormat("Nouveau report de %s#%s: %s", event.getAuthor().getAsMention(), event.getAuthor().getDiscriminator(), args.stream().collect(Collectors.joining(" "))).complete();
		}
		return true;
	}
	
	@Override
	protected AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
}
