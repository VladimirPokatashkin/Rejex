package automaton.dfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Decompiler {
	private static final String EMPTY_SET = "∅";
	private static final String EMPTY_STRING = "$";

	private static String buildInitial(List<String> chars, boolean isTheSameState) {
		String base = EMPTY_SET;

		for (String c : chars) {
			base = formatChoice(base, c);
		}

		if (isTheSameState) return formatChoice(base, EMPTY_STRING);
		return base;
	}

	private static String formatChoice(String a, String b) {
		if (a.equals(EMPTY_SET)) return b;
		if (b.equals(EMPTY_SET)) return a;
		if (a.equals(b)) return a;

		if (a.startsWith("/") && b.startsWith("/")) {
			return "/" + formatChoice(a.substring(1), b.substring(1));
		}

		return "(" + a + "|" + b + ")";
	}

	private static String formatConcatenation(String a, String b) {
		if (a.equals(EMPTY_SET) || b.equals(EMPTY_SET)) return EMPTY_SET;
		if (a.equals(EMPTY_STRING)) return b;
		if (b.equals(EMPTY_STRING)) return a;

		boolean aIsComplex = a.length() > 1 && !a.startsWith("(") && !a.contains("/");
		boolean bIsComplex = b.length() > 1 && !b.startsWith("(") && !b.startsWith("/");

		String left  = aIsComplex ? "(" + a + ")" : a;
		String right = bIsComplex ? "(" + b + ")" : b;
		return left + right;
	}

	private static String formatStar(String a) {
		if (a.equals(EMPTY_SET) || a.equals(EMPTY_STRING)) return EMPTY_STRING;
		if (a.endsWith("*") && !a.contains("|")) return a;
		if (a.startsWith("/")) return '/' + formatStar(a.substring(1));
		return a.length() > 1 ? "(" + a + ")*" : a + "*";
	}


	public static String decompile(DFA dfa) {
		List<DFAState> states = dfa.getStates();
		int statesCnt = states.size();
		Map<DFAState, Integer> indexMap = new HashMap<>();

		for (int i = 0; i < statesCnt; ++i) {
			indexMap.put(states.get(i), i);
		}

		String[][][] R = new String[statesCnt + 1][statesCnt][statesCnt];

		for (int i = 0; i < statesCnt; ++i) {
			for (int j = 0; j < statesCnt; ++j) {
				DFAState stateI = states.get(i);
				DFAState stateJ = states.get(j);

				List<String> transitions = new ArrayList<>();
				stateI.getTransitions().forEach((symbol, state) -> {
					if (state.equals(stateJ)) {
						String str = isMeta(symbol) ? "%" + symbol : String.valueOf(symbol);
						if (stateI.isLookaheadBound()) str = '/' + str;
						transitions.add(str);
					}
				});

				R[0][i][j] = buildInitial(transitions, i == j);
			}
		}

		for (int k = 1; k <= statesCnt; ++k) {
			for (int i = 0; i < statesCnt; ++i) {
				for (int j = 0; j < statesCnt; ++j) {
					String fromItoJ = R[k - 1][i][j];
					String fromItoK = R[k - 1][i][k - 1];
					String fromKtoK = R[k - 1][k - 1][k - 1];
					String fromKtoJ = R[k - 1][k - 1][j];

					String starK = formatStar(fromKtoK);
					String throughK = formatConcatenation(fromItoK, formatConcatenation(starK, fromKtoJ));
					R[k][i][j] = formatChoice(fromItoJ, throughK);
				}
			}
		}

		int beginIndex = indexMap.get(dfa.getBegin());
		String res = EMPTY_SET;

		for (int i = 0; i < statesCnt; ++i) {
			if (states.get(i).isAcceptable()) {
				res = formatChoice(res, R[statesCnt][beginIndex][i]);
			}
		}

		return res;
	}

	private static boolean isMeta(char c) {
		return c == '[' || c == ']' || c == '{' || c == '}' || c == '(' || c == ')' || c == '$'
				|| c == '%' || c == '.' || c == '*' || c == '+' || c == '…' || c == '|' || c == '/' || c == '-';
	}
}