package testing;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import "LoggingForm.java"
import "FsPair.java"

public class jUnitTesting {

	@Test
	public void returnFSPairWithFileName(){
		FSPair tester = new FSPair;
		assertEquals("must return correct file name","www.google.com",tester.memLookup("www.google.com"));
	}
	
	public void returnFSPairCacheLookup(){
		FSPair tester = new FSPair;
		assertEquals("must return correct file name","www.google.com",tester.cacheLookup("www.google.com"));
	}
	
	
	

}
