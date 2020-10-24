package net.digiturtle.apollo.match;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.Circle;
import net.digiturtle.apollo.MathUtils;
import net.digiturtle.apollo.Vector2;
import net.digiturtle.apollo.definitions.MatchDefinition;
import net.digiturtle.apollo.definitions.ResourceRegionDefinition;
import net.digiturtle.apollo.match.event.Event;
import net.digiturtle.apollo.match.event.IEventListener;
import net.digiturtle.apollo.match.event.MatchSimulator;

public class Match {
	
	private IIntersector intersector;
	private ITiledMap tiledMap;
	private IWorld world;
	private HashMap<UUID, Player> players;
	private ArrayList<Bullet> bullets;
	private ArrayList<ResourceRegion> resourceRegions;
	private ArrayList<DroppedBackpack> droppedBackpacks;
	private ArrayList<Explosion> explosions;
	private float lengthSeconds, totalTimeSeconds;
	private Team[] teams;
	//private Random random;
	private boolean allowFriendlyFire;
	private IEventListener eventListener;
	
	public Match () {
		
	}
	
	public Match (ITiledMapLoader loader, IIntersector intersector, VisualFXEngine fxEngine) {
		eventListener = new MatchSimulator(this, fxEngine);
		this.intersector = intersector;
		//random = new Random();
        tiledMap = loader.load("sample.tmx");
        world = loader.createWorld(new Vector2(0, 0), true);
        //float tileSize = tiledMap.getProperties().get("tilewidth", Integer.class); 
        players = new HashMap<>();
        bullets = new ArrayList<Bullet>();
        resourceRegions = new ArrayList<>();
        droppedBackpacks = new ArrayList<>();
        allowFriendlyFire = false;
        explosions = new ArrayList<>();
        
        //FIXME
        ResourceRegion hotspot1 = new ResourceRegion(Resource.COAL);
        hotspot1.setPosition(new Vector2(128, -128));
        hotspot1.setSize(new Vector2(128, 128));//FIXME resource collection only works in the first 64x64
        hotspot1.setCapacity(1000);
        hotspot1.setCollectionRate(0.01f);
        hotspot1.setRegenerationRate(0.05f);
        hotspot1.setQuantity(1000);
        resourceRegions.add(hotspot1);
        addDroppedBackpack(new Backpack(), new Vector2(128, -256));
        lengthSeconds = totalTimeSeconds = 5*60;
	}
	
	public void load (MatchDefinition definition, ITiledMapLoader loader, IIntersector intersector, VisualFXEngine fxEngine) {
		eventListener = new MatchSimulator(this, fxEngine);
		this.intersector = intersector; 
		//random = new Random();
        tiledMap = loader.load(definition.tiledMapFile);
        world = loader.createWorld(new Vector2(0, 0), true);
        players = new HashMap<>();
        bullets = new ArrayList<Bullet>();
        resourceRegions = new ArrayList<>();
        droppedBackpacks = new ArrayList<>();
        for (ResourceRegionDefinition regionDefinition : definition.resourceRegions) {
        	resourceRegions.add(new ResourceRegion(regionDefinition));
        }
        teams = new Team[definition.teams.length];
        for (int i = 0; i < teams.length; i++) {
        	teams[i] = new Team(definition.teams[i]);
        }
        lengthSeconds = totalTimeSeconds = definition.lengthSeconds;
        allowFriendlyFire = definition.allowFriendlyFire;
        explosions = new ArrayList<>();
	}
	
	public void setTimeLeft(float lengthSeconds) {
		this.lengthSeconds = lengthSeconds;
	}
	
	public boolean canAllowFriendlyFire () {
		return allowFriendlyFire;
	}
	
	public void onEvent(Event event) {//FIXME send event across network and timestamp it
		eventListener.onEvent(event);
	}
	
	public IIntersector getIntersector () {
		return intersector;
	}
	
	public IWorld getWorld () {
		return world;
	}
	
	public Team[] getTeams () {
		return teams;
	}
	
	public float getTotalTime () {
		return totalTimeSeconds;
	}
	
	public float getTimeLeft () {
		return Math.max(0, lengthSeconds);
	}
	
	public HashMap<UUID, Player> getPlayersMap () {
		return players;
	}
	
	public void respawnAllPlayers () {
		for (Player player : players.values()) {
			respawnPlayer(player);
		}
	}
	
	public void respawnPlayer (Player player) {
		player.setPosition(teams[player.getTeam()].getRespawnPoint());
	}
	
	public ResourceRegion getResourceRegion (Player player) {
		Circle circle = new Circle();
		circle.set(player.getPosition(), ApolloSettings.CHARACTER_SIZE/2);
		for (ResourceRegion resourceRegion : resourceRegions) {
			//FIXME should probably not iterate through EVERY spot
			if (MathUtils.overlaps(resourceRegion.getBounds(), circle)) {
				return resourceRegion;
			}
		}
		return null;
	}
	
	public ArrayList<ResourceRegion> getResourceRegions () {
		return resourceRegions;
	}
	
	public ArrayList<DroppedBackpack> getDroppedBackpacks () {
		return droppedBackpacks;
	}
	
	public ArrayList<Explosion> getExplosions () {
		return explosions;
	}
	
	public void addDroppedBackpack (Backpack backpack, Vector2 position) {
		DroppedBackpack droppedBackpack = new DroppedBackpack();
		droppedBackpack.setBackpack(backpack);
		droppedBackpack.setPosition(position);
		droppedBackpacks.add(droppedBackpack);
	}
	
	public ArrayList<Bullet> getBullets () {
		return bullets;
	}

	public void addPlayer (Player player, IBody body) {
		player.setBody(body);
		players.put(player.getId(), player);
	}
	
	public Collection<Player> getPlayers () {
		return players.values();
	}
	
	public Player getPlayer (UUID uuid) {
		return players.get(uuid);
	}
	
	public ITiledMap getTiledMap () {
		return tiledMap;
	}

}
