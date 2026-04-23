package pattern;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PatternTest {
	@Test
	public void concatenationDotTest() {
		Pattern pattern = Pattern.compile("a.b");
		assertTrue(pattern.matches("ab"));
		assertFalse(pattern.matches("a b"));
	}

	@Test
	public void concatenationTest() {
		Pattern pattern = Pattern.compile("ab");
		assertTrue(pattern.matches("ab"));
		assertFalse(pattern.matches("abb"));
	}

	@Test
	public void starOperatorTest() {
		Pattern pattern = Pattern.compile("j*");
		assertTrue(pattern.matches(""));
		assertTrue(pattern.matches("j"));
		assertTrue(pattern.matches("jjj"));
		assertFalse(pattern.matches("jaboba"));

		pattern = Pattern.compile("(ab)*");
		assertTrue(pattern.matches(""));
		assertTrue(pattern.matches("ababab"));
		assertFalse(pattern.matches("aba"));
	}

	@Test
	public void threeDotsOperatorTest() {
		Pattern pattern = Pattern.compile("j…");
		assertTrue(pattern.matches(""));
		assertTrue(pattern.matches("j"));
		assertTrue(pattern.matches("jjj"));
		assertFalse(pattern.matches("jaboba"));

		pattern = Pattern.compile("a…b");
		assertTrue(pattern.matches("b"));
		assertTrue(pattern.matches("aaaaab"));
		assertFalse(pattern.matches("aaaajb"));
	}

	@Test
	public void plusOperatorTest() {
		Pattern pattern = Pattern.compile("j+");
		assertFalse(pattern.matches(""));
		assertTrue(pattern.matches("j"));
		assertTrue(pattern.matches("jjj"));
		assertFalse(pattern.matches("jaboba"));

		pattern = Pattern.compile("a+b");
		assertFalse(pattern.matches("b"));
		assertTrue(pattern.matches("ab"));
		assertTrue(pattern.matches("aaaaab"));
		assertFalse(pattern.matches("aaaajb"));
	}

	@Test
	public void braceOperatorTest() {
		Pattern pattern = Pattern.compile("j{,8}");
		assertTrue(pattern.matches(""));
		assertTrue(pattern.matches("jjjjj"));
		assertFalse(pattern.matches("jjjjjjjjjj"));
		assertFalse(pattern.matches("jaja"));

		pattern = Pattern.compile("(jq){2,6}");
		assertTrue(pattern.matches("jqjq"));
		assertTrue(pattern.matches("jqjqjqjqjq"));
		assertFalse(pattern.matches("jq"));
		assertFalse(pattern.matches("jqjqjqjqjqjqjqjq"));
		assertFalse(pattern.matches("jqjqjqqqqq"));

		pattern = Pattern.compile("j{1,3}q{1,2}");
		assertTrue(pattern.matches("jqq"));
		assertTrue(pattern.matches("jjjq"));
		assertFalse(pattern.matches("qq"));
		assertFalse(pattern.matches("jjjjq"));
		assertFalse(pattern.matches("j"));
	}

	@Test
	public void choiceTest() {
		Pattern pattern = Pattern.compile("j|q");
		assertTrue(pattern.matches("j"));
		assertTrue(pattern.matches("q"));
		assertFalse(pattern.matches("jq"));

		pattern = Pattern.compile("ja|qq");
		assertTrue(pattern.matches("ja"));
		assertTrue(pattern.matches("qq"));
		assertFalse(pattern.matches("jaqq"));

		pattern = Pattern.compile("j(a|b)+q");
		assertTrue(pattern.matches("jaq"));
		assertTrue(pattern.matches("jbq"));
		assertTrue(pattern.matches("jabaaabq"));
	}

	@Test
	public void charRangeTest() {
		Pattern pattern = Pattern.compile("[a-z]+");
		assertTrue(pattern.matches("aboba"));
		assertTrue(pattern.matches("java"));
		assertFalse(pattern.matches("ab0ba"));
		assertFalse(pattern.matches("Aboba"));

		pattern = Pattern.compile("[a-zA-Z8]+");
		assertTrue(pattern.matches("aboba"));
		assertTrue(pattern.matches("Aboba"));
		assertTrue(pattern.matches("aboba8"));
		assertFalse(pattern.matches("ab0ba"));
	}

	@Test
	public void GroupTest() {
		Pattern pattern = Pattern.compile("(a(bc)d)");
		assertTrue(pattern.matches("abcd"));
		assertEquals("abcd", pattern.getMatchResult().group(1));
		assertEquals("bc", pattern.getMatchResult().group(2));

		pattern = Pattern.compile("((a|b)+)c");
		assertTrue(pattern.matches("ababac"));
		assertEquals("a", pattern.getMatchResult().group(2));
		assertEquals("ababa", pattern.getMatchResult().group(1));
	}

	@Test
	public void dollarOperatorTest() {
		Pattern pattern = Pattern.compile("a$b$c");
		assertTrue(pattern.matches("abc"));
		assertFalse(pattern.matches("a b c"));

		pattern = Pattern.compile("j|$+");
		assertTrue(pattern.matches("j"));
		assertTrue(pattern.matches(""));
		assertFalse(pattern.matches("j "));
		assertFalse(pattern.matches(" "));
	}

	@Test
	public void lookaheadOperatorTest() {
		Pattern pattern = Pattern.compile("a/(b|c)");
		assertTrue(pattern.matches("ab"));
		assertTrue(pattern.matches("ac"));
		assertFalse(pattern.matches("aa"));

		pattern = Pattern.compile("j/(a*)q");
		assertTrue(pattern.matches("jq"));
		assertTrue(pattern.matches("jaaaq"));
		assertFalse(pattern.matches("jdq"));
	}

	@Test
	public void perCentOperatorTest() {
		Pattern pattern = Pattern.compile("a%{1,4%}b");
		assertTrue(pattern.matches("a{1,4}b"));
		assertFalse(pattern.matches("aaab"));

		pattern = Pattern.compile("%%%*");
		assertTrue(pattern.matches("%*"));
		assertFalse(pattern.matches("*"));
		assertFalse(pattern.matches("%%"));

		pattern = Pattern.compile("[a%-z]");
		assertTrue(pattern.matches("-"));
		assertTrue(pattern.matches("a"));
		assertTrue(pattern.matches("z"));
		assertFalse(pattern.matches("j"));
	}

	@Test
	public void complexExpressionTest() {
		Pattern pattern = Pattern.compile("[a-z]+@[a-z]+%.[a-z]+");
		assertTrue(pattern.matches("aboba@gmail.com"));
		assertTrue(pattern.matches("gavi@barca.com"));
		assertFalse(pattern.matches("ab0ba@mail.ru"));
		assertFalse(pattern.matches("abobamail.ru"));
		assertFalse(pattern.matches("@aboba.com"));
		assertFalse(pattern.matches("aboba@BEBE.com"));
		assertFalse(pattern.matches("aboba@yandex"));

		pattern = Pattern.compile("(a|b+)(c|d*)");
		assertTrue(pattern.matches("ac"));
		assertTrue(pattern.matches("bb"));
		assertTrue(pattern.matches("bbbddd"));
		assertFalse(pattern.matches("ab"));
		assertFalse(pattern.matches("b c"));
	}
}