import java.util.Scanner;

public class Main {
    final static String MAIN_TITLE = "INVENTORY MANAGEMENT SYSTEM";

    final static String SCREENS_PRINT_GAP = "\n\n\n\n\n";
    final static String TITLE_FRAME = "}______{";
    final static String OPTION_CONNECTOR = "-<";
    final static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Dtbs.add_to(Dtbs.accounts, new String[]{"id","admin","pass","4"});
        Dtbs.add_to(Dtbs.products, new String[]{"id","Apple","Fruit","8"});
        Dtbs.add_to(Dtbs.products, new String[]{"id","Zebra","Hayop","1"});
        Users.sign_in_menu();
    }

    static void exit_program() {
        scanner.close();
        System.exit(0);
    }

    static String string_of(String[][] table, Integer filter_column, String filter) {
        String output = "";
        for (String[] row : table) {
            if (filter_column == null ||
            filter_column != null && row[filter_column].equalsIgnoreCase(filter)) {
                output += "\n";
                for (String x : row) {
                    output += "[" + x + "]  ";
                }
            }
        }
        output += "\n";
        return output;
    }

    static String[][] sorted(String[][] table, int column, boolean ascending, boolean number_column) {
        int i, ii;
        String[][] copy_of_table = new String[table.length][table[0].length];
        for (i = 0; i < table.length; i++) {
            for (ii = 0; ii < table[i].length; ii++) {
                copy_of_table[i][ii] = table[i][ii];
            }
        }
        boolean unsorted = true;
        String[] holder;
        while(unsorted) {
            unsorted = false;
            for (i = 0; i < table.length-1; i++) {
                if (ascending && !number_column && copy_of_table[i][column].compareToIgnoreCase(copy_of_table[i+1][column]) > 0 ||
                !ascending && !number_column && copy_of_table[i][column].compareToIgnoreCase(copy_of_table[i+1][column]) < 0 ||
                ascending && number_column && Integer.parseInt(copy_of_table[i][column]) > Integer.parseInt(copy_of_table[i+1][column]) ||
                !ascending && number_column && Integer.parseInt(copy_of_table[i][column]) < Integer.parseInt(copy_of_table[i+1][column])) {
                    unsorted = true;
                    holder = copy_of_table[i];
                    copy_of_table[i] = copy_of_table[i+1];
                    copy_of_table[i+1] = holder;
                }
            }
        }
        return copy_of_table;
    }

    static String get_choice(String title, String message, String[] options) {
        String screen = TITLE_FRAME + title + TITLE_FRAME + "\n";
        if (message != null) { 
            screen += "\n" + message; 
        }
        for (int i = 0; i < options.length; i++) {
            screen += "\n" + (i+1) + OPTION_CONNECTOR + options[i];
        }
        screen += "\n\nType Here :";
        print(screen);
        return scanner.next();
    }

    static String get_input(String title, String message) {
        print(
            TITLE_FRAME + title + TITLE_FRAME + 
            "\n\n" +
            message + 
            "\n\n" +
            "Type Here :"
        );
        return scanner.next();
    }

    static void show_message(String message) {
        print(
            TITLE_FRAME + "MESSAGE" + TITLE_FRAME + 
            "\n\n" +
            message + 
            "\n\n" +
            "1" + OPTION_CONNECTOR + "OK" + 
            "\n\n" +
            "Type Here :"
        );
        scanner.next();
    }

    static boolean is_number(String string) {
        try {
            Float.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static void print(String screen) {
        System.out.print(SCREENS_PRINT_GAP + screen);
    }
}