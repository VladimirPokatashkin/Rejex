package syntaxtree;

public sealed interface ASTNode permits
		LiteralNode,
		ConcatenationNode,
		ChoiceNode,
		RepetitionNode,
		GroupNode,
		LookaheadNode,
		CharRangeNode {

	boolean isNullable();
}