package nfa;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class NFAState {
	private static int counter = 0;
	private int id;
	@Setter
	private boolean isAcceptable;
	private Map<Integer, Boolean> groupMap;
	private Map<Character, List<NFAState>> transitions;
	private List<NFAState> epsilons;
	@Setter
	private NFA lookahead;

	public NFAState() {
		this.id = counter++;
		isAcceptable = false;
		groupMap = new HashMap<>();
		transitions = new HashMap<>();
		epsilons = new ArrayList<>();
	}

	public void addTransition(char c, NFAState state) {
		transitions.computeIfAbsent(c, _ -> new ArrayList<>()).add(state);
	}

	public void addEpsilon(NFAState state) {
		epsilons.add(state);
	}

	public List<NFAState> getTransition(char c) {
		return transitions.get(c);
	}
}