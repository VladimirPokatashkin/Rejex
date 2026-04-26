package pattern;

import automaton.dfa.DFA;
import automaton.dfa.Decompiler;
import lombok.Getter;
import matcher.*;
import automaton.nfa.NFA;
import syntaxtree.SyntaxTree;

@Getter
public class Pattern {
	private NFA nfa;
	private DFA dfa;
	private final SyntaxTree syntaxTree;

	public Pattern(String rejex) {
		syntaxTree = new SyntaxTree(rejex);
		if (syntaxTree.getGroupCnt() > 0) {
			nfa = NFA.ofTree(syntaxTree);
		}
		dfa = DFA.ofTree(syntaxTree);
		dfa = dfa.minimize();
	}

	public static Pattern compile(String rejex) {
		return new Pattern(rejex);
	}

	public String decompile() {
		return Decompiler.decompile(dfa);
	}

	public SearchResult search(String text) {
		return search(text, false);
	}

	public SearchResult search(String text, boolean withGroups) {
		if (withGroups && nfa == null) {
			throw new IllegalArgumentException("pattern doesn`t contain groups");
		} else if (withGroups) {
			var matcher = new NFAMatcher(nfa, text);
			return matcher.search();
		} else {
			var matcher = new DFAMatcher(dfa, text);
			return matcher.search();
		}
	}

	public Pattern difference(Pattern other) {
		Pattern res = compile("");
		res.dfa = this.dfa.difference(other.dfa);
		res.nfa = null;
		return res;
	}

	public Pattern intersection(Pattern other) {
		Pattern res = compile("");
		res.dfa = this.dfa.intersection(other.dfa);
		res.nfa = null;
		return res;
	}
}