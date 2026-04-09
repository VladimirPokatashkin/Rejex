package syntaxtree;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class GroupNode implements TreeNode {
	private TreeNode node;
	private int index;

	@Override
	public boolean isNullable() {
		return false;
	}
}
