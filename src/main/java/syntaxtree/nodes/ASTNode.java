package syntaxtree.nodes;

public sealed interface ASTNode permits
		LiteralNode,
		EmptyNode,
		ConcatenationNode,
		ChoiceNode,
		RepetitionNode,
		GroupNode,
		LookaheadNode,
		CharRangeNode,
		EndNode {}