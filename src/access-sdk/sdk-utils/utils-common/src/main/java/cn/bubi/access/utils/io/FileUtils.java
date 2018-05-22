package cn.bubi.access.utils.io;

import java.io.*;

/**
 * @author haiq
 */
public class FileUtils{

    /**
     * 返回指定文件的首行非空行(即包含了非空字符串的行)；
     *
     * @param file
     * @param charset 字符集；
     * @return 返回首行非空行；返回结果不会自动截取两头的空字符串；
     * @throws IOException
     */
    public static String getFirstLineText(File file, String charset) throws IOException{
        FileInputStream in = new FileInputStream(file);
        try {
            InputStreamReader reader = new InputStreamReader(in, charset);
            return getFirstLine(reader);
        } finally {
            in.close();
        }
    }

    public static String getFirstLine(Reader reader) throws IOException{
        BufferedReader bfr = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
        try {
            String line = null;
            while ((line = bfr.readLine()) != null) {
                if (line.trim().length() > 0) {
                    return line;
                }
            }
            return null;
        } finally {
            bfr.close();
        }
    }

    /**
     * 将指定的文本保存到指定的文件中；
     *
     * @param file    要保存的文件；
     * @param charset 字符集；
     * @param text    文本内容；
     * @throws IOException
     */
    public static void saveText(File file, String charset, String text) throws IOException{
        FileOutputStream out = new FileOutputStream(file, false);
        try {
            OutputStreamWriter writer = new OutputStreamWriter(out, charset);
            try {
                writer.write(text);
            } finally {
                writer.close();
            }
        } finally {
            out.close();
        }
    }

    public static String readText(String file, String charset) throws IOException{
        return readText(new File(file), charset);
    }

    public static String readText(File file, String charset) throws IOException{
        FileInputStream in = new FileInputStream(file);
        try {
            return readText(in, charset);
        } finally {
            in.close();
        }
    }


    public static String readText(InputStream in, String charset) throws IOException{
        InputStreamReader reader = new InputStreamReader(in, charset);
        try {
            StringBuilder content = new StringBuilder();
            char[] buffer = new char[64];
            int len = 0;
            while ((len = reader.read(buffer)) > 0) {
                content.append(buffer, 0, len);
            }
            return content.toString();
        } finally {
            reader.close();
        }
    }

}
