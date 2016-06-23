
import java.io.FileReader;
import java.util.Iterator;

import com.sun.tools.javac.file.RelativePath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author by Konrad Gondek
 */

public class readPermission {

    public static void main(String[] args) {
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(
                    RelativePath.RelativeFile.class.getClassLoader().getResource("permissions.txt").getPath()));

            JSONObject jsonObject = (JSONObject) obj;

            String name = (String) jsonObject.get("Name");
            JSONArray permissionDatabase = (JSONArray) jsonObject.get("user permissions");

            System.out.println("Name: " + name);
            System.out.println("\nuser permissions:");
            Iterator<String> iterator = permissionDatabase.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}