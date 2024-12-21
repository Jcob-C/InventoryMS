import java.util.Scanner;

public class Utils {
    final static Scanner scnnr = new Scanner(System.in);



    static Integer parse(String string) {
        if (is_int(string)) {
            return Integer.parseInt(string);
        } 
        else {
            return null;
        }
    }



    static boolean is_int(String string) {
        try {
            Integer.parseInt(string);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }



    static int total_sales(String[] history, int day_range) {
        int
        total = 0,
        day = 0,
        days = history.length-1;

        for (day = days; day > days-day_range; day--) {
            if (history[day] != null && is_int(history[day])) {
                total += parse(history[day]);
            }
        }

        return total;
    }



    static Integer index_of(String target, String[][] table, int column) {
        int
        r = 0,
        rows = table.length;

        for (r = 0; r < rows; r++) {
            if (table[r][column].equals(target)) {
                return r;
            }
        }

        return null;
    }



    static String outputinput(String title, String input_context, String display) {
        String output = "";

        for (int i = 1; i <= 30; i++) {
            output += "\n";
        }

        output += Main.TITLE_BORDER + title + Main.TITLE_BORDER;

        if (display != null) {
            output += "\n\n" + display; 
        }
        output +=  "\n\n" + input_context + "\n\n" + "(no_spaces)\nType Here :";
        System.out.print(output);

        return scnnr.next();
    }



    static String menu_format(String[] options) {
        String formatted_options = "";
        for (int i = 0; i < options.length; i++) {
            if (!formatted_options.isEmpty()) {
                formatted_options += "\n";
            }
            formatted_options += (i+1) + Main.OPTION_GAP + options[i];
        }
        return formatted_options;
    }



    static String[][] sorted(String[][] table, Integer column, boolean asc, boolean nums) {
        if (column == null) {
            return table;
        }

        boolean 
        unsorted = true;
        int
        r,
        rows = table.length;
        String 
        new_table[][] = copy_of(table),
        holder[] = null;

        while (unsorted) {
            unsorted = false;

            for (r = 2; r < rows; r++) {
                if (
                asc && nums && parse(new_table[r-1][column]) > parse(new_table[r][column]) ||
                !asc && nums && parse(new_table[r-1][column]) < parse(new_table[r][column]) ||
                asc && !nums && new_table[r-1][column].compareToIgnoreCase(new_table[r][column]) > 0 ||
                !asc && !nums && new_table[r-1][column].compareToIgnoreCase(new_table[r][column]) < 0 
                ) {
                    unsorted = true;
                    holder = new_table[r];
                    new_table[r] = new_table[r-1];
                    new_table[r-1] = holder;
                }
            }
        }
        return new_table;
    }



    static String[][] filtered(String[][] table, Integer column, String filter) {
        if (column == null) {
            return table;
        }
        int 
        r, c,
        fr = 0, 
        frows = 0,
        rows = table.length,
        columns = table[0].length;

        for (r = 0; r < rows; r++) {
            if (r == 0 || table[r][column].contains(filter)) {
                frows++;
            }
        }

        String[][] new_table = new String[frows][columns];

        for (r = 0; r < rows; r++) {
            if (r == 0 || table[r][column].contains(filter)) {
                for (c = 0; c < columns; c++) {
                    new_table[fr][c] = table[r][c];
                }
                fr++;
            }
        }

        return new_table;
    }



    static String[][] copy_of(String[][] table) {
        int 
        r = 0, 
        c = 0,
        rows = table.length,
        columns = table[0].length;
        String[][] 
        copy = new String[rows][columns];

        for (r = 0; r < rows; r++) {
            for (c = 0; c < columns; c++) {
                copy[r][c] = table[r][c];
            }
        }
        
        return copy;
    }

    static String string_of(String[][] table) {
        String output = "";
        for (String[] x : table) {
            if (!output.isEmpty()) {
                output += "\n";
            }
            for (String y : x) {
                output += "[" + y + "]  ";
            }
        }
        return output;
    }
}