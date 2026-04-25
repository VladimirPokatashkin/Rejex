package automaton.dfa;

import lombok.Getter;
import syntaxtree.SyntaxTree;
import syntaxtree.nodes.ASTNode;
import syntaxtree.nodes.CharRangeNode;
import syntaxtree.nodes.LiteralNode;

import java.util.*;

@Getter
public class DFA {
	private final DFAState begin;
	private final Set<DFAState> states;

	private DFA(DFAState begin, Set<DFAState> states) {
		this.begin = begin;
		this.states = states;
	}


	public static DFA ofTree(SyntaxTree tree) {
		NodeMarker marker = new NodeMarker();
		var rootInfo = marker.mark(tree.getRoot());
		var followpos = marker.getFollowpos();
		var positionMap = marker.getPositionMap();
		var lookaheadEnds = marker.getLookaheadEnds();
		int endPos = marker.getEndPos();

		Queue<DFAState> queue = new LinkedList<>();
		Map<Set<Integer>, DFAState> discoveredStates = new HashMap<>();
		Set<DFAState> states = new HashSet<>();

		DFAState.resetCounter();
		DFAState begin = new DFAState(rootInfo.firstpos());
		discoveredStates.put(begin.getPositions(), begin);
		queue.add(begin);
		states.add(begin);

		while (!queue.isEmpty()) {
			var current = queue.poll();
			if (current.getPositions().contains(endPos)) current.setAcceptable(true);

			Map<Character, Set<Integer>> transitionsFromCurrent = new HashMap<>();
			Set<Character> lookaheadTranstitions = new HashSet<>();

			for (int pos : current.getPositions()) {
				if (pos == endPos || !followpos.containsKey(pos)) continue;

				Set<Integer> nextPositions = followpos.get(pos);
				ASTNode node = positionMap.get(pos);
				boolean isLookaheadEnd = lookaheadEnds.contains(pos);

				if (node instanceof LiteralNode literal) {
					transitionsFromCurrent.computeIfAbsent(literal.getValue(),
							_ -> new HashSet<>()).addAll(nextPositions);
					if (isLookaheadEnd) lookaheadTranstitions.add(literal.getValue());
				} else if (node instanceof CharRangeNode range) {
					range.getRanges().forEach(pair -> {
						for (char c = pair.first; c <= pair.second; ++c) {
							transitionsFromCurrent.computeIfAbsent(c, _ -> new HashSet<>()).addAll(nextPositions);
							if (isLookaheadEnd) lookaheadTranstitions.add(c);
						}
					});
					range.getSingles().forEach(symbol -> {
						transitionsFromCurrent.computeIfAbsent(symbol, _ -> new HashSet<>()).addAll(nextPositions);
						if (isLookaheadEnd) lookaheadTranstitions.add(symbol);
					});
				}
			}

			for (var entry : transitionsFromCurrent.entrySet()) {
				char c = entry.getKey();
				var targetPositions = entry.getValue();
				if (targetPositions.isEmpty()) continue;

				DFAState state = discoveredStates.computeIfAbsent(targetPositions, k -> {
					DFAState newState = new DFAState(k);
					queue.add(newState);
					states.add(newState);
					return newState;
				});

				if (lookaheadTranstitions.contains(c)) {
					state.setLookaheadBound(true);
				}

				current.addTransition(c, state);
			}
		}

		return new DFA(begin, states);
	}

	public DFA minimize() {
		Set<Character> alphabet = getAlphabet();

		Map<List<Boolean>, Set<DFAState>> initialPartitions = new HashMap<>();
		states.forEach(state -> {
			List<Boolean> signature = List.of(state.isAcceptable(), state.isLookaheadBound());
			initialPartitions.computeIfAbsent(signature, _ -> new HashSet<>()).add(state);
		});

		List<Set<DFAState>> partitions = new ArrayList<>(initialPartitions.values());

		boolean splited = true;
		while (splited) {
			splited = false;
			List<Set<DFAState>> newPartitions = new ArrayList<>();

			for (var group : partitions) {
				if (group.size() <= 1) {
					newPartitions.add(group);
					continue;
				}

				Map<List<Integer>, Set<DFAState>> splits = new HashMap<>();
				for (DFAState state : group) {
					List<Integer> signature = new ArrayList<>();

					for (char symbol : alphabet) {
						DFAState target = state.getNextState(symbol);
						signature.add(findPartitionIndex(partitions, target));
					}

					splits.computeIfAbsent(signature, _ -> new HashSet<>()).add(state);
				}

				newPartitions.addAll(splits.values());
				if (splits.size() > 1) splited = true;
			}
			partitions = newPartitions;
		}

		return rebuild(begin, partitions, alphabet);
	}


	private static int findPartitionIndex(List<Set<DFAState>> partitions, DFAState target) {
		if (target == null) return -1;
		for (int i = 0; i < partitions.size(); ++i) {
			if (partitions.get(i).contains(target)) return i;
		}
		return -1;
	}

	private Set<Character> getAlphabet() {
		Set<Character> alphabet = new HashSet<>();
		states.forEach(state -> alphabet.addAll(state.getTransitions().keySet()));
		return alphabet;
	}

	private static DFA rebuild(DFAState oldBegin, List<Set<DFAState>> partitions, Set<Character> alphabet) {
		Map<Set<DFAState>, DFAState> stateMap = new HashMap<>();
		DFAState newBegin = null;
		DFAState.resetCounter();

		for (var group : partitions) {
			DFAState representative = group.iterator().next();

			Set<Integer> newPositions = new HashSet<>();
			group.forEach(state -> newPositions.addAll(state.getPositions()));

			DFAState newState = new DFAState(newPositions);
			newState.setAcceptable(representative.isAcceptable());
			newState.setLookaheadBound(representative.isLookaheadBound());
			stateMap.put(group, newState);

			if (group.contains(oldBegin)) newBegin = newState;
		}

		for (var group : partitions) {
			DFAState state = stateMap.get(group);
			DFAState representative = group.iterator().next();

			for (char symbol : alphabet) {
				DFAState target = representative.getNextState(symbol);
				if (target != null) {
					var targetGroup = partitions.stream().filter(gr -> gr.contains(target)).findAny();
					targetGroup.ifPresent(states -> state.addTransition(symbol, stateMap.get(states)));
				}
			}
		}

		return new DFA(newBegin, new HashSet<>(stateMap.values()));
	}
}