package fr.raksrinana.rsndiscord.utils.anilist;

import fr.mrcraftcod.utils.http.requestssenders.post.JSONPostRequestSender;
import fr.raksrinana.rsndiscord.listeners.CommandsMessageListener;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.anilist.AnilistAccessTokenConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.InvalidResponseException;
import fr.raksrinana.rsndiscord.utils.log.Log;
import kong.unirest.json.JSONObject;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class AniListUtils{
	private static final String API_URL = "https://anilist.co/api/v2";
	private static final int APP_ID = 1230;
	private static final String REDIRECT_URI = "https://anilist.co/api/v2/oauth/pin";
	@Getter
	private static final String CODE_LINK = String.format("%s/oauth/authorize?client_id=%d&response_type=code&redirect_uri=%s", API_URL, APP_ID, REDIRECT_URI);
	private static final String USER_INFO_QUERY = "query{Viewer {id name}}";
	private static final int HTTP_OK = 200;
	private static final int HTTP_ERROR = 503;
	public static URL FALLBACK_URL;
	
	public static void getAndSaveToken(@NonNull final Member member, @NonNull final String code) throws MalformedURLException, URISyntaxException, InvalidResponseException{
		Log.getLogger(member.getGuild()).debug("Getting access token for {}", member);
		final var accessToken = getPreviousAccessToken(member);
		if(accessToken.isPresent()){
			return;
		}
		final var headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		final var body = new JSONObject();
		body.put("grant_type", "authorization_code");
		body.put("client_id", "" + APP_ID);
		body.put("client_secret", System.getProperty("ANILIST_SECRET"));
		body.put("redirect_uri", REDIRECT_URI);
		body.put("code", code);
		final var sender = new JSONPostRequestSender(new URL(API_URL + "/oauth/token"), headers, new HashMap<>(), body.toString());
		final var result = sender.getRequestHandler();
		if(!Objects.equals(result.getStatus(), HTTP_OK)){
			throw new InvalidResponseException("Getting token failed HTTP code " + result.getStatus());
		}
		final var json = result.getRequestResult().getObject();
		if(json.has("error") || !json.has("access_token") || !json.has("refresh_token")){
			throw new IllegalArgumentException("Getting token failed with error: " + json.getString("error"));
		}
		Settings.get(member.getGuild()).getAniListConfiguration().setRefreshToken(member.getUser().getIdLong(), json.getString("refresh_token"));
		Settings.get(member.getGuild()).getAniListConfiguration().addAccessToken(new AnilistAccessTokenConfiguration(member.getUser().getIdLong(), LocalDateTime.now().plusSeconds(json.getInt("expires_in")), json.getString("access_token")));
	}
	
	@NonNull
	private static Optional<AnilistAccessTokenConfiguration> getPreviousAccessToken(@NonNull final Member member){
		Log.getLogger(member.getGuild()).debug("Getting previous access token for {}", member);
		final var accessToken = Settings.get(member.getGuild()).getAniListConfiguration().getAccessToken(member.getUser().getIdLong());
		if(accessToken.isPresent()){
			Log.getLogger(member.getGuild()).debug("Found previous access token for {}", member);
			return accessToken;
		}
		Log.getLogger(member.getGuild()).debug("No access token found for {}", member);
		return Optional.empty();
	}
	
	public static Optional<Integer> getUserId(@NonNull final Member member){
		return Settings.get(member.getGuild()).getAniListConfiguration().getUserId(member.getUser().getIdLong()).map(Optional::of).orElseGet(() -> {
			try{
				final var userInfos = AniListUtils.getQuery(member, USER_INFO_QUERY, new JSONObject());
				final var userId = userInfos.getJSONObject("data").getJSONObject("Viewer").getInt("id");
				Settings.get(member.getGuild()).getAniListConfiguration().setUserId(member.getUser().getIdLong(), userId);
				return Optional.of(userId);
			}
			catch(final Exception e){
				Log.getLogger(member.getGuild()).error("Failed to get AniList user id for {}", member);
			}
			return Optional.empty();
		});
	}
	
	@NonNull
	public static JSONObject getQuery(@NonNull final Member member, @NonNull final String query, @NonNull final JSONObject variables) throws Exception{
		Log.getLogger(member.getGuild()).debug("Sending query to AniList for user {}", member.getUser());
		final var token = AniListUtils.getPreviousAccessToken(member).orElseThrow(() -> {
			Settings.get(member.getGuild()).getAniListConfiguration().removeUser(member.getUser());
			Actions.replyPrivate(member.getGuild(), member.getUser(), MessageFormat.format("Your token for AniList on {0} expired. Please use `{1}al r` to register again if you want to continue receiving information", member.getGuild().getName(), Settings.get(member.getGuild()).getPrefix().orElse(CommandsMessageListener.defaultPrefix)), null);
			return new IllegalStateException("No valid token found, please register again");
		});
		return getQuery(token, query, variables);
	}
	
	@NonNull
	private static JSONObject getQuery(@NonNull final AnilistAccessTokenConfiguration token, @NonNull final String query, @NonNull final JSONObject variables) throws Exception{
		final var headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + token.getToken());
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		final var body = new JSONObject();
		body.put("query", query);
		body.put("variables", variables);
		final var handler = new JSONPostRequestSender(new URL("https://graphql.anilist.co"), headers, new HashMap<>(), body.toString()).getRequestHandler();
		if(Objects.equals(handler.getStatus(), HTTP_OK)){
			return handler.getRequestResult().getObject();
		}
		if(Objects.equals(handler.getStatus(), HTTP_ERROR)){
			try{
				final var result = handler.getRequestResult().getObject();
				if(result.has("errors")){
					final var errors = result.getJSONArray("errors");
					if(!errors.isEmpty()){
						throw new AnilistException(handler.getStatus(), errors.getJSONObject(0).getString("message"));
					}
				}
			}
			catch(final Exception ignored){
			}
		}
		throw new Exception("Error sending API request, HTTP code " + handler.getStatus() + " => " + handler.getRequestResult().toString() + " | query was " + query);
	}
	
	public static LocalDateTime getDefaultDate(){
		return LocalDateTime.of(2019, 7, 7, 0, 0);
	}
	
	static{
		try{
			FALLBACK_URL = new URL("https://anilist.co");
		}
		catch(final MalformedURLException e){
			Log.getLogger(null).error("Failed to create default URL", e);
		}
	}
}
