package pattern;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import matcher.SearchResult;

public class SearchTest {
	@Test
	public void concatenationDotTest() {
		Pattern pattern = Pattern.compile("a.b");
		SearchResult result = pattern.search("xabx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(3, result.end());

		result = pattern.search("aab", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(3, result.end());

		result = pattern.search("xa bx", false);
		assertFalse(result.isSuccess());
	}

	@Test
	public void concatenationTest() {
		Pattern pattern = Pattern.compile("ab");
		SearchResult result = pattern.search("xabx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(3, result.end());

		result = pattern.search("ab", false);
		assertTrue(result.isSuccess());
		assertEquals(0, result.begin());
		assertEquals(2, result.end());

		result = pattern.search("xbbx", false);
		assertFalse(result.isSuccess());
	}

	@Test
	public void starOperatorTest() {
		Pattern pattern = Pattern.compile("j*");
		SearchResult result = pattern.search("xyz", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(1, result.end());

		result = pattern.search("xjx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		result = pattern.search("jjjx", false);
		assertTrue(result.isSuccess());
		assertEquals(0, result.begin());
		assertEquals(3, result.end());

		result = pattern.search("xjabobax", false);
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		pattern = Pattern.compile("(ab)*");
		result = pattern.search("xyz", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(1, result.end());

		result = pattern.search("ababab", false);
		assertTrue(result.isSuccess());
		assertEquals(0, result.begin());
		assertEquals(6, result.end());
	}

	@Test
	public void threeDotsOperatorTest() {
		Pattern pattern = Pattern.compile("j…");
		SearchResult result = pattern.search("xyz", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(1, result.end());

		result = pattern.search("xjx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		result = pattern.search("jjjx", false);
		assertTrue(result.isSuccess());
		assertEquals(0, result.begin());
		assertEquals(3, result.end());

		result = pattern.search("xjabobax", false);
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		pattern = Pattern.compile("(ab)*");
		result = pattern.search("xyz", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(1, result.end());

		result = pattern.search("ababab", false);
		assertTrue(result.isSuccess());
		assertEquals(0, result.begin());
		assertEquals(6, result.end());
	}

	@Test
	public void plusOperatorTest() {
		Pattern pattern = Pattern.compile("j+");
		SearchResult result = pattern.search("xyz", false);
		assertFalse(result.isSuccess());

		result = pattern.search("xjx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		result = pattern.search("xjjjx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(4, result.end());

		result = pattern.search("xjabobax", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		pattern = Pattern.compile("a+b");
		result = pattern.search("xbx", false);
		assertFalse(result.isSuccess());

		result = pattern.search("xabx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(3, result.end());

		result = pattern.search("xaaaaabx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(7, result.end());

		result = pattern.search("xaaaajbx", false);
		assertFalse(result.isSuccess());
	}

	@Test
	public void braceOperatorTest() {
		Pattern pattern = Pattern.compile("j{,8}");
		SearchResult result = pattern.search("xyz", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(1, result.end());

		result = pattern.search("xjjjjjx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(6, result.end());

		result = pattern.search("jjjjjjjjjjx", false);
		assertTrue(result.isSuccess());
		assertEquals(0, result.begin());
		assertEquals(8, result.end());

		result = pattern.search("xjajax", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		pattern = Pattern.compile("(jq){2,6}");
		result = pattern.search("xjqjqx", true);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(5, result.end());
		assertEquals("jq", result.group(1));

		result = pattern.search("xjqjqjqjqjqx", true);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(11, result.end());
		assertEquals("jq", result.group(1));

		result = pattern.search("xjqx", false);
		assertFalse(result.isSuccess());

		result = pattern.search("xjqqqqqx", false);
		assertFalse(result.isSuccess());

		pattern = Pattern.compile("j{1,3}q{1,2}");
		result = pattern.search("xjqqx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(4, result.end());

		result = pattern.search("xjjjqx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(5, result.end());

		result = pattern.search("xqqx", false);
		assertFalse(result.isSuccess());

		result = pattern.search("xjjjjx", false);
		assertFalse(result.isSuccess());
	}

	@Test
	public void choiceTest() {
		Pattern pattern = Pattern.compile("j|q");
		SearchResult result = pattern.search("xjx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		result = pattern.search("xqx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		result = pattern.search("xjqx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		pattern = Pattern.compile("ja|qq");
		result = pattern.search("xjax", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(3, result.end());

		result = pattern.search("xqqx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(3, result.end());

		result = pattern.search("xjaqqx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(3, result.end());

		pattern = Pattern.compile("j(a|b)+q");
		result = pattern.search("xjaqx", true);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(4, result.end());
		assertEquals("a", result.group(1));

		result = pattern.search("xjbqx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(4, result.end());

		result = pattern.search("xjabaaabqx", true);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(9, result.end());
		assertEquals("b", result.group(1));
	}

	@Test
	public void charRangeTest() {
		Pattern pattern = Pattern.compile("[a-z]+");
		SearchResult result = pattern.search("xabobax", false);
		assertTrue(result.isSuccess());
		assertEquals(0, result.begin());
		assertEquals(7, result.end());

		result = pattern.search("123java456", false);
		assertTrue(result.isSuccess());
		assertEquals(3, result.begin());
		assertEquals(7, result.end());

		result = pattern.search("8888", false);
		assertFalse(result.isSuccess());

		pattern = Pattern.compile("[a-zA-Z8]+");
		result = pattern.search("123aboba456", false);
		assertTrue(result.isSuccess());
		assertEquals(3, result.begin());
		assertEquals(8, result.end());

		result = pattern.search("123Aboba456", false);
		assertTrue(result.isSuccess());
		assertEquals(3, result.begin());
		assertEquals(8, result.end());

		result = pattern.search("123aboba8456", false);
		assertTrue(result.isSuccess());
		assertEquals(3, result.begin());
		assertEquals(9, result.end());

		result = pattern.search("1230456", false);
		assertFalse(result.isSuccess());
	}

	@Test
	public void GroupTest() {
		Pattern pattern = Pattern.compile("(a(bc)d)");
		SearchResult result = pattern.search("xabcdx", true);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(5, result.end());
		assertEquals("abcd", result.group(1));
		assertEquals("bc", result.group(2));

		pattern = Pattern.compile("((a|b)+)c");
		result = pattern.search("xababacx", true);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(7, result.end());
		assertEquals("ababa", result.group(1));
		assertEquals("a", result.group(2));
	}

	@Test
	public void dollarOperatorTest() {
		Pattern pattern = Pattern.compile("a$b$c");
		SearchResult result = pattern.search("xabcx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(4, result.end());

		result = pattern.search("xa b cx", false);
		assertFalse(result.isSuccess());

		pattern = Pattern.compile("j|$+");
		result = pattern.search("xyz", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(1, result.end());

		result = pattern.search("x x", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(1, result.end());
	}

	@Test
	public void lookaheadOperatorTest() {
		Pattern pattern = Pattern.compile("a/(b|c)");
		SearchResult result = pattern.search("xabx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		result = pattern.search("xacx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		result = pattern.search("xaax", false);
		assertFalse(result.isSuccess());

		pattern = Pattern.compile("j/(a*)q");
		result = pattern.search("xjqx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		result = pattern.search("xjaaaqx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(5, result.end());

		result = pattern.search("xjdqx", false);
		assertFalse(result.isSuccess());
	}

	@Test
	public void perCentOperatorTest() {
		Pattern pattern = Pattern.compile("a%{1,4%}b");
		SearchResult result = pattern.search("xa{1,4}bx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(8, result.end());

		result = pattern.search("xaaabx", false);
		assertFalse(result.isSuccess());

		pattern = Pattern.compile("%%%*");
		result = pattern.search("x%*x", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(3, result.end());

		result = pattern.search("x*x", false);
		assertFalse(result.isSuccess());

		result = pattern.search("x%%x", false);
		assertFalse(result.isSuccess());

		pattern = Pattern.compile("[a%-z]");
		result = pattern.search("x-x", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		result = pattern.search("xax", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		result = pattern.search("xzx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(2, result.end());

		result = pattern.search("xjx", false);
		assertFalse(result.isSuccess());
	}

	@Test
	public void complexExpressionTest() {
		Pattern pattern = Pattern.compile("[a-z]+@[a-z]+%.[a-z]+");
		SearchResult result = pattern.search("Email: aboba@gmail.com for contact", false);
		assertTrue(result.isSuccess());
		assertEquals(7, result.begin());
		assertEquals(22, result.end());

		result = pattern.search("gavi@barca.com is valid", false);
		assertTrue(result.isSuccess());
		assertEquals(0, result.begin());
		assertEquals(14, result.end());

		result = pattern.search("ab0ba@mail.ru is invalid", false);
		assertTrue(result.isSuccess());
		assertEquals(3, result.begin());
		assertEquals(13, result.end());

		result = pattern.search("abobamail.ru missing @", false);
		assertFalse(result.isSuccess());

		result = pattern.search("@aboba.com missing name", false);
		assertFalse(result.isSuccess());

		result = pattern.search("aboba@BEBE.com has uppercase", false);
		assertFalse(result.isSuccess());

		result = pattern.search("aboba@yandex missing domain", false);
		assertFalse(result.isSuccess());

		pattern = Pattern.compile("(a|b+)(c|d*)");
		result = pattern.search("xacx", true);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(3, result.end());
		assertEquals("a", result.group(1));
		assertEquals("c", result.group(2));

		result = pattern.search("xbbx", false);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(3, result.end());

		result = pattern.search("xbbbdddx", true);
		assertTrue(result.isSuccess());
		assertEquals(1, result.begin());
		assertEquals(7, result.end());
		assertEquals("bbb", result.group(1));
		assertEquals("ddd", result.group(2));


		result = pattern.search("x cx", false);
		assertFalse(result.isSuccess());
	}
}