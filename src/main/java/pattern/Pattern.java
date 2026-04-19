package pattern;

import lombok.Getter;
import matcher.MatchResult;
import matcher.NFAMatcher;
import automaton.nfa.NFA;
import syntaxtree.SyntaxTree;

@Getter
public class Pattern {
	private final NFA nfa;
	private MatchResult matchResult;

	public Pattern(String rejex) {
		SyntaxTree ast = new SyntaxTree(rejex);
		nfa = NFA.ofTree(ast);
	}

	public static Pattern compile(String rejex) {
		return new Pattern(rejex);
	}

	public boolean matches(String text) {
		var matcher = new NFAMatcher(nfa, text);
		matchResult = matcher.match();
		return matchResult.isSuccess();
	}
}