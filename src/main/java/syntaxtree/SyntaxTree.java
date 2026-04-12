package syntaxtree;

import lombok.Getter;
import syntaxtree.nodes.*;
import syntaxtree.parser.Parser;
import syntaxtree.parser.ParsingException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SyntaxTree {
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

	public SyntaxTree(String expression) throws ParsingException {
		var parser = Parser.of(expression);

		root = parser.parse();
		groupCnt = parser.getGroupCnt();

		assignPositions(root);
	}
}