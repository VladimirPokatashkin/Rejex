package syntaxtree;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class ChoiceNode implements TreeNode {
	private TreeNode left;
	private TreeNode right;

	@Override
	public boolean isNullable() {
		return false;
	}
}
