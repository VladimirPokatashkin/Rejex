package matcher;

import automaton.nfa.GroupInfo;
import automaton.nfa.NFA;
import automaton.nfa.NFAState;

import java.util.*;

public final class NFAMatcher implements Matcher {
	private final NFA nfa;
	private final String input;
	private final Map<Integer, GroupInfo> groups;

	public NFAMatcher(NFA nfa, String input) {
		this.nfa = nfa;
		this.input = input;
		this.groups = new HashMap<>();
	}

	@Override
	public MatchResult match() {
		if (backtrack(nfa.getBegin(), 0, new HashSet<>())) {
			Map<Integer, String> groupMap = new HashMap<>();
			groups.forEach((index, group) -> {
				int start = group.getBegin();
				int end = group.getEnd();

				if (start <= end + 1) {
					groupMap.put(index, input.substring(start, end + 1));
				}
			});
			return new MatchResult(groupMap);
		}

		return new MatchResult();
	}

	public SearchResult searchWithGroups() {
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

	@Override
	public SearchResult searchWithoutGroups() {
		for (int i = 0; i < input.length(); ++i) {
			int end = backtrackSearch(nfa.getBegin(), i , new HashSet<>());
			if (end != -1) return new SearchResult(null, i, end, true);
		}
		return new SearchResult();
	}

	private boolean backtrack(NFAState state, int currentPos, Set<NFAState> visitedEpsilons) {
		if (state.getLookahead() != null) {
			NFAMatcher lookaheadMatcher = new NFAMatcher(state.getLookahead(), input.substring(currentPos));
			if (!lookaheadMatcher.matchForLookahead()) {
				return false;
			}
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

		if (currentPos == input.length() && state.isAcceptable()) {
			return true;
		}

		for (NFAState epsilon : state.getEpsilons()) {
			if (visitedEpsilons.add(epsilon)) {
				if (backtrack(epsilon, currentPos, visitedEpsilons)) {
					return true;
				}
				visitedEpsilons.remove(epsilon);
			}
		}

		if (currentPos < input.length()) {
			char c = input.charAt(currentPos);
			for (NFAState nextState : state.getTransition(c)) {
				if (backtrack(nextState, currentPos + 1, new HashSet<>())) {
					return true;
				}
			}
		}

		for (var entry : state.getGroupMap().entrySet()) {
			int index = entry.getKey();
			if (backupGroups.containsKey(index)) {
				groups.put(index, backupGroups.get(index));
			} else {
				groups.remove(index);
			}
		}

		return false;
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


	private int backtrackSearch(NFAState state, int currentPos, Set<NFAState> visitedEpslions) {
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
			if (visitedEpslions.add(epsilon)) {
				int res = backtrackSearch(epsilon, currentPos, visitedEpslions);
				if (res != -1) return res;
				visitedEpslions.remove(epsilon);
			}
		}

		if (currentPos < input.length()) {
			char c = input.charAt(currentPos);
			for (NFAState next : state.getTransition(c)) {
				int res = backtrackSearch(next, currentPos, visitedEpslions);
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