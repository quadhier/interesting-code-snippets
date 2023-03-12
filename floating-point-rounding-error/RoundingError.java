import java.math.BigDecimal;
import java.math.MathContext;
import java.util.stream.IntStream;
import java.io.*;

public class RoundingError {

  public static double[] sampleArray(double[] arr, int sampleIntv) {
    return IntStream.range(0, arr.length)
                    .filter(i -> i % sampleIntv == 0)
                    .mapToDouble(i -> arr[i])
                    .toArray();
  }

  //   "1" -> "0.01"
  //  "10" -> "0.10"
  // "100" -> "1.00"
  public static String shrinkStringNumber(int num, int byFactor) {
    int numShift = 0;
    while (true) {
      int div = byFactor / 10;
      int rem = byFactor % 10;
      if (div == 0) {
        break;
      }
      if (rem != 0) {
        throw new RuntimeException("'byFactor' (" + byFactor + ") is not a power of 10");
      }
      byFactor = div;
      numShift++;
    }

    String shrinkedNum;
    StringBuilder strNum = new StringBuilder(String.valueOf(num));
    if (strNum.length() < numShift + 1) {
      strNum.insert(0, "0".repeat(numShift + 1 - strNum.length()));
    }
    strNum.insert(strNum.length() - 2, ".");
    return strNum.toString();
  }

  public static double[] calDoubleRoundingError(int num, int shrinkFactor) {
    double[] roundingErrs = new double[num];
    for (int i = 0; i < num; i++) {
      double fraction = shrinkFactor;
      double dVal = i / fraction;
      BigDecimal ddec = new BigDecimal(dVal, MathContext.UNLIMITED);

      String strDec = shrinkStringNumber(i, shrinkFactor);
      BigDecimal sdec = new BigDecimal(strDec, MathContext.UNLIMITED);

      BigDecimal errorDec = sdec.subtract(ddec);

      roundingErrs[i] = errorDec.doubleValue();
    }
    return roundingErrs;
  }

  public static double[] calFloatRoundingError(int num, int shrinkFactor) {
    double[] roundingErrs = new double[num];
    for (int i = 0; i < num; i++) {
      float fraction = shrinkFactor;
      float fVal = i / fraction;
      BigDecimal ddec = new BigDecimal(fVal, MathContext.UNLIMITED);

      String strDec = shrinkStringNumber(i, shrinkFactor);
      BigDecimal sdec = new BigDecimal(strDec, MathContext.UNLIMITED);

      BigDecimal errorDec = sdec.subtract(ddec);

      roundingErrs[i] = errorDec.doubleValue();
    }
    return roundingErrs;
  }

  public static void outputRoundingError(double[] values, double[] errors, String fileName) throws IOException {
    PrintWriter out = new PrintWriter(fileName, "UTF-8");
    IntStream.range(0, values.length).forEach(i -> {
      out.print(values[i]);
      out.print(" ");
      out.println(errors[i]);
    });
    out.flush();
  }

  public static void main (String[] args) throws Exception {
    int num = 10000;
    int shrinkFactor = 100;
    double[] values = new double[num];
    IntStream.range(0, num).forEach(i -> values[i] = i / (double) shrinkFactor);
    double[] doubleRoundingErrs = calDoubleRoundingError(num, shrinkFactor);
    double[] floatRoundingErrs = calFloatRoundingError(num, shrinkFactor);

    // Sample
    int sampleIntv = 30;
    double[] sampledValues = sampleArray(values, sampleIntv);
    double[] sampledDoubleRoundingErrs = sampleArray(doubleRoundingErrs, sampleIntv);
    double[] sampledFloatRoundingErrs = sampleArray(floatRoundingErrs, sampleIntv);

    outputRoundingError(sampledValues, sampledFloatRoundingErrs, "float-error.csv");
    outputRoundingError(sampledValues, sampledDoubleRoundingErrs, "double-error.csv");
  }
}
