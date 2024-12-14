import java.util.Scanner;

class InventoryMS {
    final int // CONFIGURATION
    activity_log_limit = 50,
    signup_users_level = 1;

    final String // CUSTOMIZATION
    TITLE_BORDER = "______", 
    OPTION_GAP = "-<", 
    PRINTS_GAP = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";

    String[][] // TABLES (rows start at 1)
    accounts = {{"ID", "USERNAME", "PASSWORD", "LEVEL"}},
    products = {{"ID", "NAME", "TYPE", "STOCK"}},
    sales = {{"PRODUCT ID", "TODAY", "7 DAYS", "30 DAYS"}},
    sales_history = new String[1][31], // {PRODUCT ID, DAY 1-30}
    activity_log = {{"DATE & TIME", "USERNAME", "ACTIVITY"}};

    // sort settings
    Integer
    sort_column = 0;
    boolean
    ascending = true, num_column = true;
    // search settings
    Integer
    search_column = null;
    String
    search_word = null;

    // cached values
    int
    next_user_id = 1,
    next_product_id = 1,
    level_logged_in = 0;
    String
    saved_date = null,
    username_logged_in = null;

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
        switch(menu_input) {
            case "1": sign_in_menu(); break;
            case "2": products_menu(); break;
            case "3": break;
            case "4": break;
            default: main_menu();
        }
    }

    void products_menu() {
        String 
        options[] = {"Back", "Record Sales", "Sort/Filter", "Manage"},
        menu_input = output_input("PRODUCTS", menu_format(options), products_with_settings());
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
        menu_input = output_input("SORT & FILTER", menu_format(options), products_with_settings());
        switch (menu_input) {
            case "1": products_menu(); break;
            case "2": break;
            case "3": break;
            default: sortfilter_menu();
        }
    }

    void manage_menu() {
        Integer index = null;
        String
        options[] = {"Back", "Add", "Delete", "Edit"},
        menu_input = output_input("MANAGE", menu_format(options), products_with_settings()),
        name_input, type_input, stock_input, id_input;
        switch (menu_input) {
            case "1": products_menu(); break;
            case "2":
                name_input = output_input("ADD PRODUCT", "Enter Product Name", products_with_settings());
                type_input = output_input("ADD PRODUCT", "Enter Product Type", products_with_settings());
                stock_input = output_input("ADD PRODUCT", "Enter Product Amount", products_with_settings());
                if (null != index_of(name_input, products, 1)) {
                    output_input("ADD PRODUCT", "Product Name Already Used", null); 
                } else if (!is_int(stock_input)) {
                    output_input("ADD PRODUCT", "Invalid Amount Input", products_with_settings()); 
                } else {
                    insert_into(products, new String[]{"gen-id",name_input,type_input,stock_input});
                    output_input("ADD PRODUCT", "New Product Added", products_with_settings()); 
                }
                manage_menu();
            break;
            case "3":
                id_input = output_input("DELETE PRODUCT", "Enter Product ID", products_with_settings());
                index = index_of(id_input, products, 0);
                if (null != index) {
                    delete_from(products, index);
                    output_input("DELETE PRODUCT", "Product Deleted", products_with_settings()); 
                } else {
                    output_input("DELETE PRODUCT", "Product ID Not Found", products_with_settings()); 
                }
                manage_menu();
            break;
            default: manage_menu();
        }
    }
    
    void reports_menu() {
        String
        options[] = {"Back", "Top Sales", "Low Stock"},
        menu_input = output_input("REPORTS", menu_format(options), null);
        switch (menu_input){
            case "1": main_menu(); break;
            case "2": break;
            case "3": break;
        }
    }
    
    void sales_menu(){
        
    }
    
    void stock_menu(){
        
    }

    void insert_into(String[][] table, String[] row) {
        if (row[0].equals("gen-id")) {
            if (accounts == table) {
                row[0] = String.valueOf(next_user_id++);
            } else if (products == table) {
                row[0] = String.valueOf(next_product_id++);
                insert_into(sales, new String[]{row[0],"0","0","0"});
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

    void delete_from(String[][] table, int index) {

    }

    void overwrite(String[][] table, String[][] new_table) {
        if (accounts == table) { accounts = new_table; }
        else if (products == table) { products = new_table; }
        else if (sales == table) { sales = new_table; }
        else if (sales_history == table) { sales_history = new_table; }
        else if (activity_log == table) { activity_log = new_table; }
    }
    
    String output_input(String title, String context, String display) {
        String output = PRINTS_GAP + TITLE_BORDER + title + TITLE_BORDER;
        if (display != null) { output += "\n\n" + display; }
        output +=  "\n\n" + context + "\n\n" + "Type Here :";
        System.out.print(output);
        return SCANNER.next();
    } 
    final Scanner SCANNER = new Scanner(System.in);
    
    String products_with_settings() {
        return string_of((sorted(products, sort_column, ascending, num_column)), search_column, search_word);
    }

    String[][] sorted(String[][] table, Integer column, boolean asc, boolean sorting_nums) {
        int i, ii;
        String[][] table_copy = new String[table.length][table[0].length];
        for (i = 0; i < table.length; i++) {
            for (ii = 0; ii <table[i].length; ii++) {
                table_copy[i][ii] = table[i][ii];
            }
        }
        boolean unsorted = true;
        String holder = null;
        while (unsorted) {
            unsorted = false;
            for (i = 2; i < table.length; i++) {
                if (asc && !sorting_nums && table_copy[i-1][column].compareToIgnoreCase(table_copy[i][column]) > 0 ||
                !asc && !sorting_nums && table_copy[i-1][column].compareToIgnoreCase(table_copy[i][column]) < 0 ||
                asc && sorting_nums && Integer.parseInt(table_copy[i-1][column]) > Integer.parseInt(table_copy[i][column]) ||
                !asc && sorting_nums && Integer.parseInt(table_copy[i-1][column]) < Integer.parseInt(table_copy[i][column]) ) {
                    unsorted = true;
                    holder = table_copy[i][column];
                    table_copy[i][column] = table_copy[i-1][column];
                    table_copy[i-1][column] = holder;
                }
            }
        }
        return table_copy;
    }

    String string_of(String[][] table, Integer column, String search) {
        String output = "";
        int x, y;
        for (x = 0; x < table.length; x++) {
            if (x > 0 && search != null && !table[x][column].equals(search)) { continue; }
            if (!output.isEmpty()) { output += "\n"; }
            for (y = 0;  y < table[x].length; y++) {
                output += "[" + table[x][y] + "]  ";
            }
        } 
        return output;
    }

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

    boolean is_int(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    Integer index_of(String target, String[][] table, int column) {
        for(int i = 0; i < table.length; i++) {
            if (table[i][column].equalsIgnoreCase(target)) {
                return i;
            }
        }
        return null;
    }

    void exit_program() {
        SCANNER.close();
        System.exit(0);
    }

    InventoryMS() { 
        insert_into(accounts, new String[]{"gen-id","admin","pass","3"});
        insert_into(products, new String[]{"gen-id","product1","type1","20"});
        insert_into(products, new String[]{"gen-id","product2","type1","8"});
        insert_into(products, new String[]{"gen-id","product3","type2","14"});
        sign_in_menu(); 
    }
    public static void main(String[] args) { new InventoryMS(); }
}