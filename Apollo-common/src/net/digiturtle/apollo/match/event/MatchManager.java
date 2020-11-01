package net.digiturtle.apollo.match.event;

import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

import net.digiturtle.apollo.IntersectorStub;
import net.digiturtle.apollo.SharedUtils;
import net.digiturtle.apollo.TiledMapLoaderStub;
import net.digiturtle.apollo.VisualFXEngineStub;
import net.digiturtle.apollo.definitions.MatchDefinition;
import net.digiturtle.apollo.match.IIntersector;
import net.digiturtle.apollo.match.ITiledMapLoader;
import net.digiturtle.apollo.match.Match;
import net.digiturtle.apollo.match.Player;
import net.digiturtle.apollo.match.VisualFXEngine;

public class MatchManager implements IEventListener {
	
	public static Consumer<Event> eventDispatcher;
	
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
	
	private void onServerReady () {
		eventDispatcher.accept(new MatchStartEvent(matchDefinition, match.getPlayers().toArray(n -> new Player[n])));
		match.load(matchDefinition, new TiledMapLoaderStub(), new IntersectorStub(), new VisualFXEngineStub());
		onStart.accept(match);
	}
	
	@Override
	public void onEvent (Event event) {
		if (event instanceof MatchConnectEvent) {
			MatchConnectEvent matchConnectEvent = (MatchConnectEvent) event;
			onPlayerConnect(matchConnectEvent.getUniqueIdentifier());
			if (match.getPlayersMap().size() == players*teams) {
				onServerReady();
			}
		}
		if (event instanceof PlayerEvent) {
			match.onEvent(event);
		}
	}

}
