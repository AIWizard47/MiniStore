import com.sam.components.*;
import com.sam.ministore.*;
import java.util.*;
public class demo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Table tb = Controller.createTable("users","id:int","name:String","password:String");
        System.out.print("Do you want to create a new user : ");
        String select = sc.next();
        System.out.println("");
        if(select.equals("y") || select.equals("yes")){
            System.out.println("inserting...");
        }
        // String scs = tb.insert();
    }
}
