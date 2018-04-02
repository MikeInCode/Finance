package mike.finance;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileReader {

    public static List<String> readFile(Context context, int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String txtLine;
        List<String> list = new ArrayList<>();
        try {
            while ((txtLine = bufferedReader.readLine()) != null) {
                list.add(txtLine);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
