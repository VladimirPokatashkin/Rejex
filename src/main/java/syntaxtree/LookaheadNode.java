package syntaxtree;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class LookaheadNode implements ASTNode {
	private ASTNode left;
	private ASTNode right;

	@Override
	public boolean isNullable() {
		return false;
	}
}
