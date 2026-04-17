package matcher;

import nfa.GroupInfo;
import nfa.NFA;
import nfa.NFAState;

import java.util.*;

public class NFAMatcher {
	private final NFA nfa;
	private final String input;
	private int pos;
	private final Map<Integer, GroupInfo> groups;

	public NFAMatcher(NFA nfa, String input) {
		this.nfa = nfa;
		this.input = input;
		pos = 0;
		groups = new HashMap<>();
	}

	public MatchResult match() {
		Set<NFAState> currentStates = epsilonClosure(Set.of(nfa.getBegin()));
		processGroups(currentStates);

		while (pos < input.length()) {
			char currentChar = input.charAt(pos);
			if (!processLookahead(currentStates)) return new MatchResult();

			Set<NFAState> nextStates = new HashSet<>();
			currentStates.forEach(state -> nextStates.addAll(state.getTransition(currentChar)));

			currentStates = epsilonClosure(nextStates);
			++pos;
			processGroups(nextStates);
		}

		if (!processLookahead(currentStates)) return new MatchResult();

		List<String> groupList = new ArrayList<>();
		groups.forEach((_, group) -> groupList.add(input.substring(group.getBegin(), group.getEnd() + 1)));
		return new MatchResult(groupList);
	}

	private Set<NFAState> epsilonClosure(Set<NFAState> states) {
		Set<NFAState> epsilonStates = new HashSet<>(states);
		Queue<NFAState> queue = new LinkedList<>(states);

		while (!queue.isEmpty()) {
			var state = queue.poll();
			for (var epsilon : state.getEpsilons()) {
				if (!epsilonStates.contains(epsilon)) {
					epsilonStates.add(epsilon);
					queue.add(epsilon);
				}
			}
		}

		return epsilonStates;
	}

	private void processGroups(Set<NFAState> states) {
		for (var state : states) {
			for (var entry : state.getGroupMap().entrySet()) {
				int index = entry.getKey();
				boolean isBegin = entry.getValue();

				if (isBegin) {
					groups.put(index, new GroupInfo(index, pos));
				} else {
					groups.get(index).setEnd(pos);
				}
			}
		}
	}

	private boolean processLookahead(Set<NFAState> states) {
		for (var state : states) {
			NFA lookaheadPart = state.getLookahead();
			if (lookaheadPart != null) {
				NFAMatcher lookaheadMatcher = new NFAMatcher(lookaheadPart, input.substring(pos));
				if (!lookaheadMatcher.match().isSuccess()) {
					return false;
				}
			}
		}
		return true;
	}
}