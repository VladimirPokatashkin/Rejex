package automaton.dfa;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@EqualsAndHashCode
public class DFAState {
	private static int counter = 0;
	@Setter
	@EqualsAndHashCode.Exclude
	private boolean isAcceptable = false;
	@Setter
	@EqualsAndHashCode.Exclude
	private boolean isLookaheadEnd = false;
	@EqualsAndHashCode.Exclude
	private final int id;
	@EqualsAndHashCode.Exclude
	private final Map<Character, DFAState> transitions = new HashMap<>();
	private final Set<Integer> positions;

	public DFAState(Set<Integer> positions) {
		id = counter++;
		this.positions = Collections.unmodifiableSet(positions);
	}

	public void addTransition(char c, DFAState dest) {
		transitions.put(c, dest);
	}

	public DFAState getNextState(char c) {
		return transitions.get(c);
	}

	public static void resetCounter() {
		counter = 0;
	}
}