package info.rmapproject.transformer;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Before;
import org.junit.Test;

public class RMapTransformerTest {

	String inputPath;
	
	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("sampledata.json").getFile());

		inputPath = file.getParentFile().getAbsolutePath();
		
		try {
			inputPath = URLDecoder.decode(inputPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void testMain() {
		String[] args = {"-i", inputPath, "-iex", "json", "-oex", "disco"};
		RMapTransformer.main(args);
		//check output files
		
		
		//delete output files
	}

}
