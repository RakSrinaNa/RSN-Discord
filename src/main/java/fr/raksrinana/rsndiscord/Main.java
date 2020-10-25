package fr.raksrinana.rsndiscord;

import fr.raksrinana.rsndiscord.listeners.CommandsEventListener;
import fr.raksrinana.rsndiscord.listeners.EventListener;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.irc.config.TwitchConfiguration;
import fr.raksrinana.rsndiscord.modules.irc.twitch.TwitchIRC;
import fr.raksrinana.rsndiscord.modules.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.modules.reaction.ReactionUtils;
import fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.modules.series.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.modules.settings.GuildConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.modules.stopwatch.reply.StopwatchWaitingUserReplyEventListener;
import fr.raksrinana.rsndiscord.runner.RunnerUtils;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.utils.http.JacksonObjectMapper;
import kong.unirest.Unirest;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import picocli.CommandLine;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class Main{
	public static final ZonedDateTime bootTime = ZonedDateTime.now();
	public static final boolean DEVELOPMENT = Boolean.parseBoolean(System.getProperty("rsndev", "false"));
	@Getter
	private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
	@Getter
	private static CLIParameters parameters;
	@Getter
	private static JDA jda;
	private static ConsoleHandler consoleHandler;
	
	/**
	 * Main entry point.
	 *
	 * @param args Not used.
	 */
	public static void main(@NonNull final String[] args){
		parameters = Optional.ofNullable(loadEnv(args))
				.orElseThrow(() -> new IllegalStateException("Failed to load environment"));
		Unirest.config()
				.setObjectMapper(new JacksonObjectMapper())
				.connectTimeout(30000)
				.socketTimeout(30000)
				.enableCookieManagement(true)
				.verifySsl(true);
		consoleHandler = new ConsoleHandler();
		try{
			Log.getLogger(null).info("Building JDA");
			final var jdaBuilder = JDABuilder.createDefault(System.getProperty("RSN_TOKEN"))
					.enableIntents(GatewayIntent.GUILD_MEMBERS)
					.setMemberCachePolicy(MemberCachePolicy.ALL);
			registerAllEventListeners(jdaBuilder);
			jdaBuilder.setAutoReconnect(true);
			jda = jdaBuilder.build();
			jda.awaitReady();
			jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, CommandsEventListener.defaultPrefix + "help for the help"));
			Log.getLogger(null).info("Loaded {} guild settings", jda.getGuilds().stream().map(Settings::get).count());
			Log.getLogger(null).info("Adding handlers");
			ReactionUtils.registerAllHandlers();
			ScheduleUtils.registerAllHandlers();
			Log.getLogger(null).info("Creating runners");
			RunnerUtils.registerAllScheduledRunners();
			Log.getLogger(null).info("Started");
			announceStart();
			restartTwitchIRCConnections();
			
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				Log.getLogger(null).info("Shutdown hook triggered");
				Settings.close();
			}));
			Log.getLogger(null).info("Shutdown hook registered");
			consoleHandler.start();
		}
		catch(final LoginException | InterruptedException e){
			Log.getLogger(null).error("Couldn't start bot", e);
			close();
		}
		catch(final Exception e){
			Log.getLogger(null).error("Bot error", e);
			close();
		}
	}
	
	private static void registerAllEventListeners(JDABuilder jdaBuilder){
		Utilities.getAllAnnotatedWith(EventListener.class, clazz -> (ListenerAdapter) clazz.getConstructor().newInstance())
				.forEach(jdaBuilder::addEventListeners);
	}
	
	static CLIParameters loadEnv(@NonNull String[] args){
		Log.getLogger(null).info("Starting bot version {}", getRSNBotVersion());
		if(DEVELOPMENT){
			Log.getLogger(null).warn("Developer mode activated, shouldn't be used in production!");
		}
		
		final var parameters = new CLIParameters();
		var cli = new CommandLine(parameters);
		cli.registerConverter(Path.class, Paths::get);
		cli.setUnmatchedArgumentsAllowed(true);
		try{
			cli.parseArgs(args);
		}
		catch(final CommandLine.ParameterException e){
			Log.getLogger(null).error("Failed to parse arguments", e);
			cli.usage(System.out);
			return null;
		}
		
		final var prop = new Properties();
		try(final var is = Files.newInputStream(parameters.getConfigurationFile())){
			prop.load(is);
		}
		catch(final IOException e){
			Log.getLogger(null).warn("Failed to read file {}", parameters.getConfigurationFile());
		}
		prop.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
		Log.getLogger(null).debug("Loaded {} properties from file", prop.keySet().size());
		return parameters;
	}
	
	/**
	 * Announce the bot started in channels defined in the configuration.
	 *
	 * @see GuildConfiguration#getAnnounceStartChannel()
	 */
	private static void announceStart(){
		Main.jda.getGuilds().stream()
				.map(Settings::get)
				.map(GuildConfiguration::getAnnounceStartChannel)
				.flatMap(Optional::stream)
				.map(ChannelConfiguration::getChannel)
				.flatMap(Optional::stream)
				.forEach(channel -> Actions.sendMessage(channel, translate(channel.getGuild(), "started"), null));
	}
	
	/**
	 * Connects to IRC channels defined in the configuration.
	 *
	 * @see TwitchConfiguration#getTwitchAutoConnectUsers()
	 */
	private static void restartTwitchIRCConnections(){
		Main.getJda().getGuilds()
				.forEach(guild -> Settings.get(guild)
						.getTwitchConfiguration()
						.getTwitchAutoConnectUsers()
						.forEach(user -> {
							try{
								TwitchIRC.connect(guild, user);
							}
							catch(Exception e){
								Log.getLogger(guild).error("Failed to automatically connect to twitch user {}", user, e);
							}
						}));
	}
	
	/**
	 * Get the version of the bot.
	 *
	 * @return The version, or {@code "Unknown"} if unknown.
	 */
	@NonNull
	public static String getRSNBotVersion(){
		final var properties = new Properties();
		try{
			properties.load(Main.class.getResource("/version.properties").openStream());
		}
		catch(final Exception e){
			Log.getLogger(null).warn("Error reading version", e);
		}
		return properties.getProperty("bot.version", "Unknown");
	}
	
	/**
	 * Close the bot.
	 */
	public static void close(){
		TraktUtils.stopAll();
		StopwatchWaitingUserReplyEventListener.stopAll();
		RSNAudioManager.stopAll();
		TwitchIRC.close();
		executorService.shutdownNow();
		consoleHandler.close();
		Settings.close();
		Main.getJda().shutdown();
		final var client = Main.getJda().getHttpClient();
		client.connectionPool().evictAll();
		client.dispatcher().executorService().shutdown();
	}
}
