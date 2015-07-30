package ml.rugal.sshcommon.util;

/**
 *
 * @author Rugal Bernstein
 * @since 0.1
 *
 */
public class Num62
{

    /**
     * All digit, lower case [a-z] and upper case [A-Z]
     */
    public static final char[] N62_CHARS =
    {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
        'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    /**
     * All digit and lower case [a-z]
     */
    public static final char[] N36_CHARS =
    {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    /**
     * Use N36 to represent maximum length of long
     */
    public static final int LONG_N36_LEN = 13;

    /**
     * Use N62 to represent maximum length of long
     */
    public static final int LONG_N62_LEN = 11;

    private static StringBuilder longToNBuf(long l, char[] chars)
    {
//        final int upgrade = chars.length;
        StringBuilder result = new StringBuilder();
        int last;
        while (l > 0)
        {
            last = (int) (l % chars.length);
            result.append(chars[last]);
            l /= chars.length;
        }
        return result;
    }

    /**
     * Convert long type to N62.
     *
     * @param l The number to be converted.
     *
     * @return A N62 number.
     */
    public static String longToN62(long l)
    {
        return longToNBuf(l, N62_CHARS).reverse().toString();
    }

    /**
     * Convert long type to N36.
     *
     * @param l The number to be converted.
     *
     * @return A N36 number.
     */
    public static String longToN36(long l)
    {
        return longToNBuf(l, N36_CHARS).reverse().toString();
    }

    /**
     * Convert long type to N62.
     *
     * @param l      The number to be converted.
     * @param length minimum length need to append.
     *
     * @return A N62 number.
     */
    public static String longToN62(long l, int length)
    {
        StringBuilder sb = longToNBuf(l, N62_CHARS);
        for (int i = sb.length(); i < length; i++)
        {
            sb.append('0');
        }
        return sb.reverse().toString();
    }

    /**
     * Convert long type to N36.
     *
     * @param l      The number to be converted.
     * @param length minimum length need to append.
     *
     * @return A N36 number.
     */
    public static String longToN36(long l, int length)
    {
        StringBuilder sb = longToNBuf(l, N36_CHARS);
        for (int i = sb.length(); i < length; i++)
        {
            sb.append('0');
        }
        return sb.reverse().toString();
    }

    /**
     * Convert N62 to long type.
     *
     * @param n62 The number to be converted.
     *
     * @return A long type number.
     */
    public static long n62ToLong(String n62)
    {
        return nToLong(n62, N62_CHARS);
    }

    /**
     * Convert N36 to long type.
     *
     * @param n36 The number to be converted.
     *
     * @return A long type number.
     */
    public static long n36ToLong(String n36)
    {
        return nToLong(n36, N36_CHARS);
    }

    private static long nToLong(String s, char[] chars)
    {
        char[] nc = s.toCharArray();
        long result = 0;
        long pow = 1;
        for (int i = nc.length - 1; i >= 0; i--, pow *= chars.length)
        {
            int n = findNIndex(nc[i], chars);
            result += n * pow;
        }
        return result;
    }

    private static int findNIndex(char c, char[] chars)
    {
        for (int i = 0; i < chars.length; i++)
        {
            if (c == chars[i])
            {
                return i;
            }
        }
        throw new RuntimeException("N62(N36) Illegal character" + c);
    }
}
