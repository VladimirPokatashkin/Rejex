package syntaxtree.parser;

import java.security.InvalidParameterException;

public class ParsingException extends InvalidParameterException {
	public ParsingException(String message) {
		super(message);
	}
}