package syntaxtree.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public final class ConcatenationNode implements ASTNode {
	@Getter
	private List<ASTNode> children;

}