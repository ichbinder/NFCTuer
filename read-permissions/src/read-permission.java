import java.io.*;

/**
 * Created by konradgondek on 19.05.16.
 */


public class readPermission {

    String fileName;

    public readPermission(String fileName) {
        // Instanzvariable initialisieren
        this.fileName = fileName;
    }

    public void printDatei()
    {
        FileReader fr;
        BufferedReader br;
        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);

            // Textzeilen der Datei einlesen und auf Konsole ausgeben:
            String zeile;
            zeile = br.readLine();
            while (zeile != null) {
                System.out.println(zeile);
                zeile = br.readLine();
            }

            fr.close();
        }
        catch (IOException e){
            System.out.println("Fehler beim Lesen der Datei " + fileName);
            System.out.println(e.toString());
        }
    }

    public static void main(String[] args) {
        readPermission read  = new readPermission(../permissions.txt);
    }
}
