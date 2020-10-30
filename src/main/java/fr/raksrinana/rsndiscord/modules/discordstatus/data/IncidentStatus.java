package fr.raksrinana.rsndiscord.modules.discordstatus.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum IncidentStatus{
	INVESTIGATING, IDENTIFIED, MONITORING, RESOLVED, POSTMORTEM;
	
	@JsonCreator
	public IncidentStatus getByName(String name){
		for(var indicator : IncidentStatus.values()){
			if(indicator.name().equalsIgnoreCase(name)){
				return indicator;
			}
		}
		return null;
	}
}