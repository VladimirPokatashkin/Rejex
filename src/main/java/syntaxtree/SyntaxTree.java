package syntaxtree;

import lombok.Getter;
import syntaxtree.nodes.*;
import syntaxtree.parser.Parser;
import syntaxtree.parser.ParsingException;

import java.util.List;

@Getter
public class SyntaxTree {
	private ASTNode root;
	private final int groupCnt;

	public SyntaxTree(String expression) throws ParsingException {
		var parser = Parser.of(expression);
		root = parser.parse();
		root = root instanceof EndNode ? root : new ConcatenationNode(List.of(root, new EndNode()));
		groupCnt = parser.getGroupCnt();
	}
}