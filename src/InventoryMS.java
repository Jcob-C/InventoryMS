import java.util.Scanner;

class InventoryMS {
    final int // CONFIGURATION
    activity_log_limit = 50,
    signup_users_level = 1;

    final String // CUSTOMIZATION
    TITLE_BORDER = "______", 
    OPTION_GAP = "-<", 
    PRINTS_GAP = "\n\n\n\n\n";

    String[][] // TABLES (rows start at 1)
    accounts = {{"ID", "USERNAME", "PASSWORD", "LEVEL"}},
    products = {{"ID", "NAME", "TYPE", "STOCK"}},
    sales = {{"PRODUCT ID", "TODAY", "7 DAYS", "30 DAYS"}},
    sales_history = new String[1][31], // {PRODUCT ID, DAY 1-30}
    activity_log = {{"DATE & TIME", "USERNAME", "ACTIVITY"}};

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
        menu_input = output_input("SIGN IN", menu_format(menu_options)), 
        name_input, pass_input;

        switch (menu_input) {
            case "1": exit_program(); break;
            case "2":
                name_input = output_input("LOG IN", "Enter Username");
                pass_input = output_input("LOG IN", "Enter Password");
                name_index = index_of(name_input, accounts, 1);
                if (name_index != null && accounts[name_index][2].equals(pass_input)) {
                    main_menu();
                } else {    
                    output_input("LOG IN", "Invalid Account");
                    sign_in_menu();
                }
            break;
            case "3":
                name_input = output_input("SIGN UP", "Enter Username");
                pass_input = output_input("SIGN UP", "Enter Password");
                name_index = index_of(name_input, accounts, 1);
                if (name_index == null) {
                    insert_into(accounts, new String[]{"gen-id", name_input, pass_input, String.valueOf(signup_users_level)});
                    output_input("SIGN UP", "Account Created");
                } else {    
                    output_input("SIGN UP", "Username Already Used");
                }
                sign_in_menu();
            break;
            default: sign_in_menu();
        }
    }

    void main_menu() {
        
    }

    void insert_into(String[][] table, String[] row) {
        if (row[0].equals("gen-id")) {
            if (accounts == table) {
                row[0] = String.valueOf(next_user_id++);
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
    }
    
    String output_input(String title, String display){
        System.out.print( PRINTS_GAP +
            TITLE_BORDER + title + TITLE_BORDER + 
            "\n\n" +
            display + 
            "\n\n" +
            "Type Here :"
        );
        return SCANNER.next();
    } 
    final Scanner SCANNER = new Scanner(System.in);

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
            if (table[i][column].equals(target)) {
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
        sign_in_menu(); 
    }
    public static void main(String[] args) { new InventoryMS(); }
}