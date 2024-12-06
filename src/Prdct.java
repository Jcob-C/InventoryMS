public class Prdct {
    final static String[] column_names = {
        "[ID]  [NAME]  [TYPE]  [STOCK] \n",
        "[ID]  [NAME]  [TODAY]  [7 DAYS]  [30 DAYS] \n"
    };

    // sort settings
    static int sort_column = 0; 
    static boolean number_column = false, ascending = true;

    // filter settings
    static Integer filter_column = null; 
    static String filter_word = null;

    static void products_menu(String title) {
        String[] options = {
            "Back",
            "Record Sales",
            "Filter & Sort",
            "Manage"
        };
        switch(Main.get_choice(title.toUpperCase(), column_names[0]+products_string(), options)) {
            case "1": Users.main_menu(); break;
            case "2": Users.main_menu(); break;
            case "3": Users.main_menu(); break;
            case "4": Users.main_menu(); break;
            default: products_menu(title);
        }
    }

    static void reports_menu(String title) {
        
    }

    static String products_string() {
        return Main.string_of(Main.sorted(Dtbs.products,sort_column,ascending,number_column),filter_column,filter_word);
    }

    static String sales_string(int column, boolean ascending) {
        return Main.string_of(Main.sorted(Dtbs.sales, column, ascending, true), null, null);
    }
}