package automaton.dfa;

import lombok.Getter;
import syntaxtree.SyntaxTree;
import syntaxtree.nodes.ASTNode;
import syntaxtree.nodes.CharRangeNode;
import syntaxtree.nodes.LiteralNode;

import java.util.*;


public class DFA {
	@Getter
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

			for (int pos : current.getPositions()) {
				if (pos == endPos || !followpos.containsKey(pos)) continue;

				Set<Integer> nextPositions = followpos.get(pos);
				ASTNode node = positionMap.get(pos);

				if (node instanceof LiteralNode literal) {
					transitionsFromCurrent.computeIfAbsent(literal.getValue(),
							_ -> new HashSet<>()).addAll(nextPositions);
				} else if (node instanceof CharRangeNode range) {
					range.getRanges().forEach(pair -> {
						for (char c = pair.first; c < pair.second; ++c) {
							transitionsFromCurrent.computeIfAbsent(c, _ -> new HashSet<>()).addAll(nextPositions);
						}
					});
					range.getSingles().forEach(symbol ->
						transitionsFromCurrent.computeIfAbsent(symbol, _ -> new HashSet<>()).addAll(nextPositions)
					);
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

				if (targetPositions.stream().anyMatch(lookaheadEnds::contains)) state.setLookaheadEnd(true);

				current.addTransition(c, state);
			}
		}

		return new DFA(begin, states);
	}
}