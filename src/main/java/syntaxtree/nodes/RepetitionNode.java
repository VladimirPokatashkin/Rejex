package syntaxtree.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class RepetitionNode implements ASTNode {
	private ASTNode node;
	private int min;
	private int max;

	@Override
	public boolean isNullable() {
		return false;
	}
}
