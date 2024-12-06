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

    static void products_menu() {
        String[] options = {
            "Back",
            "Record Sales",
            "Filter & Sort",
            "Manage"
        };
        switch(Main.get_choice("PRODUCTS", column_names[0]+products_string(), options)) {
            case "1": Users.main_menu(); break;
            case "2": record_sales_menu(); break;
            case "3":  break;
            case "4":  break;
            default: products_menu();
        }
    } 
    
    static void record_sales_menu() {
        String input_id, input_sales;
        int index;
        String[] options = {
            "Back",
            "Record Sales",
            "Refund/Undo Sales"
        };
        switch(Main.get_choice("RECORD SALES", column_names[0]+products_string(), options)) {
            case "1": 
                products_menu(); 
            break;

            case "2": 
                input_id = Main.get_input("RECORD SALES", "Enter Product's ID");
                input_sales = Main.get_input("RECORD SALES", "Enter New Sales");
                index = Dtbs.row_index_of(input_id, Dtbs.products, 0);
                if (index != -1 && Main.is_number(input_sales)) {
                    if (Integer.parseInt(Dtbs.products[index][3]) < Integer.parseInt(input_sales)) {
                        Main.show_message("Not Enough Stock");
                    } else {
                        Dtbs.sales[index][1] = String.valueOf(
                            Integer.parseInt(Dtbs.sales[index][1])
                            +Integer.parseInt(input_sales));
                        Dtbs.products[index][3] = String.valueOf(
                            Integer.parseInt(Dtbs.products[index][3])
                            -Integer.parseInt(input_sales));   
                        Main.show_message("Sales Record Added & Stock Reduced");
                    }
                } else {
                    Main.show_message("Invalid Inputs");
                }
                record_sales_menu();
            break;

            case "3": 
                input_id = Main.get_input("UNDO SALES", "Enter Product's ID");
                input_sales = Main.get_input("UNDO SALES", "Enter Refunded Sales");
                index = Dtbs.row_index_of(input_id, Dtbs.products, 0);
                if (index != -1 && Main.is_number(input_sales)) {
                    Dtbs.sales[index][1] = String.valueOf(
                        Integer.parseInt(Dtbs.sales[index][1])
                        -Integer.parseInt(input_sales));
                    Dtbs.products[index][3] = String.valueOf(
                        Integer.parseInt(Dtbs.products[index][3])
                        +Integer.parseInt(input_sales));  
                    Main.show_message("Sales Record Subtracted & Stock Added");
                } else {
                    Main.show_message("Invalid Inputs");
                }
                record_sales_menu();
            break;

            default: 
                record_sales_menu();
        }
    }

    static void filter_sort_menu() {
        String[] options = {
            "Back",
            "Filter",
            "Sort"
        };
        switch (Main.get_choice("FILTER/SORT", column_names[0]+products_string(), options)) {
            case "1":
                products_menu();
            break;

            case "2":
                String[] filter_options = {
                    "Back",
                    "Name",
                    "Type",
                    "Reset"
                };
                
            break;

            case "3":
                String[] sort_options = {
                    "Back",
                    "ID",
                    "Name",
                    "Type"
                };
                
            break;

            default:
                filter_sort_menu();
        }
    }

    static void reports_menu() {
        
    }

    static String products_string() {
        return Main.string_of(Main.sorted(Dtbs.products,sort_column,ascending,number_column),filter_column,filter_word);
    }

    static String sales_string(int column, boolean ascending) {
        return Main.string_of(Main.sorted(Dtbs.sales, column, ascending, true), null, null);
    }
}