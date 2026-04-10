package syntaxtree;

import syntaxtree.nodes.ASTNode;

public interface ISyntaxTree {
	ASTNode getRoot();
	int getGroupCnt();
}