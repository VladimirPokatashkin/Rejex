package syntaxtree;

public sealed interface TreeNode permits
		LiteralNode,
		ConcatenationNode,
		ChoiceNode,
		RepetitionNode,
		GroupNode,
		LookaheadNode {

	boolean isNullable();
}