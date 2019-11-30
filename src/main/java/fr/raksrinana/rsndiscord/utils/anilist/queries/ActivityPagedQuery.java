package fr.raksrinana.rsndiscord.utils.anilist.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.utils.anilist.activity.list.ListActivity;
import kong.unirest.json.JSONObject;
import lombok.NonNull;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ActivityPagedQuery implements PagedQuery<ListActivity>{
	private static final String QUERY_FEED = PagedQuery.pagedQuery(", $userID: Int, $date: Int", "activities(userId: $userID, createdAt_greater: $date){\n" + "... on " + ListActivity.getQUERY() + "\n}");
	private final JSONObject variables;
	private int nextPage = 0;
	
	public ActivityPagedQuery(final int userId, final LocalDateTime date){
		this.variables = new JSONObject();
		this.variables.put("userID", userId);
		this.variables.put("page", 1);
		this.variables.put("perPage", PER_PAGE);
		final var s = date.atZone(ZoneId.of("UTC")).toEpochSecond();
		this.variables.put("date", s >= 0 ? s : 0);
	}
	
	@Override
	@NonNull
	public String getQuery(){
		return QUERY_FEED;
	}
	
	@Override
	@NonNull
	public JSONObject getParameters(final int page){
		this.variables.put("page", page);
		return this.variables;
	}
	
	@Override
	public int getNextPage(){
		return ++this.nextPage;
	}
	
	@NonNull
	@Override
	public String getPageElementName(){
		return "activities";
	}
	
	@NonNull
	public ListActivity buildChange(@NonNull final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(ListActivity.class).readValue(change.toString());
	}
	
	@Override
	public LocalDateTime getBaseDate(){
		return null;
	}
}