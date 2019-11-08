package fr.raksrinana.rsndiscord.utils.irc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
@SuppressWarnings("WeakerAccess")
public class IRCClient implements Closeable{
	public static final int DEFAULT_TIMEOUT = 600000;
	private static final Logger LOGGER = LoggerFactory.getLogger(IRCClient.class);
	private final String host;
	private final int port;
	private final Set<String> joinedChannels;
	private final List<IRCListener> listeners;
	private String pass;
	private Socket socket;
	private PrintWriter socketWriter;
	private boolean connected;
	private IRCReaderThread ircReader;
	
	public IRCClient(@Nonnull final String host, final int port){
		this.host = host;
		this.port = port;
		this.pass = null;
		this.connected = false;
		this.joinedChannels = new HashSet<>();
		this.listeners = new ArrayList<>();
	}
	
	public void connect() throws IOException{
		this.socket = new Socket(this.host, this.port);
		this.socket.setSoTimeout(DEFAULT_TIMEOUT);
		this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
		this.ircReader = new IRCReaderThread(this, this.socket.getInputStream());
		this.ircReader.start();
		if(Objects.nonNull(this.pass)){
			this.sendMessage(String.format("PASS %s", this.pass));
			LOGGER.info("Using pass to connect to {}:{}", this.host, this.port);
		}
		this.connected = true;
		LOGGER.info("IRC connection initialized with {}:{}", this.host, this.port);
	}
	
	public void sendMessage(@Nonnull final String message){
		this.socketWriter.println(message);
	}
	
	public void joinChannel(@Nonnull final String channel){
		if(this.isConnected()){
			this.sendMessage(String.format("JOIN %s", channel));
			this.joinedChannels.add(channel);
			LOGGER.info("Joined channel {} on {}:{}", channel, this.host, this.port);
		}
		else{
			throw new IllegalStateException("Not connected");
		}
	}
	
	private boolean isConnected(){
		return this.connected;
	}
	
	@Override
	public void close() throws IOException{
		if(this.isConnected()){
			this.sendMessage("QUIT");
			this.socketWriter.close();
			this.ircReader.close();
			this.socket.close();
			LOGGER.info("Connection IRC closed with {}:{}", this.host, this.port);
		}
		else{
			throw new IllegalStateException("Not connected");
		}
	}
	
	public void timedOut(){
		final var ls = new ArrayList<>(this.listeners);
		ls.forEach(l -> TwitchIRC.disconnect(l.getGuild(), l.getUser()));
		ls.forEach(l -> {
			try{
				TwitchIRC.connect(l.getGuild(), l.getUser());
			}
			catch(final IOException e){
				LOGGER.warn("Failed to reconnect {} to user {}", l.getGuild(), l.getUser());
			}
		});
	}
	
	public void addEventListener(@Nonnull final IRCListener ircListener){
		this.listeners.add(ircListener);
	}
	
	public void leaveChannel(@Nonnull final String channel){
		if(this.isConnected()){
			this.sendMessage(String.format("PART %s", channel));
			this.joinedChannels.remove(channel);
			LOGGER.info("Left channel {} on {}:{}", channel, this.host, this.port);
		}
		else{
			throw new IllegalStateException("Not connected");
		}
	}
	
	@Nonnull
	public Set<String> getJoinedChannels(){
		return this.joinedChannels;
	}
	
	@Nonnull
	public List<IRCListener> getListeners(){
		return this.listeners;
	}
	
	public void setNick(@Nonnull final String nickname){
		if(this.isConnected()){
			this.sendMessage(String.format("NICK %s", nickname));
			LOGGER.info("Set nick to {} on {}:{}", nickname, this.host, this.port);
		}
		else{
			throw new IllegalStateException("Not connected");
		}
	}
	
	public void setSecureKeyPassword(@Nonnull final String pass){
		this.pass = pass;
	}
}
