package syntaxtree.nodes;

import lombok.Getter;
import lombok.Setter;
import other.Pair;

import java.util.List;

@Getter
public final class CharRangeNode implements ASTNode {
	private final List<Pair<Character, Character>> ranges;
	private final List<Character> singles;

	@Setter
	private int pos;

	public CharRangeNode(List<Pair<Character, Character>> ranges, List<Character> singles) {
		this.ranges = ranges;
		this.singles = singles;
	}

	@Override
	public boolean isNullable() {
		return false;
	}
}
