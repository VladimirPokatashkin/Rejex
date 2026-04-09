package syntaxtree;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class ChoiceNode implements ASTNode {
	private ASTNode left;
	private ASTNode right;

	@Override
	public boolean isNullable() {
		return false;
	}
}
