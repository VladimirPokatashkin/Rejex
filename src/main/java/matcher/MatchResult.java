package matcher;

import java.util.Map;

public record MatchResult(
		Map<Integer, String> groups,
		boolean isSuccess
) {
	public MatchResult() {
		this(null, false);
	}

	public MatchResult(Map<Integer, String> groups) {
		this(groups, true);
	}

	public String getGroupNumber(int num) {
		return groups.get(num);
	}
}