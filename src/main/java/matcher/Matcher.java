package matcher;

public sealed interface Matcher permits NFAMatcher, DFAMatcher {
	SearchResult search();
}