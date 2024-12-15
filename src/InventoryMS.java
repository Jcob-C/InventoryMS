// import date util
// import file writer / reader
import java.util.Scanner;

class InventoryMS {
    final int // CONFIGURATION
    activity_log_limit = 50,
    signup_users_level = 0;

    final String // CUSTOMIZATION
    TITLE_BORDER = "_______", 
    OPTION_GAP = " <- ";
    final int
    PRINTS_GAP = 50;

    String[][] // TABLES (rows start at 1)
    accounts = {{"ID", "USERNAME", "PASSWORD", "LEVEL"}},
    products = {{"ID", "NAME", "TYPE", "STOCK"}},
    sales = {{"PRODUCT ID", "NAME", "TODAY", "7 DAYS", "30 DAYS"}},
    sales_history = new String[1][31], // {PRODUCT ID, DAY 1-30}
    activity_log = {{"DATE & TIME", "USERNAME", "ACTIVITY"}};

    // sort settings
    Integer
    saved_sort_column = 0;
    boolean
    saved_ascending = true, saved_num_column = true;
    // filter settings
    Integer
    saved_filter_column = null;
    String
    saved_filter_word = null;

    // cached values
    int
    next_user_id = 1,
    next_product_id = 1,
    level_logged_in = 0;
    String
    saved_date = null,
    username_logged_in = null;

    // start of project
    public static void main(String[] args) { 
        new InventoryMS(); 
    }

    // start of class
    InventoryMS() { 
        insert_into(accounts, new String[]{"gen-id","admin","pass","3"});
        insert_into(products, new String[]{"gen-id","product1","type1","20"});
        insert_into(products, new String[]{"gen-id","product2","type1","8"});
        insert_into(products, new String[]{"gen-id","product3","type2","14"});
        sign_in_menu(); 
    }

    // copies table into longer with new row
    void insert_into(String[][] table, String[] row) {
        if (row[0].equals("gen-id")) {
            if (accounts == table) {
                row[0] = String.valueOf(next_user_id++);
            } else if (products == table) {
                row[0] = String.valueOf(next_product_id++);
                insert_into(sales, new String[]{row[0],row[1],"0","0","0"});
                insert_into(sales_history, new String[]{row[0]});
            }
        }
        String[][] expanded_table = new String[table.length+1][table[0].length];
        for (int j = 0; j < table.length; j++) {
            for (int i = 0; i < table[0].length; i++) {
                expanded_table[j][i] = table[j][i];
            }
        }
        for (int i = 0; i < row.length; i++) {
            expanded_table[table.length][i] = row[i];
        }
        overwrite(table, expanded_table);
    }

    // copies table into shorter, without the row of index
    void delete_from(String[][] table, int index) {

    }

    // replaces an initialized table
    void overwrite(String[][] table, String[][] new_table) {
        if (accounts == table) { accounts = new_table; }
        else if (products == table) { products = new_table; }
        else if (sales == table) { sales = new_table; }
        else if (sales_history == table) { sales_history = new_table; }
        else if (activity_log == table) { activity_log = new_table; }
    }
    
    // returns the user input after printing the (title, context for user input, additional displayed text)
    String output_input(String title, String context, String display) {
        String output = "";
        for (int i = 1; i <= PRINTS_GAP; i++) {
            output += "\n";
        }
        output += TITLE_BORDER + title + TITLE_BORDER;
        if (display != null) { output += "\n\n" + display; }
        output +=  "\n\n" + context + "\n\n" + "Type Here :";
        System.out.print(output);
        return SCANNER.next();
    } final Scanner SCANNER = new Scanner(System.in);
    
    // returns the string_of() of products applied with the saved sort and filters settings
    String string_products() {
        return string_of(sorted_filtered(products, saved_sort_column, saved_ascending, saved_num_column, saved_filter_column, saved_filter_word));
    }

    // returns a copy of the table, sorted or filtered based on the parameters (first row is ignored)
    String[][] sorted_filtered(String[][] table, Integer sort_column, boolean ascending, boolean num_column, Integer filter_column, String filter_word) {
        int i, ii, filtered = 0;
        for(i = 0; i < table.length; i++) {
            if (filter_column != null && filter_word != null && table[i][filter_column].equalsIgnoreCase(filter_word) ||
            filter_column == null && filter_word == null || i == 0) {
                filtered++;
            }
        }
        String[][] table_copy = new String[filtered][table[0].length];
        for (i = 0; i < table_copy.length; i++) {
            if (filter_column != null && filter_word != null && table[i][filter_column].equalsIgnoreCase(filter_word) ||
            filter_column == null && filter_word == null || i == 0) {
                for (ii = 0; ii < table_copy[i].length; ii++) {
                    table_copy[i][ii] = table[i][ii];
                }
            }
        }
        boolean unsorted = true;
        String[] holder = null;
        while (unsorted) {
            unsorted = false;
            for (i = 2; i < table_copy.length; i++) {
                if (ascending && !num_column && table_copy[i-1][sort_column].compareToIgnoreCase(table_copy[i][sort_column]) > 0 ||
                !ascending && !num_column && table_copy[i-1][sort_column].compareToIgnoreCase(table_copy[i][sort_column]) < 0 ||
                ascending && num_column && Integer.parseInt(table_copy[i-1][sort_column]) > Integer.parseInt(table_copy[i][sort_column]) ||
                !ascending && num_column && Integer.parseInt(table_copy[i-1][sort_column]) < Integer.parseInt(table_copy[i][sort_column]) ) {
                    unsorted = true;
                    holder = table_copy[i];
                    table_copy[i] = table_copy[i-1];
                    table_copy[i-1] = holder;
                }
            }
        }
        return table_copy;
    }

