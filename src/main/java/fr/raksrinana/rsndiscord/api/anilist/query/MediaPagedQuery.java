package fr.raksrinana.rsndiscord.api.anilist.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.api.anilist.data.media.IMedia;
import kong.unirest.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MediaPagedQuery implements IPagedQuery<IMedia>{
	private static final String QUERY = IPagedQuery.pagedQuery(", $mediaId: Int", IMedia.getQueryWithId());
	
	private final JSONObject variables;
	private int currentPage = 0;
	
	public MediaPagedQuery(int mediaId){
		variables = new JSONObject();
		variables.put("mediaId", mediaId);
		variables.put("page", 1);
		variables.put("perPage", PER_PAGE);
	}
	
	@NotNull
	@Override
	public String getQuery(){
		return QUERY;
	}
	
	@NotNull
	@Override
	public JSONObject getParameters(int page){
		variables.put("page", page);
		return variables;
	}
	
	@Override
	public int getNextPage(){
		return ++currentPage;
	}
	
	@NotNull
	@Override
	public String getPageElementName(){
		return "media";
	}
	
	@Nullable
	public IMedia buildChange(@NotNull JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(IMedia.class).readValue(change.toString());
	}
}
