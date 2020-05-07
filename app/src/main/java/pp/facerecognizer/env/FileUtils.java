package pp.facerecognizer.env;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FileUtils {
    private static final Logger LOGGER = new Logger();
    public static final String ROOT =
            Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "facerecognizer";

    public static final String DATA_FILE = "data";
    public static final String MODEL_FILE = "model";
    public static final String LABEL_FILE = "label";
    public static final String UNKNWON_DATA_FILE = "unknown_data";
    public static final String UNKNOWN_MODEL_FILE = "unknown_model";
    public static final String UNKNOWN_LABEL_FILE = "unknown_label";
    public static final String IMAGE_FILE = "image_path";
    public static final String IMAGE_DIR  = "face_album";

    public static final String REACTION_FILE = "reaction_time";
    public static final String ACCURACY_FILE = "accuracy";
    public static final String ANNOTATION_FILE = "annotations";

    /**
     * Saves a Bitmap object to disk for analysis.
     *
     * @param bitmap The bitmap to save.
     * @param filename The location to save the bitmap to.
     */
    public static void saveBitmap(final Bitmap bitmap, final String filename) {
        LOGGER.i("Saving %dx%d bitmap to %s.", bitmap.getWidth(), bitmap.getHeight(), ROOT);
        final File myDir = new File(ROOT);

        if (!myDir.mkdirs()) {
            LOGGER.i("Make dir failed");
        }

        final File file = new File(myDir, filename);
        if (file.exists()) {
            file.delete();
        }
        try {
            final FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 99, out);
            out.flush();
            out.close();
        } catch (final Exception e) {
            LOGGER.e(e, "Exception!");
        }
    }

    public static String saveBitmap(final Bitmap bitmap, final String filename, final String Dir)
    {
        LOGGER.i("Saving %dx%d bitmap to %s.", bitmap.getWidth(), bitmap.getHeight(), ROOT + File.separator + Dir);
        final File myDir = new File(ROOT + File.separator + Dir);

        final File file = new File(myDir, filename);
        if (file.exists()) {
            file.delete();
        }
        try {
            final FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 99, out);
            out.flush();
            out.close();
        } catch (final Exception e) {
            LOGGER.e(e, "Exception!");
        }

        return file.getPath();
    }

    public static void copyAsset(AssetManager mgr, String filename) {
        InputStream in = null;
        OutputStream out = null;

        try {
            File file = new File(ROOT + File.separator + filename);
            if (!file.exists()) {
                file.createNewFile();
            }

            in = mgr.open(filename);
            out = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
        } catch (Exception e) {
            LOGGER.e(e, "Excetion!");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.e(e, "IOExcetion!");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.e(e, "IOExcetion!");
                }
            }
        }
    }

    public static void appendText(String text, String filename) {
        try(FileWriter fw = new FileWriter(ROOT + File.separator + filename, true);
            PrintWriter out = new PrintWriter(new BufferedWriter(fw))) {
            out.println(text);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
            LOGGER.e(e, "IOException!");
        }
    }

    public static ArrayList<String> readLabel(String filename) throws FileNotFoundException{
        Scanner s = new Scanner(new File(ROOT + File.separator + filename));
        ArrayList<String> list = new ArrayList<>();
        while (s.hasNextLine()){
            list.add(s.nextLine());
        }
        s.close();

        return list;
    }

    public static void cleanupUnknownCache(String label_filename, String data_filename) throws IOException {
        ArrayList<Character> known_idx = new ArrayList<>();
        int idx = 0;
        File l_file = new File(ROOT + File.separator + label_filename);
        File d_file = new File(ROOT + File.separator + data_filename);
        File l_file_b = new File(ROOT + File.separator + label_filename + "_backup");
        File d_file_b = new File(ROOT + File.separator + data_filename + "_backup");
        // Cleanup label file
        try
        {
            FileWriter fw = new FileWriter(ROOT + File.separator + label_filename + "_backup", true);
            BufferedWriter out = new BufferedWriter(fw);
            Scanner sf = new Scanner(l_file);

            while (sf.hasNextLine()){
                String l = sf.nextLine();
                if (!l.toLowerCase().contains("unknown"))
                {
                    out.write(l);
                    out.newLine();
                    known_idx.add((char) (idx + '0'));  // convert to char
                }
                else
                {
                    // Remove the cached image_path file
                    File image_cache = new File(ROOT + File.separator + l + ".png");
                    if (image_cache.exists()) {
                        image_cache.delete();
                    }
                }
                idx++;
            }

            sf.close();
            out.close();
            fw.close();
        }
        catch (IOException e) {
            //exception handling left as an exercise for the reader
            LOGGER.e(e, "IOException!");
        }

        if (idx == 0)   // no unknown, skipp the following steps to save time
        {
            return;
        }

        // Cleanup datafile
        try
        {
            Scanner sd = new Scanner(d_file);
            FileWriter fw = new FileWriter(ROOT + File.separator + data_filename + "_backup", true);
            PrintWriter out = new PrintWriter(new BufferedWriter(fw));

            while(sd.hasNextLine())
            {
                String l = sd.nextLine();
                if(known_idx.contains(l.charAt(0)))
                {
                    out.println(l);
                }
            }
            sd.close();
            out.close();
            fw.close();
        }
        catch (IOException e) {
            //exception handling left as an exercise for the reader
            LOGGER.e(e, "IOException!");
        }

        if(l_file_b.canRead() && d_file_b.canRead())
        {
            if (l_file.exists()) {
                l_file.delete();
            }
            if (d_file.exists()) {
                d_file.delete();
            }
            l_file_b.renameTo(l_file);
            d_file_b.renameTo(d_file);
        }
    }

    public static void cleanupUnknownCache()
    {
        File directory = new File(ROOT);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; ++i)
        {
            // IMPORTANT: it is key to keep a space after unknown or else the unknown_data
            if (files[i].getName().toLowerCase().contains("unknown"))
            {
                files[i].delete();
            }
        }
    }

    public static void updateImagePathRecord(Boolean newLine, String imgPath, int label)
    {
        try
        {
            if (countLines(ROOT + File.separator + IMAGE_FILE) == label) // just append at the back of the image_path file
            {
                appendText(imgPath, IMAGE_FILE);
            }
            else    // update image_path path
            {
                File file = new File(ROOT + File.separator + IMAGE_FILE);
                File temp = new File(ROOT + File.separator + IMAGE_FILE + "_temp");
                BufferedWriter out = new BufferedWriter(new FileWriter(temp, true));
                Scanner sf = new Scanner(file);

                String oldTxt = "";
                int count = 0;
                while (sf.hasNextLine()){
                    String l = sf.nextLine();
                    if (count == label)
                    {
                        out.write(imgPath);
                    }
                    else
                    {
                        out.write(l);
                    }
                    out.newLine();
                    count++;
                }
                file.delete();
                temp.renameTo(file);
                sf.close();
                out.close();
            }

        }
        catch (IOException e)
        {
            LOGGER.e(e, "Exception!");
        }

    }

    public static String saveUnknownImage(String name, String path)
    {

        try
        {
            File uri_file = new File(path);
            File file = new File(ROOT + File.separator + uri_file.getName());

            // NOTE: assumption here is that only photo from app folder will contain the keyword UNKNOWN
            if (file.getName().toLowerCase().contains("unknown"))
            {
                LOGGER.i("Melissa: old path is: " + path);

                File newpath = new File(ROOT + File.separator + IMAGE_DIR + File.separator + name + ".png");
                LOGGER.i("Melissa: new path is: " + ROOT + File.separator + IMAGE_DIR + File.separator + name + ".png");
                if (newpath.exists())
                {
                    newpath.delete();
                }

                if (file.renameTo(newpath))
                {
                    file.delete();
                    LOGGER.i("Melissa: Renamed successfully");
                }
                else
                {
                    LOGGER.i("Melissa: Renamed failed");
                }

                return newpath.toString();
            }
        }
        catch (Exception e)
        {
            LOGGER.e(e, "Exception!");
        }

        return path;
    }

    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];

            int readChars = is.read(c);
            if (readChars == -1) {
                // bail out if nothing to read
                return 0;
            }

            // make it easy for the optimizer to tune this loop
            int count = 0;
            while (readChars == 1024) {
                for (int i=0; i<1024;) {
                    if (c[i++] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            // count remaining characters
            while (readChars != -1) {
                System.out.println(readChars);
                for (int i=0; i<readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            return count == 0 ? 1 : count;
        } finally {
            is.close();
        }
    }



}
