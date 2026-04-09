package syntaxtree;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class LookaheadNode implements TreeNode {
	private TreeNode left;
	private TreeNode right;

	@Override
	public boolean isNullable() {
		return false;
	}
}
