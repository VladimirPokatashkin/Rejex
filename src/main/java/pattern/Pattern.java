package pattern;

import lombok.Getter;
import matcher.NFAMatcher;
import nfa.NFA;
import syntaxtree.SyntaxTree;

public class Pattern {
	@Getter
	private final NFA nfa;

	public Pattern(String rejex) {
		SyntaxTree ast = new SyntaxTree(rejex);
		nfa = NFA.ofTree(ast);
	}

	public static Pattern compile(String rejex) {
		return new Pattern(rejex);
	}

	public boolean matches(String text) {
		var matcher = new NFAMatcher(nfa, text);
		return matcher.match().isSuccess();
	}
}