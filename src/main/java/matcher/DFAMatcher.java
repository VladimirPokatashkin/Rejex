package matcher;

import automaton.dfa.DFA;
import automaton.dfa.DFAState;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DFAMatcher {
	private final DFA dfa;
	private final String input;


	public SearchResult search() {
		if (input.isEmpty())
			return dfa.getBegin().isAcceptable() ? new SearchResult(null, 0, 0, true)
					: new SearchResult();

		for (int i = 0; i < input.length(); ++i) {
			DFAState current = dfa.getBegin();

			int lookaheadBound = current.isLookaheadBound() ? i - 1 : -1;
			int lastAcceptableIndex;
			if (current.isAcceptable()) {
				if (lookaheadBound != -1) {
					lastAcceptableIndex = lookaheadBound;
				} else {
					lastAcceptableIndex = i - 1;
				}
			} else lastAcceptableIndex = -1;

			for (int j = i; j < input.length(); ++j) {
				current = current.getNextState(input.charAt(j));
				if (current == null) break;

				if (current.isLookaheadBound()) lookaheadBound = j;

				if (current.isAcceptable()) lastAcceptableIndex = lookaheadBound != -1 ? lookaheadBound : j;
			}

			if (lastAcceptableIndex != -1) {
				return new SearchResult(null, i, lastAcceptableIndex + 1, true);
			}
		}
		return new SearchResult();
	}
}