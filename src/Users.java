public class Users {
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
                int name_index = Dtbs.row_index_of(input_name, Dtbs.accounts, 1);

                input_pass = Main.get_input(
                    options[1].toUpperCase(), 
                    "Enter Password"
                );
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
                    sign_in_menu();
                } else {
                    Main.show_message("Username Already Used");
                    sign_in_menu();
                }
            break;

            default:
                sign_in_menu();
        }
    }

    static void main_menu() {
        String[] options = {
            "Log Out", 
            "Record Sales", 
            "Inventory",
            "Reports",
            "Users"
        };
        String menu_input = Main.get_choice(Main.MAIN_TITLE, null, options);
        switch (menu_input) {
            case "1":
                sign_in_menu();
            break;

            case "2":
                
            break;

            case "3":
            break;

            case "4":
            break;

            case "5":
            break;

            default:
        }
    }
}