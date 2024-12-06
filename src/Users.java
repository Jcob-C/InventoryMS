public class Users {
    final static String[] column_names = {
        "[ID]  [USERNAME]  [PASSWORD]  [ROLE LEVEL] \n",
        "[DATE & TIME]  [USERNAME]  [ACTION] \n"
    };

    static String current_user = null;
    static int current_level = 0;

    static void sign_in_menu() {
        String input_name, input_pass;
        String[] options = {
            "Exit", 
            "Log In", 
            "Sign Up"
        };
        switch (Main.get_choice(Main.MAIN_TITLE,null,options)) {
            case "1":
                Main.exit_program();
            break;

            case "2":
                input_name = Main.get_input(
                    options[1].toUpperCase(), 
                    "Enter Username"
                );
                input_pass = Main.get_input(
                    options[1].toUpperCase(), 
                    "Enter Password"
                );
                int name_index = Dtbs.row_index_of(input_name, Dtbs.accounts, 1);
                int pass_index = Dtbs.row_index_of(input_pass, Dtbs.accounts, 2);
                if (name_index != -1 && pass_index != -1) {
                    current_user = input_name;
                    current_level = Integer.parseInt(Dtbs.accounts[name_index][3]);
                    main_menu();
                } else {
                    Main.show_message("Invalid Account");
                    sign_in_menu();
                }
            break;
            
            case "3":
                input_name = Main.get_input(
                    options[1].toUpperCase(), 
                    "Enter Username"
                );
                input_pass = Main.get_input(
                    options[1].toUpperCase(), 
                    "Enter Password"
                );
                if (Dtbs.row_index_of(input_name, Dtbs.accounts, 1) == -1) {
                    Dtbs.add_to(Dtbs.accounts, new String[]{"id",input_name,input_pass,"0"});
                    Main.show_message("Account Created");
                } else {
                    Main.show_message("Username Already Used");
                }
                sign_in_menu();
            break;

            default:
                sign_in_menu();
        }
    }

    static void main_menu() {
        String[] options = {
            "Log Out", 
            "Products",
            "Reports",
            "Users"
        };
        String menu_input = Main.get_choice(Main.MAIN_TITLE, null, options);
        if (menu_input.equals("1")) {
            sign_in_menu();
        } else if (menu_input.equals("2") && current_level >= 1) { 
            Prdct.products_menu();  
        } else if (menu_input.equals("3") && current_level >= 2) { 
            Prdct.reports_menu();
        } else if (menu_input.equals("4") && current_level == 3) { 
           users_menu(options[3]);
        } else {
            main_menu();
        }
    }

    static void users_menu(String title) {

    }
}