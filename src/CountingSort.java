import java.util.AbstractMap.SimpleEntry;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

// Author: David Chan (Luckder)

public class CountingSort<T extends Comparable<T>> extends Sort<T> {
    // No Comparisons!!!

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.isEmpty() || list.size() == 1) { return list; }

        T sample = list.get(0).getKey();
        int n = list.size();
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (SimpleEntry<T, Integer> entry : list) {
            max = Math.max(max, getInt(entry.getKey()));
            min = Math.min(min, getInt(entry.getKey()));
        }

        int[] count = new int[max - min + 1];

        for (SimpleEntry<T, Integer> entry : list) {
            count[getInt(entry.getKey()) - min]++;
        }

        List<SimpleEntry<T, Integer>> sorted = new ArrayList<>();

        for (int i = 0; i < count.length; i++) {
            for (int j = 0; j < count[i]; j++) {
                // A new tail for each SimpleEntry key is made so the original tails are lost :(
                // Stability of list remains, artificially, since it is supposed to for
                // CountingSort, hence I cheated here a bit
                sorted.add(new SimpleEntry<>(giveInt(i + min, sample), j));
            }
        }

        return sorted;
    }

    private int getInt (T t) {
        if (t instanceof Number n) {
            // BigInteger and BigDecimal have intValueExact() which throws on overflow
            if (n instanceof BigInteger bi)  return bi.intValueExact();
            if (n instanceof BigDecimal bd)  return bd.intValueExact();

            // Long can exceed Integer.MAX_VALUE
            if (n instanceof Long l) {
                if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE)
                    throw new ArithmeticException("Long value " + l + " overflows int");
                return l.intValue();
            }

            // Double/Float can overflow AND have fractional parts
            if (n instanceof Double d) {
                if (d < Integer.MIN_VALUE || d > Integer.MAX_VALUE)
                    throw new ArithmeticException("Double value " + d + " overflows int");
                if (d != Math.floor(d))
                    throw new ArithmeticException("Double " + d + " has fractional part");
                return d.intValue();
            }
            if (n instanceof Float f) {
                if (f < Integer.MIN_VALUE || f > Integer.MAX_VALUE)
                    throw new ArithmeticException("Float value " + f + " overflows int");
                if (f != Math.floor(f))
                    throw new ArithmeticException("Float " + f + " has fractional part");
                return f.intValue();
            }

            // Byte (-128 to 127), Short (-32768 to 32767), Integer — always safe
            return n.intValue();
        }
        else if (t instanceof Character c) { return (int) c;      }
        else if (t instanceof Boolean b)   { return b ? 1 : 0;    }
        else if (t instanceof Enum<?> e)   { return e.ordinal();  }
        else if (t instanceof String s)    { // lossy, for reference
            try  { return Integer.parseInt(s);  }
            catch (NumberFormatException e) { throw e; }
        }
        else { throw new UnsupportedOperationException("Cannot convert " + t.getClass() + " to int"); }
    }

    private T giveInt(int i, T sample) {
        if (sample instanceof Integer)    return (T)(Integer) i;
        if (sample instanceof Long)       return (T)(Long)(long) i;
        if (sample instanceof Double)     return (T)(Double)(double) i;
        if (sample instanceof Float)      return (T)(Float)(float) i;
        if (sample instanceof Short)      return (T)(Short)(short) i;
        if (sample instanceof Byte)       return (T)(Byte)(byte) i;
        if (sample instanceof BigInteger) return (T) BigInteger.valueOf(i);
        if (sample instanceof BigDecimal) return (T) BigDecimal.valueOf(i);
        if (sample instanceof Character)  return (T)(Character)(char) i;
        if (sample instanceof Boolean)    return (T)(Boolean)(i == 1);
        if (sample instanceof Enum<?>)    return (T) sample.getClass().getEnumConstants()[i];
        if (sample instanceof String)    return (T) Integer.toString(i);
        throw new UnsupportedOperationException("Cannot convert int to " + sample.getClass());
    }

    @Override
    public String toString() {
        return "CountingSort";
    }

}
