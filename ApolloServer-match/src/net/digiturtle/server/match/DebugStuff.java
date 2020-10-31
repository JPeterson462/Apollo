package net.digiturtle.server.match;

import net.digiturtle.apollo.definitions.MatchDefinition;
import net.digiturtle.apollo.definitions.ResourceRegionDefinition;
import net.digiturtle.apollo.definitions.TeamDefinition;

public class DebugStuff {
	
	public static MatchDefinition newMatchDefinition (int teams) {
		MatchDefinition definition = new MatchDefinition();
		
		definition.tiledMapFile = "sample.tmx";
		definition.resourceRegions = new ResourceRegionDefinition[4];
		for (int i = 0; i < 4; i += 4) {
			ResourceRegionDefinition resourceDefinition = new ResourceRegionDefinition();
			resourceDefinition.resource = "COAL";
			resourceDefinition.position = new float[] { 32, -32 };
			resourceDefinition.size = new float[] { 32, 32 };
			resourceDefinition.capacity = 1000;
			resourceDefinition.collectionRate = 0.01f;
			resourceDefinition.regenerationRate = 0.05f;
			resourceDefinition.quantity = 1000;
			definition.resourceRegions[i] = resourceDefinition;
			
			resourceDefinition = new ResourceRegionDefinition();
			resourceDefinition.resource = "SHEET_METAL";
			resourceDefinition.position = new float[] { 96, -96 };
			resourceDefinition.size = new float[] { 24, 24 };
			resourceDefinition.capacity = 700;
			resourceDefinition.collectionRate = 0.01f;
			resourceDefinition.regenerationRate = 0.05f;
			resourceDefinition.quantity = 700;
			definition.resourceRegions[i+1] = resourceDefinition;
			
			resourceDefinition = new ResourceRegionDefinition();
			resourceDefinition.resource = "ELECTRONICS";
			resourceDefinition.position = new float[] { 96+64, -96-64 };
			resourceDefinition.size = new float[] { 16, 16 };
			resourceDefinition.capacity = 300;
			resourceDefinition.collectionRate = 0.01f;
			resourceDefinition.regenerationRate = 0.05f;
			resourceDefinition.quantity = 300;
			definition.resourceRegions[i+2] = resourceDefinition;
			
			resourceDefinition = new ResourceRegionDefinition();
			resourceDefinition.resource = "GOLD";
			resourceDefinition.position = new float[] { 96+64+64, -96-64-64 };
			resourceDefinition.size = new float[] { 8, 8 };
			resourceDefinition.capacity = 80;
			resourceDefinition.collectionRate = 0.01f;
			resourceDefinition.regenerationRate = 0.05f;
			resourceDefinition.quantity = 80;
			definition.resourceRegions[i+3] = resourceDefinition;
		}
		definition.teams = new TeamDefinition[teams];
		for (int i = 0; i < teams; i++) {
			TeamDefinition teamDefinition = new TeamDefinition();
			teamDefinition.respawnRegion = new float[] { 96 + i*32, -96 + -i*32, 64, 64 };
			teamDefinition.color = i;
			definition.teams[i] = teamDefinition;
		}
		definition.lengthSeconds = 5*60;
		definition.allowFriendlyFire = false;
		
		return definition;
	}

}
