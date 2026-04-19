package syntaxtree.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class GroupNode implements ASTNode {
	private ASTNode node;
	private int index;

}