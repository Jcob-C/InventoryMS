import java.time.ZonedDateTime; 
import java.time.ZoneId; 
import java.time.format.DateTimeFormatter; 

import java.util.Scanner;

class InventoryMS {
    final int // CONFIGURATION
    activity_log_limit = 20,
    signup_users_level = 0;

    final String // CUSTOMIZATION
    TITLE_BORDER = "_______", 
    OPTION_GAP = " <- ";
    final int
    PRINTS_GAP = 50;

    String[][] // TABLES (rows start at 1)
    accounts = {{"ID", "USERNAME", "PASSWORD", "LEVEL"}},
    activity_log = {{"DATE & TIME", "USERNAME", "ACTIVITY"}},
    // these 3 below are in sync
    products = {{"ID", "NAME", "TYPE", "STOCK"}},
    sales = {{"PRODUCT ID", "NAME", "TODAY", "7 DAYS", "30 DAYS"}},
    sales_history = new String[1][31]; // {PRODUCT ID, DAY 1-30}

    final DateTimeFormatter date_format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    final DateTimeFormatter time_format = DateTimeFormatter.ofPattern("HH:mm:ss");
    final ZoneId phtimeZ = ZoneId.of("Asia/Manila");
    
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
        daily_update();
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
        } else if (activity_log == table && activity_log.length >= activity_log_limit+1) {
            delete_from(activity_log, 1);
            insert_into(activity_log, row);
            return;
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
        if (index < 0 || table.length <= index) { 
            return; 
        }
        if (table == products) {
            delete_from(sales, index);
            delete_from(sales_history, index);
        }
        String[][] shorter_table = new String[table.length-1][table[0].length];
        int offset = 0;
        for (int i = 0; i < shorter_table.length; i++) {
            if (i == index) { offset = 1; }
            shorter_table[i] = table[i+offset];
        }
        overwrite(table, shorter_table);
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
        output +=  "\n\n" + context + "\n\n" + "(no_spaces!)\nType Here :";
        System.out.print(output);
        return SCANNER.next();
    } final Scanner SCANNER = new Scanner(System.in);

    // if sort column is null, it will not be sorted
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
        if (sort_column == null) { return table_copy; }
        boolean unsorted = true;
        String[] holder = null;
        while (unsorted) {
            unsorted = false;
            for (i = 2; i < table_copy.length; i++) {
                if (ascending && !num_column && table_copy[i-1][sort_column].compareToIgnoreCase(table_copy[i][sort_column]) > 0 ||
                !ascending && !num_column && table_copy[i-1][sort_column].compareToIgnoreCase(table_copy[i][sort_column]) < 0 ||
                ascending && num_column && prsInt(table_copy[i-1][sort_column]) > prsInt(table_copy[i][sort_column]) ||
                !ascending && num_column && prsInt(table_copy[i-1][sort_column]) < prsInt(table_copy[i][sort_column]) ) {
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

    // string_of() but only for a row
    String string_of(String[] row) {
        String output = "";
        for (int i = 0; i < row.length; i++) {
            output += "[" + row[i] + "]  ";
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
            prsInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // returns the index of row where inputs are found, null if not found (ignores first row)
    Integer index_of(String target, String[][] table, int column) {
        for(int i = 1; i < table.length; i++) {
            if (table[i][column].equalsIgnoreCase(target)) {
                return i;
            }
        }
        return null;
    }

    // checks date and updates date, sales, sales history
    void daily_update() {
        String current_date = get_datetime().format(date_format);
        if (saved_date == null) { 
            saved_date = current_date; 
        } else if(!current_date.equals(saved_date)) {
            saved_date = current_date;

        }
    }

    // logs parameter string as the action done
    void log_this(String action) {
        String[] row = {saved_date+" "+get_datetime().format(time_format), username_logged_in, action};
        insert_into(activity_log, row);
    }

    // closes scanner and stops the program
    void exit_program() {
        SCANNER.close();
        System.exit(0);
    }

    // shortened the string_of() of products applied with the saved sort and filters settings
    String string_products() {
        return string_of(sorted_filtered(products, saved_sort_column, saved_ascending, saved_num_column, saved_filter_column, saved_filter_word));
    }

    // shortened getting string_of() specific row with index, with column names at the top
    String string_row_of(int index, String[][] table) {
        return string_of(table[0])+"\n\n"+string_of(table[index]);
    }

    // shortened Integer.parseInt()
    Integer prsInt(String string) {
        return Integer.parseInt(string);
    }

    // shortened zonedatetime.now
    ZonedDateTime get_datetime() {
        return ZonedDateTime.now(phtimeZ);
    }





    /*
     *  ----------------------------\/---MENUS---\/--------------------------------------
     */






    void sign_in_menu() {
        Integer 
        name_index = null;
        String 
        menu_options[] = {"Exit", "Log In", "Sign Up", "Next Day (debug sales)"},
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
                } else if (!accounts[name_index][2].equals(pass_input)) {
                    output_input("LOG IN", "Incorrect Password", null);
                } else { 
                    username_logged_in = name_input;
                    level_logged_in = prsInt(accounts[name_index][3]);
                    log_this("Logged In");
                    main_menu(); 
                    return;   
                }
            break;
            case "3":
                name_input = output_input("SIGN UP", "Enter Username", null);
                pass_input = output_input("SIGN UP", "Enter Password", null);
                name_index = index_of(name_input, accounts, 1);
                if (name_index != null) {
                    output_input("SIGN UP", "Username Already Used", null);
                } else {    
                    insert_into(accounts, new String[]{"gen-id", name_input, pass_input, String.valueOf(signup_users_level)});
                    output_input("SIGN UP", "Account Created", null);
                } 
            break;
            case "4": saved_date = "n word";
        }
        sign_in_menu();
    }

    void main_menu() {
        String 
        options[] = {"Log Out", "Products", "Reports", "Users"},
        menu_input = output_input("MAIN MENU", menu_format(options), null);

        if (is_int(menu_input) && prsInt(menu_input)-1 > level_logged_in) {
            output_input("MAIN MENU", "Access Denied", null);
            main_menu();
            return;
        }
        switch(menu_input) {
            case "1": sign_in_menu(); break;
            case "2": products_menu(); break;
            case "3": reports_menu(); break;
            case "4": users_menu(); break;
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
        Integer 
        index = null;
        String 
        options[] = {"Back", "Sell", "Refund"},
        menu_input = output_input("RECORD SALES", menu_format(options), string_products()),
        id_input = null,
        sales_input = null;

        switch(menu_input) {
            case "1": products_menu(); return;
            case "2":
                id_input = output_input("SELL", "Enter Product ID", string_products());
                index = index_of(id_input, products, 0);
                if (index == null) { 
                    output_input("SELL", "Product ID Not Found", null);
                    break; 
                } 
                sales_input = output_input("SELL", "Enter Sales", string_row_of(index,products)); 
                if (!is_int(sales_input)) {
                    output_input("SELL", "Invalid Sales Input", null);
                } else if (prsInt(products[index][3]) < prsInt(sales_input)) {
                    output_input("SELL", "Not Enough Stock", null);
                } else {
                    sales[index][2] = String.valueOf(prsInt(sales[index][2]) + prsInt(sales_input));
                    products[index][3] = String.valueOf(prsInt(products[index][3]) - prsInt(sales_input));
                    output_input("SELL", "Sales Added & Stocks Subtracted", null);
                }
            break;
            case "3":
                id_input = output_input("REFUND", "Enter Product ID", string_products());
                index = index_of(id_input, products, 0);
                if (index == null) { 
                    output_input("SELL", "Product ID Not Found", null);
                    break; 
                } 
                sales_input = output_input("REFUND", "Enter Sales", string_row_of(index,products));
                if (!is_int(sales_input)) {
                    output_input("REFUND", "Invalid Inputs", null);
                } else {
                    sales[index][2] = String.valueOf(prsInt(sales[index][2]) - prsInt(sales_input));
                    products[index][3] = String.valueOf(prsInt(products[index][3]) + prsInt(sales_input));
                    output_input("SELL", "Sales Subtracted & Stocks Returned", null);
                }
        }
        recordsales_menu();
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
        menu_input = output_input("SORT", menu_format(options), string_products()),
        ascending_input = null;

        switch (menu_input) {
            case "1": sortfilter_menu(); return;
            case "2": case "3": case "4": 
                saved_sort_column = prsInt(menu_input)-2;
                saved_num_column = menu_input.equals("2");
                ascending_input = output_input("SORT", menu_format(options2), null);
                saved_ascending = !ascending_input.equals("2");
        }
        sort_menu();
    }

    void filter_menu() {
        String
        options[] = {"Back", "ID", "Name", "Type", "Remove Filter"},
        input = output_input("FILTER", menu_format(options), string_products());

        switch(input) {
            case "1": sortfilter_menu(); return;
            case "2": case "3": case "4":
                saved_filter_column = prsInt(input) - 2;
                saved_filter_word = output_input("FILTER", "Enter Search Word", null);
            break;
            case "5":
                saved_filter_column = null;
                saved_filter_word = null;
        }
        filter_menu();
    }

    void manage_menu() {
        Integer index = null;
        String
        options[] = {"Back", "Add", "Delete", "Edit"},
        menu_input = output_input("MANAGE", menu_format(options), string_products()),
        name_input, type_input, stock_input, id_input;

        switch (menu_input) {
            case "1": products_menu(); return;
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
            break;
            case "4": edit_menu(); return;
        }
        manage_menu();
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
                target_column = prsInt(column_input) - 1;
            break;
            default: edit_menu(); return;
        }
        target_index = index_of(output_input("EDIT", "Enter Product ID", string_products()), products, 0);
        if (target_index != null) {
            new_value = output_input("EDIT", "Enter New "+products[0][target_column], string_row_of(target_index, products));
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
                int int_menu_input = prsInt(menu_input);
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

    void users_menu() {
        output_input("ACTIVITY LOG", string_of(sorted_filtered(activity_log,null,true,false,null,null)), null);
        main_menu();
    }
}