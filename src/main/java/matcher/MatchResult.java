package matcher;

import java.util.List;

public record MatchResult(
		List<String> groups,
		boolean isSuccess
) {
	public MatchResult() {
		this(null, false);
	}

	public String getGroupNumber(int num) {
		return groups.get(num);
	}
}