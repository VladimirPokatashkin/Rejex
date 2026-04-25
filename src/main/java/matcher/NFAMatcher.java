package matcher;

import automaton.nfa.GroupInfo;
import automaton.nfa.NFA;
import automaton.nfa.NFAState;

import java.util.*;

public class NFAMatcher {
	private final NFA nfa;
	private final String input;
	private final Map<Integer, GroupInfo> groups;


	public NFAMatcher(NFA nfa, String input) {
		this.nfa = nfa;
		this.input = input;
		this.groups = new HashMap<>();
	}

	public SearchResult search() {
		for (int i = 0; i < input.length(); ++i) {
			groups.clear();

			int finish = backtrackSearch(nfa.getBegin(), i, new HashSet<>());
			if (finish == -1) continue;

			Map<Integer, String> groupMap = new HashMap<>();
			groups.forEach((index, group) -> {
				int start = group.getBegin();
				int end = group.getEnd();

				if (start <= end + 1) {
					groupMap.put(index, input.substring(start, end + 1));
				}
			});
			return new SearchResult(groupMap, i, finish, true);
		}
		return new SearchResult();
	}

	private boolean matchForLookahead() {
		return backtrackLookahead(nfa.getBegin(), 0, new HashSet<>());
	}

	private boolean backtrackLookahead(NFAState state, int currentPos, Set<NFAState> visitedEpsilons) {
		if (state.isAcceptable()) return true;

		for (NFAState eps : state.getEpsilons()) {
			if (visitedEpsilons.add(eps)) {
				if (backtrackLookahead(eps, currentPos, visitedEpsilons)) return true;
				visitedEpsilons.remove(eps);
			}
		}

		if (currentPos < input.length()) {
			char c = input.charAt(currentPos);
			for (NFAState nextState : state.getTransition(c)) {
				if (backtrackLookahead(nextState, currentPos + 1, new HashSet<>())) return true;
			}
		}
		return false;
	}


	private int backtrackSearch(NFAState state, int currentPos, Set<NFAState> visitedEpsilons) {
		if (state.getLookahead() != null) {
			NFAMatcher lookaheadMatcher = new NFAMatcher(state.getLookahead(), input.substring(currentPos));
			if (!lookaheadMatcher.matchForLookahead()) return -1;
		}

		Map<Integer, GroupInfo> backupGroups = new HashMap<>();
		for (var entry : state.getGroupMap().entrySet()) {
			int index = entry.getKey();
			boolean isBegin = entry.getValue();

			if (groups.containsKey(index)) {
				GroupInfo oldGroup = groups.get(index);
				GroupInfo backup = new GroupInfo(oldGroup.getIndex(), oldGroup.getBegin());
				backup.setEnd(oldGroup.getEnd());
				backupGroups.put(index, backup);
			}

			if (isBegin) {
				groups.put(index, new GroupInfo(index, currentPos));
			} else {
				if (groups.containsKey(index)) {
					groups.get(index).setEnd(currentPos - 1);
				}
			}
		}

		for (NFAState epsilon : state.getEpsilons()) {
			if (visitedEpsilons.add(epsilon)) {
				int res = backtrackSearch(epsilon, currentPos, visitedEpsilons);
				if (res != -1) return res;
				visitedEpsilons.remove(epsilon);
			}
		}

		if (currentPos < input.length()) {
			char c = input.charAt(currentPos);
			for (NFAState next : state.getTransition(c)) {
				int res = backtrackSearch(next, currentPos + 1, new HashSet<>());
				if (res != -1) return res;
			}
		}

		if (state.isAcceptable()) return currentPos;

		for (var entry : state.getGroupMap().entrySet()) {
			int index = entry.getKey();
			if (backupGroups.containsKey(index)) {
				groups.put(index, backupGroups.get(index));
			} else {
				groups.remove(index);
			}
		}

		return -1;
	}
}