    // returns the input table as string styled like a table
    String string_of(String[][] table) {
        String output = "";
        int x, y;
        for (x = 0; x < table.length; x++) {
            if (!output.isEmpty()) { 
                output += "\n"; 
            }
            if (x == 1) {
                output += "\n";
            }
            for (y = 0;  y < table[x].length; y++) {
                output += "[" + table[x][y] + "]  ";
            }
        } 
        return output;
    }

    // returns the input array of options as a string styled like a list
    String menu_format(String[] options) {
        String formatted_options = "";
        for (int i = 0; i < options.length; i++) {
            if ( !formatted_options.isEmpty() ) {
                formatted_options += "\n";
            }
            formatted_options += (i+1) + OPTION_GAP + options[i];
        }
        return formatted_options;
    }

    // returns true if string input can be a number, false if no
    boolean is_int(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // returns the index of row where inputs are found, null if not found
    Integer index_of(String target, String[][] table, int column) {
        for(int i = 0; i < table.length; i++) {
            if (table[i][column].equalsIgnoreCase(target)) {
                return i;
            }
        }
        return null;
    }

    // closes scanner and stops the program
    void exit_program() {
        SCANNER.close();
        System.exit(0);
    }





    /*
     *  ----------------------------\/-MENUS-\/--------------------------------------
     */





    void sign_in_menu() {
        Integer name_index = null;
        String 
        menu_options[] = {"Exit", "Log In", "Sign Up"},
        menu_input = output_input("SIGN IN", menu_format(menu_options), null), 
        name_input, pass_input;

        switch (menu_input) {
            case "1": exit_program(); break;
            case "2":
                name_input = output_input("LOG IN", "Enter Username", null);
                pass_input = output_input("LOG IN", "Enter Password", null);
                name_index = index_of(name_input, accounts, 1);
                if (name_index == null) {
                    output_input("LOG IN", "Invalid Username", null);
                    sign_in_menu();
                } else if (!accounts[name_index][2].equals(pass_input)) {
                    output_input("LOG IN", "Incorrect Password", null);
                    sign_in_menu();
                } else { 
                    username_logged_in = name_input;
                    level_logged_in = Integer.parseInt(accounts[name_index][3]);
                    main_menu();   
                }
            break;
            case "3":
                name_input = output_input("SIGN UP", "Enter Username", null);
                pass_input = output_input("SIGN UP", "Enter Password", null);
                name_index = index_of(name_input, accounts, 1);
                if (name_index == null) {
                    insert_into(accounts, new String[]{"gen-id", name_input, pass_input, String.valueOf(signup_users_level)});
                    output_input("SIGN UP", "Account Created", null);
                } else {    
                    output_input("SIGN UP", "Username Already Used", null);
                }
                sign_in_menu();
            break;
            default: sign_in_menu();
        }
    }

    void main_menu() {
        String 
        options[] = {"Log Out", "Products", "Reports", "Users"},
        menu_input = output_input("MAIN MENU", menu_format(options), null);
        if (is_int(menu_input) && Integer.parseInt(menu_input)-1 > level_logged_in) {
            output_input("MAIN MENU", "Access Denied", null);
            main_menu();
            return;
        }
        switch(menu_input) {
            case "1": sign_in_menu(); break;
            case "2": products_menu(); break;
            case "3": reports_menu(); break;
            case "4": break;
            default: main_menu();
        }
    }

    void products_menu() {
        String 
        options[] = {"Back", "Record Sales", "Sort/Filter", "Manage"},
        menu_input = output_input("PRODUCTS", menu_format(options), string_products());
        switch(menu_input) {
            case "1": main_menu(); break;
            case "2": recordsales_menu(); break;
            case "3": sortfilter_menu(); break;
            case "4": manage_menu(); break;
            default: products_menu();
        }
    }

    void recordsales_menu() {

    }

    void sortfilter_menu() {
        String
        options[] = {"Back", "Sort", "Filter", "Reset All"},
        menu_input = output_input("SORT & FILTER", menu_format(options), string_products());
        switch (menu_input) {
            case "1": products_menu(); break;
            case "2": sort_menu(); break;
            case "3": filter_menu(); break;
            case "4":
                saved_sort_column = 0;
                saved_num_column = true;
                saved_ascending = true;
                saved_filter_column = null;
                saved_filter_word = null;
                products_menu();
            break;
            default: sortfilter_menu();
        }
    }

    void sort_menu() {
        String
        options[] = {"Back", "By ID", "By Name", "By Type"},
        options2[] = {"Ascending", "Descending"},
        input = output_input("SORT", menu_format(options), string_products());
        switch (input) {
            case "1": sortfilter_menu(); break;
            case "2": case "3": case "4": 
                saved_sort_column = Integer.parseInt(input)-2;
                saved_num_column = input.equals("2");
                input = output_input("SORT", menu_format(options2), null);
                saved_ascending = !input.equals("2");
                sort_menu();
            break;
            default: sort_menu();
        }
    }

    void filter_menu() {
        String
        options[] = {"Back", "ID", "Name", "Type", "Remove Filter"},
        input = output_input("FILTER", menu_format(options), string_products());
        switch(input) {
            case "1": sortfilter_menu(); break;
            case "2": case "3": case "4":
                saved_filter_column = Integer.parseInt(input) - 2;
                saved_filter_word = output_input("FILTER", "Enter Search Word", null);
                filter_menu();
            break;
            case "5":
                saved_filter_column = null;
                saved_filter_word = null;
                filter_menu();
            break;
            default: filter_menu();
        }
    }

    void manage_menu() {
        Integer index = null;
        String
        options[] = {"Back", "Add", "Delete", "Edit"},
        menu_input = output_input("MANAGE", menu_format(options), string_products()),
        name_input, type_input, stock_input, id_input;
        switch (menu_input) {
            case "1": products_menu(); break;
            case "2":
                name_input = output_input("ADD PRODUCT", "Enter Product Name", string_products());
                type_input = output_input("ADD PRODUCT", "Enter Product Type", string_products());
                stock_input = output_input("ADD PRODUCT", "Enter Product Amount", string_products());
                if (null != index_of(name_input, products, 1)) {
                    output_input("ADD PRODUCT", "Product Name Already Used", null); 
                } else if (!is_int(stock_input)) {
                    output_input("ADD PRODUCT", "Invalid Amount Input", string_products()); 
                } else {
                    insert_into(products, new String[]{"gen-id",name_input,type_input,stock_input});
                    output_input("ADD PRODUCT", "New Product Added", string_products()); 
                }
                manage_menu();
            break;
            case "3":
                id_input = output_input("DELETE PRODUCT", "Enter Product ID", string_products());
                index = index_of(id_input, products, 0);
                if (null != index) {
                    delete_from(products, index);
                    output_input("DELETE PRODUCT", "Product Deleted", string_products()); 
                } else {
                    output_input("DELETE PRODUCT", "Product ID Not Found", string_products()); 
                }
                manage_menu();
            break;
            case "4":
                edit_menu();
            break;
            default: manage_menu();
        }
    }
    
    void edit_menu() {
        Integer target_index, target_column;
        String
        options[] = {"Back", "Name", "Type", "Stock"},
        column_input = output_input("EDIT", menu_format(options), string_products()),
        new_value = null;
        switch(column_input) {
            case "1": manage_menu(); return;
            case "2": case "3": case "4":
                target_column = Integer.parseInt(column_input) - 1;
            break;
            default: edit_menu(); return;
        }
        target_index = index_of(output_input("EDIT", "Enter Product ID", string_products()), products, 0);
        if (target_index != null) {
            new_value = output_input("EDIT", "Enter New "+products[0][target_column], string_products());
            if (target_column == 3 && !is_int(new_value)) {
                output_input("EDIT", "Invalid Stock Input", null);
            } else if (target_column == 1 && null != index_of(new_value, products, 1)) {
                output_input("EDIT", "Product Name Already Used", null);                
            } else {
                products[target_index][target_column] = new_value;
                output_input("EDIT", "Product Edited", null);
            }
        } else {
            output_input("EDIT", "Product ID Not Found", null);
        }
        edit_menu();
    }
    
    void reports_menu() {
        String
        options[] = {"Back", "Top Sales", "Low Stock"},
        menu_input = output_input("REPORTS", menu_format(options), null);
        switch (menu_input){
            case "1": main_menu(); break;
            case "2": sales_menu(); break;
            case "3": stock_menu(); break;
            default: reports_menu();
        }
    }
    
    void sales_menu(){
        String
        options[] = {"Back", "Today", "7 Days", "30 Days"},
        menu_input = output_input("TOP SALES", menu_format(options), null);
        switch (menu_input){
            case "1": reports_menu(); break;
            case "2": case "3": case "4":
                int int_menu_input = Integer.parseInt(menu_input);
                output_input(
                    "TOP SALES " + sales[0][int_menu_input],
                    string_of(sorted_filtered(sales,int_menu_input,false,true,null,null)),
                    null
                );
                sales_menu();
            break;
            default: sales_menu();
        }
    }
    
    void stock_menu(){
        output_input("LOW STOCK", string_of(sorted_filtered(products,3,true,true,null,null)), null);
        reports_menu();
    }
}