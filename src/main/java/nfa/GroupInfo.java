package nfa;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupInfo {
	private int index;
	private int begin;
	private int end;

	public GroupInfo(int index, int begin) {
		this.index = index;
		this.begin = begin;
	}
}