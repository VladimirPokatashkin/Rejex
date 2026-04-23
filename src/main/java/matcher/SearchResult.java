package matcher;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public record SearchResult(
		Map<Integer, String> groups,
		int begin,
		int end,
		boolean isSuccess
) implements Iterable<String> {
	public SearchResult() {
		this(null, -1, -1, false);
	}

	public String group(int num) {
		return groups.get(num);
	}

	@Override
	public Iterator<String> iterator() {
		return groups == null ? Collections.emptyIterator() : groups.values().iterator();
	}
}