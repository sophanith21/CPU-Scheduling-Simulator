import java.util.Scanner;

public class validateInput {
    // Handle numProcess input
    public static int validatePositiveInt(Scanner scan) {
        int value;
        while (true) {
            if (scan.hasNextInt()) {
                value = scan.nextInt();
                if (value > 0) {
                    scan.nextLine(); // Consume newline after valid input
                    return value;
                }
            } else {
                System.out.println("Invalid data type.");
                scan.next(); // Discard invalid input
            }
            System.out.print("Enter a valid positive integer: ");
        }
    }

    // Handle time input 
    public static int validateNonNegativeInt(Scanner scan) {
        int value;
        while (true) {
            if (scan.hasNextInt()) {
                value = scan.nextInt();
                if (value >= 0) {
                    scan.nextLine(); // Consume newline after valid input
                    return value;
                }
            } else {
                System.out.println("Input is not a number");
                scan.next(); // Discard invalid input
            }
            System.out.print("Enter a valid non-negative integer: ");
        }
    }
}
