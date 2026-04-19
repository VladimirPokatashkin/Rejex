package automaton.nfa;


import lombok.AllArgsConstructor;
import lombok.Getter;
import syntaxtree.SyntaxTree;
import syntaxtree.nodes.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class NFA {
	private NFAState begin;
	private NFAState end;
	private boolean hasLookahead;

	public NFA(NFAState begin, NFAState end) {
		this(begin, end, false);
	}

	private static NFA ofNode(ASTNode node) {
		switch (node) {
			case LiteralNode literal -> {
				return ofLiteral(literal);
			}
			case EmptyNode _ -> {
				return ofEmpty();
			}
			case ConcatenationNode concatenation -> {
				return ofConcatenation(concatenation);
			}
			case ChoiceNode choice -> {
				return ofChoice(choice);
			}
			case GroupNode group -> {
				return ofGroup(group);
			}
			case CharRangeNode charRange -> {
				return ofCharRange(charRange);
			}
			case RepetitionNode repetition -> {
				return ofRepetition(repetition);
			}
			case LookaheadNode lookahead -> {
				return ofLookahead(lookahead);
			}
			default -> throw new IllegalArgumentException("tree with end node must be compiled in dfa");
		}
	}

	private static NFA ofLiteral(LiteralNode literal) {
		var begin = new NFAState();
		var end = new NFAState();
		begin.addTransition(literal.getValue(), end);
		return new NFA(begin, end);
	}

	private static NFA ofEmpty() {
		var begin = new NFAState();
		var end = new NFAState();
		begin.addEpsilon(end);
		return new NFA(begin, end);
	}

	private static NFA ofConcatenation(ConcatenationNode concatenation) {
		List<NFA> fragments = new ArrayList<>(concatenation.getChildren().size());
		concatenation.getChildren().forEach(child -> fragments.add(ofNode(child)));

		for (int i = 0; i < fragments.size() - 1; ++i) {
			fragments.get(i).end.addEpsilon(fragments.get(i + 1).begin);
		}

		return new NFA(fragments.getFirst().begin, fragments.getLast().end);
	}

	private static NFA ofChoice(ChoiceNode choice) {
		var begin = new NFAState();
		var end = new NFAState();

		NFA left = ofNode(choice.getLeft());
		NFA right = ofNode(choice.getRight());

		begin.addEpsilon(left.begin);
		begin.addEpsilon(right.begin);

		left.end.addEpsilon(end);
		right.end.addEpsilon(end);

		return new NFA(begin, end);
	}

	private static NFA ofGroup(GroupNode group) {
		NFA inner = ofNode(group.getNode());

		var begin = new NFAState();
		var end = new NFAState();

		begin.addEpsilon(inner.begin);
		inner.end.addEpsilon(end);

		begin.getGroupMap().put(group.getIndex(), true);
		end.getGroupMap().put(group.getIndex(), false);

		return new NFA(begin, end);
	}

	private static NFA ofCharRange(CharRangeNode charRange) {
		var begin = new NFAState();
		var end = new NFAState();

		charRange.getRanges().forEach(range -> {
			for (char i = range.first; i <= range.second; ++i) {
				begin.addTransition(i, end);
			}
		});

		charRange.getSingles().forEach(symbol -> begin.addTransition(symbol, end));

		return new NFA(begin, end);
	}

	private static NFA ofRepetition(RepetitionNode repetition) {
		NFAState begin = null, currentEnd = null;

		for (int i = 0; i < repetition.getMin(); ++i) {
			NFA part = ofNode(repetition.getNode());
			if (begin == null) {
				begin = part.begin;
			} else {
				currentEnd.addEpsilon(part.begin);
			}
			currentEnd = part.end;
		}

		if (repetition.getMax() == -1) {
			NFA part = ofNode(repetition.getNode());
			NFAState start = new NFAState();
			NFAState end = new NFAState();

			start.addEpsilon(part.begin);
			start.addEpsilon(end);

			part.end.addEpsilon(part.begin);
			part.end.addEpsilon(end);

			if (begin == null) {
				begin = start;
			} else {
				currentEnd.addEpsilon(start);
			}
			currentEnd = end;
		} else {
			for (int i = repetition.getMin(); i < repetition.getMax(); ++i) {
				NFA part = ofNode(repetition.getNode());

				NFAState start = new NFAState();
				NFAState end = new NFAState();

				start.addEpsilon(part.begin);
				start.addEpsilon(end);
				part.end.addEpsilon(end);

				if (begin == null) {
					begin = start;
				} else {
					currentEnd.addEpsilon(start);
				}
				currentEnd = end;
			}
		}

		if (begin == null) {
			begin = new NFAState();
			currentEnd = begin;
		}
		return new NFA(begin, currentEnd);
	}

	private static NFA ofLookahead(LookaheadNode lookahead) {
		NFA mainPart = ofNode(lookahead.getLeft());
		NFA lookaheadPart = ofNode(lookahead.getRight());

		lookaheadPart.end.setAcceptable(true);

		mainPart.end.setLookahead(lookaheadPart);

		return new NFA(mainPart.begin, mainPart.end, true);
	}


	public static NFA ofTree(SyntaxTree tree) {
		NFAState.resetCounter();
		var nfa = ofNode(tree.getRoot());
		nfa.end.setAcceptable(true);
		return nfa;
	}
}