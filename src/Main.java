import java.io.FileNotFoundException;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                try{
                    new View();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
