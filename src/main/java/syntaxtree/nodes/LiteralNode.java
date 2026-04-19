package syntaxtree.nodes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public final class LiteralNode implements ASTNode {
	private char value;

	public LiteralNode(char value) {
		this.value = value;
	}
}