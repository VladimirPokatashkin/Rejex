package automaton.dfa;

import java.util.Set;

public record NodeInfo(
		boolean nullable,
		Set<Integer> firstpos,
		Set<Integer> lastpos
) {}