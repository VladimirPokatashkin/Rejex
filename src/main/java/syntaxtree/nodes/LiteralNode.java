package syntaxtree.nodes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public final class LiteralNode implements ASTNode {
	private char value = 'v';
	private boolean isEmpty = true;

	@Setter
	private int pos = -1;

	public LiteralNode(char value) {
		this.value = value;
		isEmpty = false;
	}

	@Override
	public boolean isNullable() {
		return isEmpty();
	}
}