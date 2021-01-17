package fr.raksrinana.rsndiscord.command.impl.trakt;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.runner.trakt.TraktUserHistoryRunner;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

class HistoryCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	HistoryCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.trakt.history", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		new TraktUserHistoryRunner(event.getJDA()).runQueryOnDefaultUsersChannels();
		return SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.trakt.history.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("history", "h");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.trakt.history.description");
	}
}