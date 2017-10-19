package dload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaDownloadFileFromURL {
    private static String URL = "http://172.16.127.9/fki/";

    private static String DIR = "c:\\testFolder\\";
    private static String DFOLDER = "c:\\testFolder\\dfolder\\";

    private static String OLD_FILENAME = "index_old.xml";
    private static String NEW_FILENAME = "index.xml";
//    private static String OLD_FILENAME = "indexold.xml";
//    private static String NEW_FILENAME = "indexnew.xml";

    public static void main(String[] args) throws IOException {
        String url = "http://172.16.127.9/fki/index.xml";
        Long start;

        if (!(new File(DIR).exists())) {
            new File(DIR).mkdir();
        }
        if (!(new File(DFOLDER).exists())) {
            new File(DFOLDER).mkdir();
        }
        start = System.currentTimeMillis();
        System.out.println(start);

        downloadUsingNIO(url, DIR + NEW_FILENAME);

        System.out.println("time1 : " + (System.currentTimeMillis() - start));
        System.out.println("File is downloading!");

//        start = System.currentTimeMillis();
//        downloadUsingStream(url, "c:\\testFolder\\index_stream.xml");
//        System.out.println("time2 : " + (System.currentTimeMillis() - start));


        List<InfoXML> infListNew = ParseXML.parseXML(DIR + NEW_FILENAME);

        System.out.println("-------------------------------------------------");

        if (new File(DIR + OLD_FILENAME).exists()) {
            List<InfoXML> infListOld = ParseXML.parseXML(DIR + OLD_FILENAME);

            Map<String, String> mOld = new HashMap<>();
            Map<String, String> mNew = new HashMap<>();

            for (InfoXML i : infListOld) {
                mOld.put(i.toString(), i.docName);
            }
            for (InfoXML i : infListNew) {
                mNew.put(i.toString(), i.docName);
            }


            System.out.println("----------------------------------");
            int i = 1;
            if (mNew.size() >= mOld.size()) {
                for (String s : mNew.keySet()) {
                    if (!mOld.containsKey(s.toString())) {
                        System.out.println(i++ + " " + s.toString());
                        String fileName = mNew.get(s);
                        downloadUsingNIO(URL + mNew.get(s) + ".xml.xz", DFOLDER + fileName + ".xml.xz");
                    } else {
                        System.out.println("no update needed!");
                    }
                }
            } else {
                for (String s : mOld.keySet()) {
                    if (!mNew.containsKey(s.toString())) {
                        System.out.println(i++ + " " + s.toString());
                        System.out.println(mOld.get(s));
                    } else {
                        System.out.println("no update needed!");
                    }
                }
            }
        } else {
            for (InfoXML i : infListNew) {
                System.out.println("downloading : " + i.toString());
                downloadUsingNIO(URL + i.getDocName() + ".xml.xz", DFOLDER + i.getDocName() + ".xml.xz");
            }

        }
        delOldFile(DIR, OLD_FILENAME);
        renameFile();
    }

    private static void downloadUsingNIO(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    private static void downloadUsingStream(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = bis.read(buffer, 0, 1024)) != -1) {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

    private static void delOldFile(String dir, String fName) {
        String path = dir + fName;
        File file = new File(path);
        if (file.delete()) {
            System.out.println("file is deleted!");
        } else {
            System.out.println("file isn't deleted!");
        }
    }

    private static void renameFile() {
        final File oldFile = new File(DIR, NEW_FILENAME);
        final File newFile = new File(DIR, OLD_FILENAME);
        if (oldFile.exists() && !newFile.exists()) {
            if (oldFile.renameTo(newFile)) {
                System.out.println("file is renamed");
            } else {
                System.out.println("file isn't renamed");
            }
        }
    }
}
