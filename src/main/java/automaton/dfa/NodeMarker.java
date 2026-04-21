package automaton.dfa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import syntaxtree.nodes.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@NoArgsConstructor
public class NodeMarker {
	private int pos = 1;
	private int endPos;
	private final Map<Integer, ASTNode> positionMap = new HashMap<>();
	private final Map<Integer, Set<Integer>> followpos = new HashMap<>();
	private final Set<Integer> lookaheadEnds = new HashSet<>();


	private NodeInfo markConcatenation(NodeInfo left, NodeInfo right) {
		if (left == null) return right;
		if (right == null) return left;

		left.lastpos().forEach(
				p -> followpos.computeIfAbsent(p, _ -> new HashSet<>()).addAll(right.firstpos())
		);

		Set<Integer> firstPos = new HashSet<>(left.firstpos());
		if (left.nullable()) firstPos.addAll(right.firstpos());

		Set<Integer> lastPos = new HashSet<>(right.lastpos());
		if (right.nullable()) lastPos.addAll(left.lastpos());

		return new NodeInfo(left.nullable() && right.nullable(), firstPos, lastPos);
	}

	private NodeInfo markRepetition(RepetitionNode repetition) {
		var inner = repetition.getNode();
		int min = repetition.getMin();
		int max = repetition.getMax();
		NodeInfo res = null;

		for (int i = 0; i < min; ++i) {
			res = markConcatenation(res, mark(inner));
		}

		if (max != -1) {
			for (int i = min; i < max; ++i) {
				NodeInfo part = mark(inner);
				NodeInfo optional = new NodeInfo(true, part.firstpos(), part.lastpos());
				res = markConcatenation(res, optional);
			}
		} else {
			NodeInfo part = mark(inner);
			part.lastpos().forEach(p ->
					followpos.computeIfAbsent(p, _ -> new HashSet<>()).addAll(part.firstpos())
			);
			NodeInfo optional = new NodeInfo(true, part.firstpos(), part.lastpos());
			res = markConcatenation(res, optional);
		}

		return res == null ? new NodeInfo(true, Set.of(), Set.of()) : res;
	}

	private NodeInfo markLookahead(LookaheadNode lookahead) {
		var left = mark(lookahead.getLeft());
		var right = mark(lookahead.getRight());
		lookaheadEnds.addAll(left.lastpos());
		return markConcatenation(left, right);
	}

	private NodeInfo markLiteral(ASTNode node) {
		int current = pos++;
		positionMap.put(current, node);
		Set<Integer> set = Set.of(current);
		return new NodeInfo(false, set, set);
	}


	public NodeInfo mark(ASTNode node) {
		return switch (node) {
			case EmptyNode _ -> new NodeInfo(true, Set.of(), Set.of());
			case LiteralNode _, CharRangeNode _ -> markLiteral(node);
			case EndNode _ -> {
				endPos = pos;
				yield markLiteral(node);
			}
			case ChoiceNode choice -> {
				var left = mark(choice.getLeft());
				var right = mark(choice.getRight());

				Set<Integer> firstPos = new HashSet<>(left.firstpos());
				firstPos.addAll(right.firstpos());

				Set<Integer> lastPos = new HashSet<>(left.lastpos());
				lastPos.addAll(right.lastpos());

				yield new NodeInfo(left.nullable() || right.nullable(), firstPos, lastPos);
			}
			case ConcatenationNode concatenation -> {
				NodeInfo res = null;
				for (var child : concatenation.getChildren()) {
					res = markConcatenation(res, mark(child));
				}
				yield res;
			}
			case RepetitionNode repetition -> markRepetition(repetition);
			case LookaheadNode lookahead -> markLookahead(lookahead);
			case GroupNode group -> mark(group.getNode());
		};
	}
}