package fr.raksrinana.rsndiscord.command2.api;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;

public interface ISubCommandGroup extends ICommand{
	@NotNull
	SubcommandGroupData getSlashCommand();
}
