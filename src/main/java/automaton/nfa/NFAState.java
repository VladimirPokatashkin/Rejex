package automaton.nfa;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class NFAState {
	private static int counter = 0;
	private final int id;
	@Setter
	private boolean isAcceptable;
	private final Map<Integer, Boolean> groupMap;
	private final Map<Character, List<NFAState>> transitions;
	private final List<NFAState> epsilons;
	@Setter
	private NFA lookahead;

	public NFAState() {
		this.id = counter++;
		isAcceptable = false;
		groupMap = new HashMap<>();
		transitions = new HashMap<>();
		epsilons = new ArrayList<>();
	}

	public static void resetCounter() {
		counter = 0;
	}

	public void addTransition(char c, NFAState state) {
		transitions.computeIfAbsent(c, _ -> new ArrayList<>()).add(state);
	}

	public void addEpsilon(NFAState state) {
		epsilons.add(state);
	}

	public List<NFAState> getTransition(char c) {
		return transitions.getOrDefault(c, List.of());
	}
}