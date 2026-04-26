package pattern;

import matcher.SearchResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DifferenceTest {
	@Test
	void differenceAllExceptATest() {
		Pattern all = Pattern.compile("[a-z]");
		Pattern a = Pattern.compile("a");
		Pattern diff = all.difference(a);

		SearchResult result = diff.search("b", false);
		assertTrue(result.isSuccess());

		result = diff.search("a", false);
		assertFalse(result.isSuccess());

		result = diff.search("j", false);
		assertTrue(result.isSuccess());
	}

	@Test
	void differenceConsonantsTest() {
		Pattern letters = Pattern.compile("[a-z]");
		Pattern vowels = Pattern.compile("[aeiou]");
		Pattern consonants = letters.difference(vowels);

		assertFalse(consonants.search("a", false).isSuccess());
		assertFalse(consonants.search("e", false).isSuccess());
		assertFalse(consonants.search("i", false).isSuccess());

		assertTrue(consonants.search("j", false).isSuccess());
		assertTrue(consonants.search("v", false).isSuccess());
		assertTrue(consonants.search("m", false).isSuccess());
	}

	@Test
	void differenceDigitsExceptZeroAndOneTest() {
		Pattern digits = Pattern.compile("[0-9]");
		Pattern zeroOne = Pattern.compile("[01]");
		Pattern diff = digits.difference(zeroOne);

		assertFalse(diff.search("0", false).isSuccess());
		assertFalse(diff.search("1", false).isSuccess());

		assertTrue(diff.search("2", false).isSuccess());
		assertTrue(diff.search("5", false).isSuccess());
		assertTrue(diff.search("9", false).isSuccess());
	}

	@Test
	void differenceWordsLength3ExceptAbcTest() {
		Pattern threeLetters = Pattern.compile("[a-z][a-z][a-z]");
		Pattern abc = Pattern.compile("abc");
		Pattern diff = threeLetters.difference(abc);

		assertFalse(diff.search("abc", false).isSuccess());

		assertTrue(diff.search("abd", false).isSuccess());
		assertTrue(diff.search("xyz", false).isSuccess());
		assertTrue(diff.search("aaa", false).isSuccess());
		assertTrue(diff.search("jvm", false).isSuccess());
	}

	@Test
	void differenceASequenceExceptAATest() {
		Pattern aStar = Pattern.compile("a*");
		Pattern aa = Pattern.compile("aa");
		Pattern diff = aStar.difference(aa);

		assertTrue(diff.search("", false).isSuccess());
		assertTrue(diff.search("a", false).isSuccess());
		assertTrue(diff.search("aaa", false).isSuccess());
		assertTrue(diff.search("aaaa", false).isSuccess());
	}

	@Test
	void differenceEmailsExceptGmailTest() {
		Pattern emails = Pattern.compile("[a-z]+@[a-z]+%.[a-z]+");
		Pattern gmail = Pattern.compile("[a-z]+@gmail%.c");
		Pattern diff = emails.difference(gmail);

		assertFalse(diff.search("aboba@gmail.c", false).isSuccess());
		assertFalse(diff.search("ivanzolo@gmail.c", false).isSuccess());

		assertTrue(diff.search("gavi@barca.com", false).isSuccess());
		assertTrue(diff.search("test@mail.ru", false).isSuccess());
		assertTrue(diff.search("dvenashka@mephi.ru", false).isSuccess());
	}

	@Test
	void differenceEmptyMinusPatternTest() {
		Pattern empty = Pattern.compile("[]");
		Pattern any = Pattern.compile("a");
		Pattern diff = empty.difference(any);

		assertFalse(diff.search("aboba", false).isSuccess());
		assertFalse(diff.search("jvm", false).isSuccess());
		assertFalse(diff.search("", false).isSuccess());
	}

	@Test
	void differencePatternMinusEmptyTest() {
		Pattern pattern = Pattern.compile("a+");
		Pattern empty = Pattern.compile("");
		Pattern diff = pattern.difference(empty);

		assertTrue(diff.search("a", false).isSuccess());
		assertTrue(diff.search("aa", false).isSuccess());
		assertFalse(diff.search("b", false).isSuccess());
		assertFalse(diff.search("", false).isSuccess());
	}

	@Test
	void testDifferencePatternMinusItself() {
		Pattern pattern = Pattern.compile("a+b+c+");
		Pattern diff = pattern.difference(pattern);

		assertFalse(diff.search("abc", false).isSuccess());
		assertFalse(diff.search("aabbcc", false).isSuccess());
		assertFalse(diff.search("aaa", false).isSuccess());
		assertFalse(diff.search("", false).isSuccess());
	}

	@Test
	void differenceDisjointPatternsTest() {
		Pattern digits = Pattern.compile("[0-9]+");
		Pattern letters = Pattern.compile("[a-z]+");
		Pattern diff = digits.difference(letters);

		assertTrue(diff.search("123", false).isSuccess());
		assertTrue(diff.search("0", false).isSuccess());
		assertFalse(diff.search("abc", false).isSuccess());
		assertFalse(diff.search("a", false).isSuccess());
	}

	@Test
	void differenceNoGroupsSupportTest() {
		Pattern pattern1 = Pattern.compile("(a)(b)");
		Pattern pattern2 = Pattern.compile("ab");
		Pattern diff = pattern1.difference(pattern2);

		assertThrows(IllegalArgumentException.class, () -> diff.search("test", true));
	}

	@Test
	void lookaheadDifferenceTest() {
		Pattern a = Pattern.compile("[a-z]/[123]");
		Pattern b = Pattern.compile("[a-z]/3");
		Pattern diff = a.difference(b);

		assertTrue(diff.search("aboba23").isSuccess());
		assertFalse(diff.search("aboba3").isSuccess());
	}

	@Test
	void differenceComplexWithRepetitionsTest() {
		Pattern hasA = Pattern.compile("[ab]*ab[ab]*");
		Pattern endsWithB = Pattern.compile("[ab]*b");
		Pattern diff = hasA.difference(endsWithB);

		assertTrue(diff.search("abbaba", false).isSuccess());
		assertTrue(diff.search("aabaa", false).isSuccess());
		assertTrue(diff.search("bbabaa", false).isSuccess());

		assertFalse(diff.search("ab", false).isSuccess());
		assertFalse(diff.search("b", false).isSuccess());
		assertFalse(diff.search("aaaaab", false).isSuccess());
		assertFalse(diff.search("bbbb", false).isSuccess());
	}
}