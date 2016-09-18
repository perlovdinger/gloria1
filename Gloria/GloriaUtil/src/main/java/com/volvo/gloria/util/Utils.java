package com.volvo.gloria.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.volvo.gloria.util.c.GloriaExceptionConstants;

/**
 * Utility methods.
 */
public final class Utils {
    private static final int RANDOM_STRING_LENGTH = 32;
    private static final int MAX_PRE_DECIMAL_LENGTH = 9;
    private static final int MAX_DECIMAL_LENGTH = 3;
    public static final int INDEX_NUMBER = 10;
    public static final int MAX_NUMBER = 999999;
    private static final String HUNDRED = "100";
    private static final String THOUSAND = "1000";
    private static final int TWO = 2;
    private static final int ONE = 1;
    private static final int THREE = 3;
    private static final String TEN = "10";
    private static final int LENGTHOFMD5CHECKSUM = 16;
    private static Random random = new Random();

    private Utils() {

    }

    public static String createRandomString() {
        return RandomStringUtils.randomAlphanumeric(RANDOM_STRING_LENGTH);
    }

    public static String createRandomString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static void validatePostiveNumberLong(Long quantity) throws GloriaApplicationException {
        if (quantity != null && quantity < 0) {
            throw new GloriaApplicationException(GloriaExceptionConstants.NEGATIVE_NUMBER, "Negative values are not allowed.", null);
        }
    }

    public static void validatePostiveNumberDouble(Double quantity) throws GloriaApplicationException {
        if (quantity != null && quantity < 0) {
            throw new GloriaApplicationException(GloriaExceptionConstants.NEGATIVE_NUMBER, "Negative values are not allowed.", null);
        }
    }
    
    public static long createRandomNumber() {
        return random.nextLong();
    }

    /**
     * Compress String to compressed byte array.
     * 
     * @param originalString
     * @return compressed byte array
     * @throws IOException
     */
    public static byte[] compress(String originalString) throws IOException {
        if (originalString == null || originalString.length() == 0) {
            return new byte[0];
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(baos);
        gzip.write(originalString.getBytes("UTF-8"));
        gzip.close();

        return baos.toByteArray();
    }

    /**
     * Decompress byte array to string.
     * 
     * @param compressedBytes
     * @return Decompressed string
     * @throws IOException
     */
    public static String decompress(byte[] compressedBytes) throws IOException {
        if (compressedBytes == null || compressedBytes.length == 0) {
            return null;
        }
        StringBuilder decompressedString = new StringBuilder();

        ByteArrayInputStream bais = new ByteArrayInputStream(compressedBytes);
        BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(bais)));

        String readed;
        while ((readed = in.readLine()) != null) {
            decompressedString.append(readed);
        }
        return decompressedString.toString();
    }

    /**
     * Validating max price.
     * 
     * @param price
     * @return
     */
    public static boolean validateMaxPrice(Double maxPrice) {
        if (maxPrice == null) {
            return true;
        }
        BigDecimal price = BigDecimal.valueOf(maxPrice);
        if (-1 == price.compareTo(new BigDecimal(0))) {
            return false;
        }
        String[] splitter = price.toString().split("\\.");
        int preDecimalLength = splitter[0].length();
        int decimalLength = splitter.length > 1 ? splitter[1].length() : 0;

        return preDecimalLength <= MAX_PRE_DECIMAL_LENGTH && decimalLength <= MAX_DECIMAL_LENGTH;
    }

    public static String generateDispatchNoteNo() {
        Random r = new Random();
        List<Integer> codes = new ArrayList<Integer>();
        for (int i = 0; i < INDEX_NUMBER; i++) {
            int x = r.nextInt(MAX_NUMBER);
            while (codes.contains(x)) {
                x = r.nextInt(MAX_NUMBER);
            }
            codes.add(x);
        }
        return String.format("%06d", codes.get(0));
    }

    public static String generatePickListCode() {
        Random r = new Random();
        return String.format("%04d", r.nextInt(MAX_NUMBER));
    }

    public static boolean hasSameItems(String valueOne, String valueTwo) {
        Set<String> valueSetOne = new HashSet<String>(Arrays.asList(valueOne.split(",")));
        Set<String> valueSetTwo = new HashSet<String>(Arrays.asList(valueTwo.split(",")));
        if (CollectionUtils.containsAny(valueSetOne, valueSetTwo)) {
            return true;
        }
        return false;
    }

    public static boolean isNumeric(String value) {
        if (value.matches("^[0-9]*$")) {
            return true;
        }
        return false;
    }

    public static BigDecimal roundOffDecimalPoints(BigDecimal value, int fromScale, int toScale) {
        BigDecimal decimal = null;
        if (fromScale == ONE) {
            BigDecimal ten = new BigDecimal(TEN);
            decimal = value.multiply(ten).setScale(toScale, RoundingMode.CEILING);
        } else if (fromScale == TWO) {
            BigDecimal hundred = new BigDecimal(HUNDRED);
            decimal = value.multiply(hundred).setScale(toScale, RoundingMode.CEILING);
        } else if (fromScale == THREE) {
            BigDecimal thousand = new BigDecimal(THOUSAND);
            decimal = value.multiply(thousand).setScale(toScale, RoundingMode.CEILING);
        }
        return decimal;
    }

    public static String md5Checksum(String s) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(s.getBytes(), 0, s.length());
        return new BigInteger(1, m.digest()).toString(LENGTHOFMD5CHECKSUM);
    }
    
    public static int compare(long val1, long val2) {
        if (val1 > val2) {
            return 1;
        } else if (val1 < val2) {
            return -1;
        }
        return 0;
    }
}
