package pattern;

import automaton.dfa.DFA;
import lombok.Getter;
import matcher.*;
import automaton.nfa.NFA;
import syntaxtree.SyntaxTree;

@Getter
public class Pattern {
	private NFA nfa;
	private DFA dfa;
	private final SyntaxTree syntaxTree;
	private MatchResult matchResult;

	public Pattern(String rejex) {
		syntaxTree = new SyntaxTree(rejex);
		if (syntaxTree.getGroupCnt() > 0) {
			nfa = NFA.ofTree(syntaxTree);
		} else {
			dfa = DFA.ofTree(syntaxTree);
			dfa = dfa.minimize();
		}
	}

	public static Pattern compile(String rejex) {
		return new Pattern(rejex);
	}

	public boolean matches(String text) {
		Matcher matcher = dfa == null ? new NFAMatcher(nfa, text) : new DFAMatcher(dfa, text);
		matchResult = matcher.match();
		return matchResult.isSuccess();
	}

	public SearchResult search(String text, boolean withGroups) {
		if (withGroups && nfa == null) {
			throw new IllegalArgumentException("pattern doesn`t contains groups");
		} else if (withGroups) {
			var matcher = new NFAMatcher(nfa, text);
			return matcher.searchWithGroups();
		}

		Matcher matcher = dfa == null ? new NFAMatcher(nfa, text) : new DFAMatcher(dfa, text);
		return matcher.search();
	}
}