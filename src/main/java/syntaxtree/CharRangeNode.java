package syntaxtree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import other.Pair;

import java.util.List;

@Getter
@AllArgsConstructor
public final class CharRangeNode implements ASTNode {
	private List<Pair<Character, Character>> ranges;
	private List<Character> singles;

	@Override
	public boolean isNullable() {
		return false;
	}
}
