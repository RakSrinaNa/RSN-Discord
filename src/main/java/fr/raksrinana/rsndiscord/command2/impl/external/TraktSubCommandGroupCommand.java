package fr.raksrinana.rsndiscord.command2.impl.external;

import fr.raksrinana.rsndiscord.command2.base.group.SubCommandGroup;
import fr.raksrinana.rsndiscord.command2.impl.external.trakt.RegisterCommand;
import org.jetbrains.annotations.NotNull;

public class TraktSubCommandGroupCommand extends SubCommandGroup{
	public TraktSubCommandGroupCommand(){
		addSubcommand(new RegisterCommand());
	}
	
	@Override
	@NotNull
	public String getId(){
		return "trakt";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Trakt services";
	}
}
