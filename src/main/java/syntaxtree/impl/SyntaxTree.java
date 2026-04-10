package syntaxtree.impl;

import lombok.Getter;
import syntaxtree.ISyntaxTree;
import syntaxtree.nodes.*;
import syntaxtree.parser.IParser;
import syntaxtree.parser.impl.Parser;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SyntaxTree implements ISyntaxTree {
	private ASTNode root;
	private int groupCnt;

	private int pos = 1;
	private final Map<Integer, ASTNode> positionMap = new HashMap<>();

	private void assignPositions(ASTNode node) {
		switch (node) {
			case LiteralNode lit -> {
				lit.setPos(pos);
				positionMap.put(pos++, lit);
			}
			case CharRangeNode range -> {
				range.setPos(pos);
				positionMap.put(pos++, range);
			}
			case ChoiceNode choice -> {
				assignPositions(choice.getLeft());
				assignPositions(choice.getRight());
			}
			case LookaheadNode lookahead -> {
				assignPositions(lookahead.getLeft());
				assignPositions(lookahead.getRight());
			}
			case ConcatenationNode concatenation -> concatenation.getChildren().forEach(this::assignPositions);
			case GroupNode group -> assignPositions(group.getNode());
			case RepetitionNode repetition -> assignPositions(repetition.getNode());
		}
	}

	public SyntaxTree(String expression) {
		IParser parser = new Parser(expression + "#");

		root = parser.parse();
		groupCnt = parser.getGroupCnt();

		assignPositions(root);
	}
}