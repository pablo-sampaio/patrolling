package yaps.files;


@SuppressWarnings("serial")
public class YapsParsingException extends Exception {
	public YapsParsingException(String str) {
		super(str);
	}
	public YapsParsingException(String expected, String found, int line) {
		super("Expected \"" + expected + "\" but found \"" + found 
				+ "\" in line " + line);
	}
	public YapsParsingException(char expected, char found, int line) {
		super("Expected \"" + expected + "\" but found \"" + found 
				+ "\" in line " + line);
	}
}
