package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.ForceShutdownThread;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ShutdownListener extends ListenerAdapter{
	@Override
	public void onShutdown(@NonNull final ShutdownEvent event){
		super.onShutdown(event);
		Log.getLogger(null).info("BOT STOPPED");
		new ForceShutdownThread().start();
	}
}
