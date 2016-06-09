package info.rmapproject.transformer;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void testIssnConverter(){
		String issn1 = "1234-1234";
		String issn2 = "1234-123X";
		String issn3 = "1234-X234";
		String issn4 = "12341234";
		String issn5 = "urn:issn:1234-1234";
		String issn6 = "issn 1234-1234";
		issn1 = TransformUtils.issnFormatter(issn1);
		assertTrue(issn1.equals("urn:issn:1234-1234"));
		issn2 = TransformUtils.issnFormatter(issn2);
		assertTrue(issn2.equals("urn:issn:1234-123X"));
		issn3 = TransformUtils.issnFormatter(issn3);
		assertTrue(issn3.equals("1234-X234"));
		issn4 = TransformUtils.issnFormatter(issn4);
		assertTrue(issn4.equals("urn:issn:12341234"));
		issn5 = TransformUtils.issnFormatter(issn5);
		assertTrue(issn5.equals("urn:issn:1234-1234"));
		issn6 = TransformUtils.issnFormatter(issn6);
		assertTrue(issn6.equals("urn:issn:1234-1234"));
	}
	
	@Test
	public void testIsbnConverter(){
		String isbn1 = "99921-58-10-7";
		String isbn2 = "80-902734-1-6";
		String isbn3 = "0-9752298-0-X";
		String isbn4 = "097522980X";
		String isbn5 = "urn:isbn:0-9752298-0-X";
		String isbn6 = "isbn 0-9752298-0-X";
		isbn1 = TransformUtils.isbnFormatter(isbn1);
		assertTrue(isbn1.equals("urn:isbn:99921-58-10-7"));
		isbn2 = TransformUtils.isbnFormatter(isbn2);
		assertTrue(isbn2.equals("urn:isbn:80-902734-1-6"));
		isbn3 = TransformUtils.isbnFormatter(isbn3);
		assertTrue(isbn3.equals("urn:isbn:0-9752298-0-X"));
		isbn4 = TransformUtils.isbnFormatter(isbn4);
		assertTrue(isbn4.equals("urn:isbn:097522980X"));
		isbn5 = TransformUtils.isbnFormatter(isbn5);
		assertTrue(isbn5.equals("urn:isbn:0-9752298-0-X"));
		isbn6 = TransformUtils.isbnFormatter(isbn6);
		assertTrue(isbn6.equals("urn:isbn:0-9752298-0-X"));
		
	}
}
