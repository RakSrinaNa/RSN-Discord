package fr.raksrinana.rsndiscord.utils.trakt.requests.oauth;

import fr.raksrinana.rsndiscord.utils.trakt.TraktPostRequest;
import fr.raksrinana.rsndiscord.utils.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.utils.trakt.model.auth.DeviceCode;
import kong.unirest.GenericType;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

public class DeviceCodePostRequest implements TraktPostRequest<DeviceCode>{
	public JSONObject getBody(){
		final var data = new JSONObject();
		data.put("client_id", TraktUtils.getClientId());
		return data;
	}
	
	@Override
	public GenericType<DeviceCode> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	public RequestBodyEntity getRequest(){
		return Unirest.post(TraktUtils.API_URL + "/oauth/device/code").body(getBody());
	}
}
