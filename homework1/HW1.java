public class HW1 {
    public static void main(String[] args) {
        System.out.println(String.format("Square: %d", new HW1().square(-100)));
        System.out.println(String.format("SquareR: %d", new HW1().squareR(-100)));
    }

    private int square(int n) {
        int result = 0;
        for (int i = 1; i <= n; i++) {
            result += 2 * i - 1;
        }
        return result;
    }

    // Precondition n >= 0
    private int squareR(int n) {
        if (n <= 0) {
            return 0;
        }
        return squareR(n-1) + (2 * n - 1);
    }
    // Postconditiion:
}