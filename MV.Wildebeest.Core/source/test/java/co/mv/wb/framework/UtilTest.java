package co.mv.wb.framework;


import org.junit.Assert;

public class UtilTest {

    @org.junit.Test
    public void coalesceWhiteSpacesSingleLine() {
        String text = " Text with    extra spaces.  In the start, middle and end ";
        String expected = "Text with extra spaces. In the start, middle and end";
        String result = Util.coalesceWhiteSpaces(text);
        Assert.assertEquals("White spaces should be coalesce to single white space", expected, result);
    }

    @org.junit.Test
    public void coalesceWhiteSpacesMultiLine() {
        String text = " Text with    extra spaces.  \nIn the start, middle and end \n";
        String expected = "Text with extra spaces. In the start, middle and end";
        String result = Util.coalesceWhiteSpaces(text);
        Assert.assertEquals("White spaces should be coalesce to single white space", expected, result);
    }
}