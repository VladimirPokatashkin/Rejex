package syntaxtree;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class RepetitionNode implements TreeNode {
	private TreeNode node;
	private int min;
	private int max;

	@Override
	public boolean isNullable() {
		return false;
	}
}
