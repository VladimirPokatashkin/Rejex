package syntaxtree.parser;

import syntaxtree.nodes.ASTNode;

public interface IParser {
	ASTNode parse();
	int getGroupCnt();
}