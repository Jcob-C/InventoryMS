import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

import java.time.format.DateTimeFormatter; 
import java.time.ZonedDateTime; 
import java.time.ZoneId;

public class Dbase {
    final static String TXTFILE_SEPARATOR = "/";
    final static int ACTIVITY_LOG_LIMIT = 50;
    static final String[] account_types = {"Pending", "Clerk", "Manager", "Admin"};

    final static ZoneId phtZone = ZoneId.of("Asia/Manila");
    final static DateTimeFormatter date_f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    final static DateTimeFormatter time_f = DateTimeFormatter.ofPattern("HH:mm:ss");
    final static String txtfolder = System.getProperty("user.dir")+"\\lib\\txts\\";

    static String
    saved_date = null;
    static int 
    next_user_id = 1,
    next_product_id = 1;

    // tables' row indexes start at 1 (row 0 is for column names)
    static String[][] 
    accounts = {{"ID", "USERNAME", "PASSWORD", "TYPE"}},
    activity_log = {{"DATE & TIME", "USERNAME", "ACTIVITY"}},
    products = {{"ID", "NAME", "TYPE", "STOCK"}},
    sales = {{"PRODUCT ID", "NAME", "TODAY", "7 DAYS", "30 DAYS"}},
    sales_history = new String[1][31]; // {PRODUCT ID, DAY 1-30}
    

    
    static String[][] inserted_into(String[][] table, String[] row) {
        int 
        r = 0, 
        c = 0, 
        old_rows = table.length, 
        columns = table[0].length;
        String[][]
        new_table = new String[old_rows+1][columns];

        for (r = 0; r < old_rows; r++) {
            for (c = 0; c < columns; c++) {
                new_table[r][c] = table[r][c]; 
            }
        }
        for (c = 0; c < row.length; c++) {
            new_table[old_rows][c] = row[c];
        }

        return new_table;
    }



    static String[][] removed_from(String[][] table, int row_index) {
        if (row_index <= 0 || table.length <= row_index) {
            return null;
        }
        
        int 
        r = 0,
        c = 0,
        offset = 0,
        new_rows = table.length-1,
        columns =  table[0].length;
        String[][] 
        new_table = new String[new_rows][columns];
        
        for (r = 0; r < new_rows; r++) {
            if (r >= row_index) {
                offset = 1; 
            }
            for (c = 0; c < columns; c++) {
                new_table[r][c] = table[r+offset][c];
            } 
        }

        return new_table;
    }



    static void add_product(String[] row) {
        String[] 
        sales_row = new String[] {row[0], row[1], "0", "0", "0"},
        saleshistory_row = new String[] {row[0]};

        products = inserted_into(products, row);
        sales = inserted_into(sales, sales_row);
        sales_history = inserted_into(sales_history, saleshistory_row);
    }



    static void remove_product(int row_index) {
        products = removed_from(products, row_index);
        sales = removed_from(sales, row_index);
        sales_history = removed_from(sales_history, row_index);
    }



    static void log_activity(String username, String activity) {
        if (activity_log.length > ACTIVITY_LOG_LIMIT) {
            activity_log = removed_from(activity_log, 1);
        }
        activity_log = inserted_into(activity_log, new String[]{saved_date + ", " + get_time(), username, activity});
    }



    static void daily_update() {
        String 
        current_date = ZonedDateTime.now(phtZone).format(date_f);
        int 
        product = 1,
        sales_rows = sales.length;

        if (saved_date != null && !current_date.equals(saved_date)) {
            for (product = 1; product < sales_rows; product++) {
                update_history(product, sales[product][2]);
                sales[product][2] = "0";
                sales[product][3] = ""+ Utils.total_sales(sales_history[product], 7);
                sales[product][4] = ""+ Utils.total_sales(sales_history[product], 30);
            }
        }

        saved_date = current_date;
    }



    static void update_history(int row_index, String new_sales) {
        int 
        day = 0, 
        days = sales_history[0].length-1;

        for (day = 1; day < days-1; day++) {
            sales_history[row_index][day] = sales_history[row_index][day+1];
        }

        sales_history[row_index][days] = new_sales;
    }



    static String get_time() {
        return ZonedDateTime.now(phtZone).format(time_f);
    }



    static void save_all() {
        save_table(sales_history, "sales_history");
        save_table(activity_log, "activity_log");
        save_table(accounts, "accounts");
        save_table(products, "products");
        save_table(sales, "sales");
        try {
            FileWriter writer = new FileWriter(txtfolder+"others.txt");
            writer.write(next_user_id + "\n");
            writer.write(next_product_id + "\n");
            writer.write(saved_date + "\n");
            writer.close();
        } 
        catch (Exception e) { }
    }



    static void load_all() {
        load_table(sales_history, "sales_history");
        load_table(activity_log, "activity_log");
        load_table(accounts, "accounts");
        load_table(products, "products");
        load_table(sales, "sales");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(txtfolder+"others.txt"));
            next_user_id = Utils.parse(reader.readLine());
            next_product_id = Utils.parse(reader.readLine());
            saved_date = reader.readLine();
            reader.close();
        } 
        catch (Exception e) { }
    }



    static void save_table(String[][] table, String file_name) {
        try (FileWriter writer = new FileWriter(txtfolder+file_name + ".txt")){
            String 
            string_table = "";
            int
            r = 1,
            rows = table.length;
            
            for (r = 1; r < rows; r++) {
                for(String c : table[r]) {
                    if (c != null) {
                        string_table += c + TXTFILE_SEPARATOR; 
                    }
                    else {
                        string_table += "null" + TXTFILE_SEPARATOR;
                    }
                }
                string_table += "\n";
            }

            writer.write(string_table);
            writer.close();
        }
        catch (Exception e) { }
    }



    static void load_table(String[][] table, String file_name) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(txtfolder+file_name + ".txt"));
            String line = "";

            while ((line = reader.readLine()) != null) {
                String[] row = line.split(TXTFILE_SEPARATOR);

                for (int i = 0; i < row.length; i++) {
                    if (row[i].equals("null")) {
                        row[i] = null;
                    }
                }
                switch (file_name) {
                    case "sales_history": sales_history = inserted_into(sales_history, row); break;
                    case "activity_log": activity_log = inserted_into(activity_log, row); break;
                    case "accounts": accounts = inserted_into(accounts, row); break;
                    case "products": products = inserted_into(products, row); break;
                    case "sales": sales = inserted_into(sales, row); break;
                }
            }

            reader.close();
        } 
        catch (Exception e) { }
    }
}