package matcher;

public sealed interface Matcher permits NFAMatcher, DFAMatcher {
	MatchResult match();
}