package fr.mrcraftcod.gunterdiscord.commands.hangman;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.HangmanListener;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-27
 */
public class HangmanLeaveCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	HangmanLeaveCommand(Command parent){
		super(parent);
	}
	
	@Override
	public CommandResult execute(@NotNull MessageReceivedEvent event, @NotNull LinkedList<String> args) throws Exception{
		super.execute(event, args);
		HangmanListener.getGame(event.getGuild(), false).ifPresent(h -> h.playerLeave(event.getMember()));
		return CommandResult.SUCCESS;
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Quitter pendu";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("leave");
	}
	
	@Override
	public String getDescription(){
		return "Quitte la partie de pendu en cours";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
