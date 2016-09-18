package com.volvo.gloria.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

    /*
     * Tests if md5Checksum is broken in any way
     */
    @Test
    public void md5Checksum() throws NoSuchAlgorithmException {
        String string = Utils.md5Checksum("abcde fghijk lmn op");
        Assert.assertEquals("1580420c86bbc3b356f6c40d46b53fc8", string);
    }
    /*
     * Tests if isNumeric is broken in any way
     */
    @Test
    public void isNumeric()   {
        Boolean bool = Utils.isNumeric("123");
        Assert.assertEquals(true, bool);
    }
    
    /*
     * Tests if isNumeric is broken in any way
     */
    @Test
    public void isNumericNegetive()   {
        Boolean bool = Utils.isNumeric("123a");
        Assert.assertEquals(false, bool);
    }
    
    /*
     * Tests if isNumeric is broken in any way
     */
    @Test
    public void compressAndDecompress() throws  IOException {
        byte[] compressed = Utils.compress("123a");
        String decompressed = Utils.decompress(compressed);
        Assert.assertEquals("123a", decompressed);
    }
    
    /*
     * Tests if hasSameItems is broken in any way
     */
    @Test
    public void hasSameItems() throws IOException {
        Assert.assertEquals(true, Utils.hasSameItems("a,b,c", "d,e,c,f"));
    }
    
    /*
     * Tests if createRandomNumber is broken in any way
     */
    @Test
    public void createRandomNumber() throws IOException {
        Assert.assertNotNull(Utils.createRandomNumber());
    }
    
    /*
     * Tests if compress is broken in any way
     */
    @Test
    public void compress() throws  IOException {
        byte[] compressed = Utils.compress("");
        Assert.assertEquals(compressed.length, 0);
    }
    /*
     * Tests if compress is broken in any way
     */
    @Test
    public void compress1() throws  IOException {
        byte[] compressed = Utils.compress(null);
        Assert.assertEquals(compressed.length, 0);
    }
    /*
     * Tests if roundOffDecimalPoints is broken in any way
     */
    @Test
    public void roundOffDecimalPoints() throws  IOException {
        BigDecimal actual = Utils.roundOffDecimalPoints(new BigDecimal(1), 2, 2);
        Assert.assertEquals("100.00", actual.toString());
    }
    
    
}
