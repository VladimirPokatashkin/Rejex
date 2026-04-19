package syntaxtree.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class ChoiceNode implements ASTNode {
	private ASTNode left;
	private ASTNode right;

}
