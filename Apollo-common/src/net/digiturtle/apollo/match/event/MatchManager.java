package net.digiturtle.apollo.match.event;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.digiturtle.apollo.IntersectorStub;
import net.digiturtle.apollo.SharedUtils;
import net.digiturtle.apollo.TiledMapLoaderStub;
import net.digiturtle.apollo.VisualFXEngineStub;
import net.digiturtle.apollo.definitions.MatchDefinition;
import net.digiturtle.apollo.match.Arsenal;
import net.digiturtle.apollo.match.IIntersector;
import net.digiturtle.apollo.match.ITiledMapLoader;
import net.digiturtle.apollo.match.Match;
import net.digiturtle.apollo.match.Player;
import net.digiturtle.apollo.match.VisualFXEngine;

public class MatchManager implements IEventListener {
	
	public static Consumer<Object> eventDispatcher, managerDispatcher;
	
	private Match match;
	private int teams, players;
	private float[] teamBins;
	private Random random;
	private int[] teamBinCounts;
	private MatchDefinition matchDefinition;
	private Consumer<Match> onStart;

	public MatchManager (int teams, int players, MatchDefinition matchDefinition,
			ITiledMapLoader tiledMapLoader, IIntersector intersector, VisualFXEngine fxEngine,
			Consumer<Match> onStart) {
		this.teams = teams;
		this.players = players;
		this.matchDefinition = matchDefinition;
		match = new Match(tiledMapLoader, intersector, fxEngine);
		teamBins = SharedUtils.generateBins(teams);
		teamBinCounts = new int[teams];
		random = new Random();
		this.onStart = onStart;
	}
	
	public Match getMatch () {
		return match;
	}
	
	private void onPlayerConnect (UUID identifier) {
		Player player = new Player(identifier, null, null);
		player.setState(Player.State.STANDING);
		player.setTeam(findAvailableTeam());
		match.addPlayer(player, null);
		System.out.println("MatchManager onPlayerConnect: " + player + " " + match.getPlayersMap() + " " + match);
	}
	
	private int findAvailableTeam () {
		int team = SharedUtils.assignBin(teamBins, random.nextFloat());
		while (teamBinCounts[team] == players) {
			// While the bin selected is full
			team = SharedUtils.assignBin(teamBins, random.nextFloat());
		}
		teamBinCounts[team]++;
		return team;
	}
	
	public void onArsenalResult (BatchArsenalQuery.Response response) {
		for (Map.Entry<UUID, Arsenal> arsenal : response.arsenals.entrySet()) {
			match.getPlayer(arsenal.getKey()).setArsenal(arsenal.getValue());
		}
		Event event = new MatchStartEvent(matchDefinition, match.getPlayers().toArray(n -> new Player[n]));
		eventDispatcher.accept(event);
		match.onEvent(event);
		match.load(matchDefinition, new TiledMapLoaderStub(), new IntersectorStub(), new VisualFXEngineStub());
		onStart.accept(match);
	}
	
	public void onServerReady () {
		BatchArsenalQuery.Request request = new BatchArsenalQuery.Request();
		request.ids = match.getPlayers().stream().map(player -> player.getId()).collect(Collectors.toList()).toArray(n -> new UUID[n]);
		managerDispatcher.accept(request);
	}
	
	@Override
	public void onEvent (Event event) {
		System.out.println("MatchManager onEvent: " + event);
		if (event instanceof MatchConnectEvent) {
			MatchConnectEvent matchConnectEvent = (MatchConnectEvent) event;
			onPlayerConnect(matchConnectEvent.getUniqueIdentifier());
			//match.onEvent(event);
			System.out.println("PlayerConnect: " + matchConnectEvent.getUniqueIdentifier() + " for " + match.getPlayersMap());
			if (match.getPlayersMap().size() == players*teams) {
				onServerReady();
			}
		}
		if (event instanceof PlayerEvent) {
			match.onEvent(event);
		}
		if (event instanceof MatchOverEvent) {
			eventDispatcher.accept(event);
		}
	}

}
