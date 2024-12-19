class Main {
    
    final static String // CUSTOMIZATION
    TITLE_BORDER = "_______", 
    OPTION_GAP = " <- ";

    // sort settings
    Integer sort_column = null;
    boolean 
    asc = true, 
    nums = true;

    // filter settings
    Integer filter_column = null;
    String filter = null;

    int loggedin_level = 0;

    public static void main(String[] args) { 
        new Main().sign_in_menu();
    }

    void exit_program() {
        Dbase.save_all();
        System.exit(0);
    }

    void sign_in_menu() {
        Dbase.load_all();
        Dbase.daily_update();

        Integer 
        name_index = null;
        String 
        menu_options[] = {"Exit", "Log In", "Sign Up", "Next Day (debug)"},
        menu_input = Utils.outputinput("SIGN IN", Utils.menu_format(menu_options), null), 
        name_input, pass_input;

        switch (menu_input) {
            case "1": exit_program(); break;
            case "2":
                name_input = Utils.outputinput("LOG IN", "Enter Username", null);
                pass_input = Utils.outputinput("LOG IN", "Enter Password", null);
                name_index = Utils.index_of(name_input, Dbase.accounts, 1);

                if ( name_index == null ) {
                    Utils.outputinput("LOG IN", "Invalid Username", null);
                } 
                else if ( !Dbase.accounts[name_index][2].equals(pass_input) ) {
                    Utils.outputinput("LOG IN", "Incorrect Password", null);
                } 
                else { 
                    loggedin_level = Utils.parse(Dbase.accounts[name_index][3]);
                    Dbase.log_activity(name_input, "Logged In");
                    main_menu(); 
                    return;   
                }
            break;
            case "3":
                name_input = Utils.outputinput("SIGN UP", "Enter Username", null);
                pass_input = Utils.outputinput("SIGN UP", "Enter Password", null);
                name_index = Utils.index_of(name_input, Dbase.accounts, 1);

                if ( name_index != null ) {
                    Utils.outputinput("SIGN UP", "Username Already Used", null);
                } 
                else {    
                    String[] new_account = {"gen-id", name_input, pass_input, "0"};
                    Dbase.inserted_into(Dbase.accounts, new_account);
                    Utils.outputinput("SIGN UP", "Account Created", null);
                } 
            break;
            case "4": Dbase.saved_date = "n word";
        }
        sign_in_menu();
    }

    void main_menu() {
        String 
        options[] = {"Log Out", "Products", "Reports",  "Users"},
        menu_input = output_input("MAIN MENU", menu_format(options), null);

        if ( is_int(menu_input) && prsInt(menu_input)-1 > level_logged_in ) {
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
                
                if ( index == null ) { 
                    output_input("SELL", "Product ID Not Found", null);
                    break; 
                } 

                sales_input = output_input("SELL", "Enter Sales", string_row_of(index,products));

                if ( !is_int(sales_input) ) {
                    output_input("SELL", "Invalid Sales Input", null);
                } 
                else if ( prsInt(products[index][3]) < prsInt(sales_input) ) {
                    output_input("SELL", "Not Enough Stock", null);
                } 
                else {
                    sales[index][2] = String.valueOf(prsInt(sales[index][2]) + prsInt(sales_input));
                    products[index][3] = String.valueOf(prsInt(products[index][3]) - prsInt(sales_input));
                    output_input("SELL", "Sales Added & Stocks Subtracted", null);
                    log_this("Sold "+sales_input+" "+products[index][1]);
                }
            break;
            case "3":
                id_input = output_input("REFUND", "Enter Product ID", string_products());
                index = index_of(id_input, products, 0);

                if ( index == null ) { 
                    output_input("SELL", "Product ID Not Found", null);
                    break; 
                } 

                sales_input = output_input("REFUND", "Enter Sales", string_row_of(index,products));

                if ( !is_int(sales_input) ) {
                    output_input("REFUND", "Invalid Inputs", null);
                } 
                else {
                    sales[index][2] = String.valueOf(prsInt(sales[index][2]) - prsInt(sales_input));
                    products[index][3] = String.valueOf(prsInt(products[index][3]) + prsInt(sales_input));
                    output_input("SELL", "Sales Subtracted & Stocks Returned", null);
                    log_this("Refunded "+sales_input+" "+products[index][1]);
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
        Integer 
        index = null;
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

                if ( null != index_of(name_input, products, 1) ) {
                    output_input("ADD PRODUCT", "Product Name Already Used", null); 
                }
                else if ( !is_int(stock_input) ) {
                    output_input("ADD PRODUCT", "Invalid Amount Input", string_products()); 
                } 
                else {
                    insert_into(products, new String[]{"gen-id",name_input,type_input,stock_input});
                    output_input("ADD PRODUCT", "New Product Added", string_products()); 
                    log_this("Added Product: "+name_input);
                }
            break;
            case "3":
                id_input = output_input("DELETE PRODUCT", "Enter Product ID", string_products());
                index = index_of(id_input, products, 0);

                if ( null != index ) {
                    log_this("Deleted Product: "+products[index][1]);
                    delete_from(products, index);
                    output_input("DELETE PRODUCT", "Product Deleted", string_products()); 
                } 
                else {
                    output_input("DELETE PRODUCT", "Product ID Not Found", string_products()); 
                }
            break;
            case "4": edit_menu(); return;
        }
        manage_menu();
    }
    
    void edit_menu() {
        Integer 
        target_index, 
        target_column;
        String
        options[] = {"Back", "Name", "Type", "Stock"},
        column_input = output_input("EDIT", menu_format(options), string_products()),
        input_id, new_value = null;

        switch(column_input) {
            case "1": manage_menu(); return;
            case "2": case "3": case "4": 
                target_column = prsInt(column_input) - 1;
            break;
            default: edit_menu(); return;
        }

        input_id = output_input("EDIT", "Enter Product ID", string_products());
        target_index = index_of(input_id, products, 0);

        if ( target_index != null ) {
            new_value = output_input(
                "EDIT", 
                "Enter New " + products[0][target_column], 
                string_row_of(target_index, products)
            );

            if ( target_column == 3 && !is_int(new_value) ) {
                output_input("EDIT", "Invalid Stock Input", null);
            } 
            else if ( target_column == 1 && null != index_of(new_value, products, 1) ) {
                output_input("EDIT", "Product Name Already Used", null);    
            } 
            else {
                log_this("Edited Product: "+products[target_index][1]+"'s "+products[0][target_column]+" to "+new_value);
                products[target_index][target_column] = new_value;
                output_input("EDIT", "Product Edited", null);
            }
        } 
        else {
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
        output_input(
            "LOW STOCK", 
            string_of(sorted_filtered(products,3,true,true,null,null)), 
            null
        );
        reports_menu();
    }

    void users_menu() {
        String 
        options[] = {"Back", "Activity Log", "User Manager"},
        menu_input = output_input("USERS", menu_format(options), null);

        switch (menu_input) {
            case "1": main_menu(); break;
            case "2": activity_menu(); break;
            case "3": usermanage_menu(); break;
            default: users_menu();
        }
    }

    void usermanage_menu() {
        Integer 
        index = null;
        String
        options[] = {"Back", "Create", "Delete", "Edit"},
        menu_input = output_input("USERS MANAGER", menu_format(options), string_of(accounts)),
        name_input, pass_input, level_input, id_input;

        switch (menu_input) {
            case "1": users_menu(); return;
            case "2":
                name_input = output_input("CREATE ACCOUNT", "Enter Account Username", string_of(accounts));

                if ( null != index_of(name_input, accounts, 1) ) {
                    output_input("CREATE ACCOUNT", "Username Already Used", null); 
                    break;
                }
                pass_input = output_input("CREATE ACCOUNT", "Enter Account Password", null);
                level_input = output_input("CREATE ACCOUNT", "Enter Account Access Level (0-3)", null);
                
                if ( !is_int(level_input) || prsInt(level_input) > 3 || prsInt(level_input) < 0 ) {
                    output_input("CREATE ACCOUNT", "Invalid Access Level", null); 
                } 
                else {
                    insert_into(accounts, new String[]{"gen-id",name_input,pass_input,level_input});
                    output_input("CREATE ACCOUNT", "New Account Created", null); 
                    log_this("Created Account: "+name_input);
                }
            break;
            case "3":
                id_input = output_input("DELETE ACCOUNT", "Enter Account ID", string_of(accounts));
                index = index_of(id_input, accounts, 0);

                if ( null != index ) {
                    log_this("Deleted Account: "+accounts[index][1]);
                    delete_from(accounts, index);
                    output_input("DELETE ACCOUNT", "Account Deleted", null); 
                } 
                else {
                    output_input("DELETE ACCOUNT", "Account ID Not Found", null); 
                }
            break;
            case "4": editaccount_menu(); return;
        }
        usermanage_menu();
    }

    void editaccount_menu() {
        Integer 
        target_index, 
        target_column;
        String
        options[] = {"Back", "Username", "Password", "Access Level"},
        column_input = output_input("EDIT ACCOUNT", menu_format(options), string_of(accounts)),
        input_id, new_value = null;

        switch(column_input) {
            case "1": usermanage_menu(); return;
            case "2": case "3": case "4": 
                target_column = prsInt(column_input) - 1;
            break;
            default: editaccount_menu(); return;
        }

        input_id = output_input("EDIT ACCOUNT", "Enter Account ID", string_of(accounts));
        target_index = index_of(input_id, accounts, 0);

        if ( target_index != null ) {
            new_value = output_input(
                "EDIT ACCOUNT", 
                "Enter New " + accounts[0][target_column], 
                string_row_of(target_index, accounts)
            );

            if ( target_column == 3 && !is_int(new_value) ) {
                output_input("EDIT ACCOUNT", "Invalid Acces Level Input", null);
            } 
            else if ( target_column == 1 && null != index_of(new_value, accounts, 1) ) {
                output_input("EDIT ACCOUNT", "Username Already Used", null);    
            } 
            else {
                log_this("Edited "+accounts[target_index][1]+"'s "+accounts[0][target_column]+" to "+new_value);
                accounts[target_index][target_column] = new_value;
                output_input("EDIT ACCOUNT", "Account Edited", null);
            }
        } 
        else {
            output_input("EDIT ACCOUNT", "Account ID Not Found", null);
        }
        editaccount_menu();
    }

    void activity_menu() {
        output_input(
            "ACTIVITY LOG", 
            string_of(sorted_filtered(activity_log,null,true,false,null,null)), 
            null
        );
        users_menu();
    }
}