package fr.raksrinana.rsndiscord.modules.externaltodos.runner;

import fr.raksrinana.rsndiscord.modules.externaltodos.ExternalTodosUtils;
import fr.raksrinana.rsndiscord.modules.reaction.config.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.modules.externaltodos.data.Status.EXTERNAL;
import static fr.raksrinana.rsndiscord.modules.reaction.ReactionTag.EXTERNAL_TODO;
import static fr.raksrinana.rsndiscord.modules.reaction.ReactionUtils.DELETE_KEY;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CHECK_OK;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CROSS_NO;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class ExternalTodosRunner implements IScheduledRunner{
	private final JDA jda;
	
	public ExternalTodosRunner(@NonNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public long getDelay(){
		return 2;
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
	
	@Override
	public void execute(){
		this.jda.getGuilds().forEach(guild -> {
			var configuration = Settings.get(guild).getExternalTodos();
			configuration.getEndpoint().ifPresent(endpoint -> {
				var token = configuration.getToken().orElse(null);
				
				configuration.getNotificationChannel()
						.flatMap(ChannelConfiguration::getChannel)
						.ifPresent(channel -> {
							var response = ExternalTodosUtils.getTodos(endpoint, token);
							
							response.ifPresent(todos -> todos.getTodos().forEach(todo -> {
								channel.sendMessage("`" + todo.getKind().name() + "` => " + todo.getDescription()).submit()
										.thenAccept(message -> {
											message.addReaction(CHECK_OK.getValue()).submit();
											if(todo.getKind().isCancellable()){
												message.addReaction(CROSS_NO.getValue()).submit();
											}
											
											var waitingReactionMessageConfiguration = new WaitingReactionMessageConfiguration(message,
													EXTERNAL_TODO, Map.of(DELETE_KEY, Boolean.toString(false)));
											Settings.get(guild).addMessagesAwaitingReaction(waitingReactionMessageConfiguration);
											
											ExternalTodosUtils.setStatus(endpoint, token, todo, EXTERNAL);
										});
							}));
						});
			});
		});
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "External todos fetcher";
	}
}
