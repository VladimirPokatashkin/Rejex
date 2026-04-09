package syntaxtree;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public final class ConcatenationNode implements TreeNode {
	@Getter
	private List<TreeNode> children;

	@Override
	public boolean isNullable() {
		return false;
	}
}