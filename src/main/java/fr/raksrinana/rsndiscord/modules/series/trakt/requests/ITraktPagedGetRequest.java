package fr.raksrinana.rsndiscord.modules.series.trakt.requests;

import java.util.Set;

public interface ITraktPagedGetRequest<T> extends ITraktGetRequest<Set<T>>{
	ITraktPagedGetRequest<T> getForPage(int page);
	
	default int getLimit(){
		return 50;
	}
	
	int getPage();
}
