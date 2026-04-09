package parser.impl;

import parser.IParser;
import parser.ParsingException;
import syntaxtree.GroupNode;
import syntaxtree.LiteralNode;
import syntaxtree.ASTNode;

public class Parser implements IParser {
	private String input;
	private int groupCnt = 0;
	private int pos = 0;


	private char peek() {
		return pos < input.length() ? input.charAt(pos) : '\0';
	}

	private char next() {
		return input.charAt(pos++);
	}

	private boolean matches(char c) {
		if (peek() == c) {
			++pos;
			return true;
		}
		return false;
	}

	private void expect(char c) throws ParsingException {
		char got = peek();
		if (got == c) {
			++pos;
		} else throw new ParsingException("unexpected token: " + got + ". expected: " + c);
	}

	private boolean isGroupEnd() {
		char c = peek();
		return c == '/' || c == '|' || c == ')' || c == '\0';
	}

	private ASTNode parseLiteral() throws ParsingException {
		char c = next();

		switch (c) {
			case '%' -> {
				return new LiteralNode(String.valueOf(next()));
			}
			case '$' -> {
				return new LiteralNode("");
			}
			case '(' -> {
				ASTNode node = parseChoice();
				expect(')');
				return new GroupNode(node, ++groupCnt);
			}
			case '[' -> {

			}
		}
	}

	private ASTNode parseRange() {

	}

	private ASTNode parseRepetition() {

	}

	private ASTNode parseConcatenation() {

	}

	private ASTNode parseLookahead() {

	}

	private ASTNode parseChoice() {
		ASTNode node = parseLookahead();
	}



	public Parser(String input) {
		this.input = input;
	}

	@Override
	public ASTNode parse() {
		return null;
	}
}