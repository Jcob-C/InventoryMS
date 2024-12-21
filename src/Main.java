class Main {
    final static String // customize
    TITLE_BORDER = "_______", 
    OPTION_GAP = " <- ";

    // sort & filter settings
    static Integer 
    sort_column = null,
    filter_column = null;
    static boolean 
    asc = true, 
    nums = true;
    static String 
    filter = null;

    // cached
    static String 
    loggedin_type = "nigg1",
    loggedin_name = "nigg2";

    public static void main(String[] args) { 
        Dbase.load_all();
        sign_in_menu();
    }

    static String presetProducts() {
        String[][] holder = Utils.copy_of(Dbase.products);     
        if (sort_column != null) {
            holder = Utils.sorted(holder, sort_column, asc, nums);
        }
        if (filter_column != null) {
            holder = Utils.filtered(holder, filter_column, filter);
        }
        return Utils.string_of(holder);
    }

    static void sign_in_menu() {
        Dbase.daily_update();

        Integer 
        name_index = null;
        String 
        name_input, pass_input,
        menu_options[] = {"Exit", "Log In", "Sign Up", "Simulate Next Day"};

        switch (Utils.outputinput("SIGN IN", Utils.menu_format(menu_options), null)) {
            case "1": 
            Dbase.save_all();
            System.exit(0); 
            break;

            case "2":
            name_input = Utils.outputinput("LOG IN", "Enter Username", null);
            pass_input = Utils.outputinput("LOG IN", "Enter Password", null);
            name_index = Utils.index_of(name_input, Dbase.accounts, 1);

            if (name_index == null) {
                Utils.outputinput("LOG IN", "Invalid Username", null);
            } 
            else if (!Dbase.accounts[name_index][2].equals(pass_input)) {
                Utils.outputinput("LOG IN", "Incorrect Password", null);
            } 
            else if (Dbase.accounts[name_index][3].equals("Pending")) {
                Utils.outputinput("LOG IN", "Account Is Not Yet Admitted", null);
            } 
            else { 
                loggedin_type = Dbase.accounts[name_index][3];
                loggedin_name = name_input;
                Dbase.log_activity(name_input, "Logged In");
                main_menu(); 
                return;   
            }
            break;

            case "3":
            name_input = Utils.outputinput("SIGN UP", "Enter Username", null);
            pass_input = Utils.outputinput("SIGN UP", "Enter Password", null);
            name_index = Utils.index_of(name_input, Dbase.accounts, 1);

            if (name_index != null) {
                Utils.outputinput("SIGN UP", "Username Already Used", null);
            } 
            else {    
                String[] new_account = {"gen-id", name_input, pass_input, "Pending"};
                Dbase.inserted_into(Dbase.accounts, new_account);
                Utils.outputinput("SIGN UP", "Account Creation Requested", null);
            } 
            break;

            case "4": 
                Dbase.saved_date = "nigg"; 
            break;
        }
        sign_in_menu();
    }

    static void main_menu() {
        switch (loggedin_type) {
            case "Admin": admins_menu(); return;
            case "Manager": managers_menu(); return;
            case "Clerk": clerks_menu(); return;
        }
    }

    static void admins_menu() {
        String menu_options[] = {"Log Out", "Sales", "Inventory", "Users"};

        switch (Utils.outputinput("MAIN MENU", Utils.menu_format(menu_options), null)) {
            case "1":
            Dbase.log_activity(loggedin_name, "Logged Out");
            sign_in_menu();
            return;
            case "2": sales_menu(); return;
            case "3": inventory_menu(); return;
            case "4": users_menu(); return;
        }
        admins_menu();
    }

    static void managers_menu() {
        String menu_options[] = {"Log Out", "Sales", "Inventory", "View Activity Log"};

        switch (Utils.outputinput("MAIN MENU", Utils.menu_format(menu_options), null)) {
            case "1":
            Dbase.log_activity(loggedin_name, "Logged Out");
            sign_in_menu();
            return;
            case "2": sales_menu(); return;
            case "3": inventory_menu(); return;
            case "4": viewActLog_menu(); return;
        }
        managers_menu();
    }
    
    static void clerks_menu() {
        String menu_options[] = {"Log Out", "Sales", "View Inventory"};

        switch (Utils.outputinput("MAIN MENU", Utils.menu_format(menu_options), null)) {
            case "1":
            Dbase.log_activity(loggedin_name, "Logged Out");
            sign_in_menu();
            return;
            case "2": sales_menu(); return;
            case "3": viewInventory_menu(); return;
        }
        clerks_menu();
    }
    
    static void sales_menu() {
        Integer sales_index;
        String id, sales,
        menu_options[] = {"Back", "View Sales", "Make Sales (Sold)", "Undo Sales (Refunded)"};

        switch (Utils.outputinput("SALES MENU", Utils.menu_format(menu_options), null)) {
            case "1": main_menu(); return;
            case "2": viewSales_menu(); return;

            case "3":
            id = Utils.outputinput("MAKE SALES", "Enter Product's ID", null);
            sales = Utils.outputinput("MAKE SALES", "Enter New Sales", null);
            sales_index = Utils.index_of(id, Dbase.sales, 0);

            if (sales_index != null) {
                Dbase.sales[sales_index][2] = String.valueOf(
                    Utils.parse(Dbase.sales[sales_index][2]) + Utils.parse(sales));
                Dbase.products[sales_index][3] = String.valueOf(
                    Utils.parse(Dbase.products[sales_index][3]) - Utils.parse(sales));  
                Utils.outputinput("MAKE SALES", "Sales Recorded & Stock Subtracted", null);
                Dbase.log_activity(loggedin_name, "Sold " + sales + " " + Dbase.products[sales_index][1]);
            }
            else {
                Utils.outputinput("MAKE SALES", "Product ID Not Found", null);
            }
            break;

            case "4":
            id = Utils.outputinput("UNDO SALES", "Enter Product's ID", null);
            sales = Utils.outputinput("UNDO SALES", "Enter Refunded Sales", null);
            sales_index = Utils.index_of(id, Dbase.sales, 0);

            if (sales_index != null) {
                Dbase.sales[sales_index][2] = String.valueOf(
                    Utils.parse(Dbase.sales[sales_index][2]) - Utils.parse(sales));
                Dbase.products[sales_index][3] = String.valueOf(
                    Utils.parse(Dbase.products[sales_index][3]) + Utils.parse(sales));  
                Utils.outputinput("UNDO SALES", "Sales Subtracted & Stock Added", null);
                Dbase.log_activity(loggedin_name, "Refunded " + sales + " " + Dbase.products[sales_index][1]);
            }
            else {
                Utils.outputinput("UNDO SALES", "Product ID Not Found", null);
            }
            break;
        }
        sales_menu();
    }

    static void viewSales_menu() {
        String 
        menu_options[] = {"Back", "Today", "7 Days", "30 Days"},
        menu_input = Utils.outputinput("VIEW SALES", Utils.menu_format(menu_options), null);

        switch (menu_input) {
            case "1": sales_menu(); return;
            case "2": case "3": case "4":
            Utils.outputinput("VIEW SALES", Utils.string_of(Utils.sorted(Dbase.sales, Utils.parse(menu_input), false, true)), null);
            break;
        }
        viewSales_menu();
    }

    static void inventory_menu() {
        Integer index;
        String id, 
        menu_options[] = {"Back", "View Inventory", "Edit Product", "Add Product", "Delete Product"};

        switch (Utils.outputinput("INVENTORY MENU", Utils.menu_format(menu_options), null)) {
            case "1": main_menu(); return;
            case "2": viewInventory_menu(); break;

            case "3":
            menu_options = new String[]{"Edit Name", "Edit Type", "Edit Stock"};
            id = Utils.outputinput("EDIT PRODUCT", "Enter Product ID", null);
            index = Utils.index_of(id, Dbase.products, 0);
            String column = Utils.outputinput("EDIT PRODUCT", Utils.menu_format(menu_options), null);
            Integer int_column = Utils.parse(column);

            if (index != null && int_column != null && int_column >= 1 && int_column <= 3) {
                String new_value = Utils.outputinput("EDIT PRODUCT", "Enter New " + Dbase.products[0][int_column], null);
                
                if (int_column == 3 && !Utils.is_int(new_value)) {
                    Utils.outputinput("EDIT PRODUCT", "Invalid Stock Input", null);
                }
                else {
                    Dbase.log_activity(loggedin_name, "Edited " + Dbase.products[index][1] + "'s " + Dbase.products[0][int_column]);
                    Dbase.products[index][int_column] = new_value;
                    Utils.outputinput("EDIT PRODUCT", Dbase.products[0][int_column] + " Edited", null);
                }
            } else {
                Utils.outputinput("EDIT PRODUCT", "Invalid Input", null);
            }
            break;

            case "4":
            String
            name = Utils.outputinput("ADD PRODUCT", "Enter Product Name", null),
            type = Utils.outputinput("ADD PRODUCT", "Enter Product Type", null),
            stock = Utils.outputinput("ADD PRODUCT", "Enter Product Stock", null);

            if (Utils.index_of(name,Dbase.products,1) != null) {
                Utils.outputinput("ADD PRODUCT", "Product Name Already Used", null);
            }
            else if (!Utils.is_int(stock)) {
                Utils.outputinput("ADD PRODUCT", "Invalid Stock Input", null);
            }
            else {  
                Dbase.add_product(new String[] {String.valueOf(Dbase.next_product_id++), name, type, stock});
                Utils.outputinput("ADD PRODUCT", "New Product Added", null);
                Dbase.log_activity(loggedin_name, "Added Product: " + name);
            }
            break;

            case "5":
            id = Utils.outputinput("DELETE PRODUCT", "Enter Product ID", null);
            index = Utils.index_of(id, Dbase.products, 0);
            if (index != null) {
                Utils.outputinput("DELETE PRODUCT", Dbase.products[index][1] + " Deleted", null);
                Dbase.log_activity(loggedin_name, "Deleted Product: " + Dbase.products[index][1]);
                Dbase.remove_product(index);
            }
            else {
                Utils.outputinput("DELETE PRODUCT", "Product ID Not Found", null);
            }
            break;
        }
        inventory_menu();
    }

    static void viewInventory_menu() {
        Integer
        old_sort_column = sort_column;
        String 
        menu_options[] = {"Back", "Filter", "Clear Filter", "Sort (ID)", "Sort (Name)", "Sort (Type)", "Sort (Stock)"},
        menu_input = Utils.outputinput("VIEW INVENTORY", Utils.menu_format(menu_options), presetProducts());

        switch (menu_input) {
            case "1": 
            switch (loggedin_type) {
                case "Clerk": clerks_menu(); return;
                default: inventory_menu(); return;
            }

            case "2":
            menu_options = new String[]{"ID", "Name", "Type"};
            menu_input = Utils.outputinput("VIEW INVENTORY", Utils.menu_format(menu_options), null);

            switch (menu_input) {
                case "1": case "2": case "3":
                filter_column = Utils.parse(menu_input)-1;
                filter = Utils.outputinput("VIEW INVENTORY", "Enter Filter Word", null);
                break;
                default:
                Utils.outputinput("VIEW INVENTORY", "Invalid Input", null);
                break;
            }
            break;

            case "3": 
            filter_column = null; 
            break;

            case "4": case "5": case "6": case "7":
            nums = menu_input == "4" || menu_input == "7";
            sort_column = Utils.parse(menu_input)-4;
            menu_input = Utils.outputinput(
                "VIEW INVENTORY", Utils.menu_format(new String[]{"Ascending","Descending"}), null);

            switch (menu_input) {
                case "1": asc = true; break;
                case "2":  asc = false; break;
                default: 
                Utils.outputinput("VIEW INVENTORY", "Invalid Input", null);
                sort_column = old_sort_column; 
                break;
            }
            break;
        }
        viewInventory_menu();
    }

    static void users_menu() {
        Integer index;
        String id,
        menu_options[] = {"Back", "View Users", "View Activity Log", "Edit Account", "Create Account", "Delete Account"};
        
        switch (Utils.outputinput("USERS MENU", Utils.menu_format(menu_options), null)) {
            case "1": main_menu(); return;
            case "2": viewUsers_menu(); return;
            case "3": viewActLog_menu(); return;

            case "4":
                
            break;

            case "5":
            
            break;

            case "6":
            
            break;
        }
        users_menu();
    }

    static void viewUsers_menu() {
        switch (Utils.outputinput("USERS MENU", Utils.menu_format(new String[]{"Back"}), Utils.string_of(Dbase.accounts))) {
            case "1": users_menu(); return;
            default: viewUsers_menu(); return;
        }
    }

    static void viewActLog_menu() {
        switch (Utils.outputinput("USERS MENU", Utils.menu_format(new String[]{"Back"}), Utils.string_of(Dbase.activity_log))) {
            case "1": 
            switch (loggedin_type) {
                case "Manager": managers_menu(); return;
                default: users_menu(); return;
            }
            default: viewActLog_menu(); return;
        }
    }
}