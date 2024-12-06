public class Dtbs {
    final static int activity_log_limit = 50;

    static String[][] activity_log = new String[activity_log_limit][3]; // date&time, username, activity
    static int next_user_id = 1;
    static String[][] accounts = new String[1][4]; // id, username, password, role level
    static int next_product_id = 1;
    static String[][] products = new String[1][4]; // id, name, type, stock
    static String[][] sales = new String[1][4]; // id, today, week, month
    static String[][] sales_history = new String[1][31]; // id, 1-30
    
    static String saved_date = null;

    // if table has an id column, put "id" for a generated id
    static void add_to(String[][] table, String[] inputs) {
        int i, ii;
        String[][] new_table = new String[table.length+1][table[0].length];
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
        if (table[0][0] == null) {
            for (i = 0; i < inputs.length; i++) {
                table[0][i] = inputs[i];
            }
            return;
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

    }

    static void overwrite(String[][] old_table, String[][] new_table) {
        if (old_table == accounts) { accounts = new_table; } 
        else if (old_table == products) { products = new_table; }
        else if (old_table == sales) { sales = new_table; }
        else if (old_table == sales_history) { sales_history = new_table; }
    }

    static int row_index_of(String target, String[][] table, int column) {
        for (int i = 0; i < table.length; i++) {
            if (table[i][column] != null && 
                table[i][column].equals(target)) {
                    return i;
            }
        }
        return -1;
    }
}