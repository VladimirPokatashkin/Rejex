package matcher;

import automaton.dfa.DFA;
import automaton.dfa.DFAState;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public final class DFAMatcher implements Matcher {
	private final DFA dfa;
	private final String input;

	@Override
	public MatchResult match() {
		DFAState current = dfa.getBegin();

		for (int i = 0; i < input.length(); ++i) {
			char c = input.charAt(i);
			current = current.getNextState(c);
			if (current == null) return new MatchResult();
		}

		return current.isAcceptable() ? new MatchResult(Map.of()) : new MatchResult();
	}

	@Override
	public SearchResult search() {
		for (int i = 0; i < input.length(); ++i) {
			DFAState current = dfa.getBegin();
			int lastAcceptableIndex = -1;

			for (int j = i; j < input.length(); ++j) {
				current = current.getNextState(input.charAt(j));

				if (current == null) break;
				if (current.isAcceptable()) lastAcceptableIndex = j;
			}

			if (lastAcceptableIndex != -1) return new SearchResult(null, i, lastAcceptableIndex + 1, true);
		}
		return new SearchResult();
	}
}