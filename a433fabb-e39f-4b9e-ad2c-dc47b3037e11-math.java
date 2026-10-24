// BigDecimal and BigInteger preserve exact decimal values while simplifying ratios.
// Scanner reads the two values from the console.

// Runs the two-number calculator and prints its results.
// ANSI escape codes reset formatting and color the console output.
private static final String RESET = "\u001B[0m";
private static final String PURPLE = "\u001B[35m";
private static final String MAGENTA = "\u001B[35m";
// Groups thousands with commas while retaining decimal precision.
private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0.#################");
// Displays division to two decimal places, rounding toward positive infinity.
private static final DecimalFormat DIVISION_FORMAT = new DecimalFormat("#,##0.00");

// Reads both operands, validates them, and displays each operation.
void main() {
    // Automatically closes the console scanner when the program ends.
    try (Scanner scanner = new Scanner(System.in)) {
        // These are the user instructions
        System.out.println(PURPLE + "User Instructions: Please type a number for x then hit enter,\n"
                + "Next type a number for y, then hit enter to see all four operations,\n"
                + "Plus ratio answers." + RESET);

        // Stop if the first input is missing or invalid.
        Double x = readNumber(scanner, "Enter x: ");
        if (x == null) {
            return;
        }

        // Stop if the second input is missing or invalid.
        Double y = readNumber(scanner, "Enter y: ");
        if (y == null) {
            return;
        }

        // Display each operation, its equation, and its formatted result.
        System.out.println();
        System.out.printf("%-16s | %-22s | %s%n", "Problem", "Format", "Result");
        printResultRow("Addition", formatNumber(x) + " + " + formatNumber(y), formatNumber(x + y));
        printResultRow("Subtraction", formatNumber(x) + " - " + formatNumber(y), formatNumber(x - y));
        printResultRow("Multiplication", formatNumber(x) + " * " + formatNumber(y), formatNumber(x * y));

        // Division and ratios are undefined when the second value is zero.
        if (y == 0.0) {
            printResultRow("Division", formatNumber(x) + " / " + formatNumber(y), "Cannot divide by zero");
            String reason = x == 0.0
                    ? "Undefined (both values are zero)"
                    : "Undefined (the second value cannot be zero)";
            printResultRow("Reduced ratio", formatNumber(x) + ":" + formatNumber(y), reason);
        } else {
            // Round the quotient upward to the nearest hundredth.
            BigDecimal quotient = BigDecimal.valueOf(x)
                    .divide(BigDecimal.valueOf(y), 2, RoundingMode.CEILING);
            printResultRow("Division", formatNumber(x) + " / " + formatNumber(y),
                    DIVISION_FORMAT.format(quotient));
            printResultRow("Reduced ratio", formatNumber(x) + ":" + formatNumber(y), simplifyRatio(x, y));
        }
        System.out.println(PURPLE + "Note: Division is rounded up to two decimal places (.00)." + RESET);
        System.out.println(PURPLE + "PEMDAS applies to combined expressions; each operation above is calculated separately." + RESET);
    }
}

// Prints one row with the problem name, equation format, and calculated result.
private static void printResultRow(String problem, String format, String result) {
    System.out.printf(PURPLE + "%-16s | %-22s | %s%n" + RESET, problem, format, result);
}

// Formats a number with comma-separated thousands and its meaningful decimals.
private static String formatNumber(double number) {
    return NUMBER_FORMAT.format(BigDecimal.valueOf(number).stripTrailingZeros());
}

// Scales decimal operands to integers, then divides both ratio parts by their GCF.
private static String simplifyRatio(double x, double y) {
    // Remove insignificant trailing zeros before determining decimal precision.
    BigDecimal xDecimal = BigDecimal.valueOf(x).stripTrailingZeros();
    BigDecimal yDecimal = BigDecimal.valueOf(y).stripTrailingZeros();
    int scale = Math.max(0, Math.max(xDecimal.scale(), yDecimal.scale()));

    // Scale both values equally so their ratio can be reduced with integer arithmetic.
    BigInteger xInteger = xDecimal.movePointRight(scale).toBigIntegerExact();
    BigInteger yInteger = yDecimal.movePointRight(scale).toBigIntegerExact();
    BigInteger gcf = xInteger.abs().gcd(yInteger.abs());

    // A zero GCF means both ratio parts are zero, so the ratio cannot be reduced.
    if (gcf.signum() == 0) {
        return "Undefined (both values are zero)";
    }
    return NUMBER_FORMAT.format(xInteger.divide(gcf))
            + ":" + NUMBER_FORMAT.format(yInteger.divide(gcf));
}

// Prompts for a finite number and returns null after reporting invalid input.
private static Double readNumber(Scanner scanner, String prompt) {
    System.out.print(prompt);
    // Reject input that Scanner cannot parse as a number.
    if (!scanner.hasNextDouble()) {
        System.out.println(MAGENTA + "Error: please enter a valid number." + RESET);
        return null;
    }

    // Reject NaN and infinity even though Scanner can parse them.
    double number = scanner.nextDouble();
    if (!Double.isFinite(number)) {
        System.out.println(MAGENTA + "Error: please enter a finite number." + RESET);
        return null;
    }
    return number;
}
