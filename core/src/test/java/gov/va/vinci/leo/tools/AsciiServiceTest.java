package gov.va.vinci.leo.tools;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by thomasginter on 3/28/16.
 */
public class AsciiServiceTest {

    @Test
    public void testAscii7() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append((char)135);
        sb.append((char)225);
        sb.append((char)134);
        String nonAscii7 = sb.toString();
        assertEquals("\u0087á\u0086", nonAscii7);
        String ascii7 = AsciiService.toASCII7(nonAscii7);
        assertEquals("cat", ascii7);
    }

    @Test
    public void testBadAscii7() throws Exception {
        StringBuilder sb = new StringBuilder(1);
        sb.append((char) 355);
        String res = AsciiService.toASCII7(sb.toString());
        assertEquals("_", res);
    }
}
