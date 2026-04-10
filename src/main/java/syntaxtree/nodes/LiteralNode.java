package syntaxtree.nodes;

import lombok.Getter;
import lombok.Setter;

@Getter
public final class LiteralNode implements ASTNode {
	private final String value;

	@Setter
	private int pos = -1;

	public LiteralNode(String value) {
		this.value = value;
	}

	public boolean isEmpty() {
		return value.isEmpty();
	}

	@Override
	public boolean isNullable() {
		return isEmpty();
	}
}