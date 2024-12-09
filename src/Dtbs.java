public class Dtbs {
    final static int activity_log_limit = 50;

    static int next_user_id = 1;
    static String[][] accounts = new String[][]{{"ID", "USERNAME", "PASSWORD", "LEVEL"}};
    static String[][] activity_log = new String[][]{{"DATE & TIME", "USERNAME", "ACTIVITY"}};

    static int next_product_id = 1;
    static String[][] products = new String[][]{{"ID", "NAME", "TYPE", "STOCK"}};
    static String[][] sales = new String[][]{{"ID", "TODAY", "LAST 7 DAYS", "LAST 30 DAYS"}};
    static String[][] sales_history = new String[1][31]; // { ID, DAY 1-30 }

    static String saved_date = null;

    // put "id" in inputs for a generated id
    static void add_to(String[][] table, String[] inputs) {
        int i, ii;
        String[][] new_table = new String[table.length+1][table[0].length];
        if (table[0][0] == null) {
            for (i = 0; i < inputs.length; i++) {
                table[0][i] = inputs[i];
            }
            return;
        }
        if (inputs[0].equals("id")) {
            if (table == accounts) { 
                inputs[0] = String.valueOf(next_user_id++); 
            }
            else if (table == products) { 
                inputs[0] = String.valueOf(next_product_id++);
                add_to(sales,new String[]{inputs[0],"0","0","0"}); 
                add_to(sales_history,new String[]{inputs[0]}); 
            }
        }
        for (i = 0; i < table.length; i++) {
            for (ii = 0; ii < table[0].length; ii++) {
                new_table[i][ii] = table[i][ii];
            }
        }
        for (i = 0; i < inputs.length; i++) {
            new_table[table.length][i] = inputs[i];
        }
        overwrite(table, new_table);
    }

    static void remove_from(String[][] table, String id) {
        String[][] new_table = new String[table.length-1][table[0].length];
        int offset = 0;
        if (table == products) {
            remove_from(sales, id);
            remove_from(sales_history, id);
        }
        for (int i = 0; i < new_table.length; i++) {
            if (table[i][0] != null && table[i][0].equals(id) && offset == 0) { 
                offset++; 
            }
            new_table[i] = table[i+offset];
        }
        overwrite(table, new_table);
    }

    static void overwrite(String[][] old_table, String[][] new_table) {
        if (old_table == accounts) { accounts = new_table; } 
        else if (old_table == products) { products = new_table; }
        else if (old_table == sales) { sales = new_table; }
        else if (old_table == sales_history) { sales_history = new_table; }
        else if (old_table == activity_log) { activity_log = new_table; }
    }

    static int row_index_of(String target, String[][] table, int column) {
        for (int i = 0; i < table.length; i++) {
            if (table[i][column] != null && table[i][column].equals(target)) {
                return i;
            }
        }
        return -1;
    }
}