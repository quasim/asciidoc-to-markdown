package com.jdd.apidoc;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.PreprocessorReader;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

import static org.asciidoctor.jruby.AsciidoctorJRuby.Factory.create;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        Asciidoctor asciidoctor = create();
        asciidoctor.javaExtensionRegistry().includeProcessor(new IncludeProcessor() {
            @Override
            public boolean handles(String target) {
                if (target.endsWith(".adoc")) {
                    return true;
                }
                return false;
            }

            @Override
            public void process(Document document, PreprocessorReader reader, String target, Map<String, Object> attributes) {
                try {
                    String pathname = interpolate(target, attributes);
                    File targetFile = isAbsolutePath(pathname) ? new File(pathname) : new File(reader.getDir(), pathname);
                    String data = fileRead(targetFile, "UTF-8");
//                    if (target.endsWith("-title.adoc")) {
//                        reader.push_include("[backspace]", target, targetFile.getAbsolutePath(), 1, attributes);
//                        reader.push_include(data, target, targetFile.getAbsolutePath(), 1, attributes);
//                    } else {
                        reader.push_include("[include]", target, targetFile.getAbsolutePath(), 1, attributes);
                        reader.push_include(data, target, targetFile.getAbsolutePath(), 1, attributes);
                        reader.push_include("[include]", target, targetFile.getAbsolutePath(), 1, attributes);
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        asciidoctor.javaConverterRegistry().register(MarkdownConverter.class);
        asciidoctor.convertFile(new File("C:\\Users\\Administrator\\IdeaProjects\\apidoc-demo\\apidoc-demo-api\\src\\docs\\asciidoc\\api-guide.adoc"), OptionsBuilder.options().backend(MarkdownConverter.DEFAULT_FORMAT));
    }

    public static boolean isAbsolutePath(String path) {
        if (path == null || path.length() == 0) {
            return false;
        }
        return '/' == path.charAt(0) || path.matches("^[a-zA-Z]:([/\\\\].*)?");
    }

    public static String interpolate(String text, Map<String, Object> namespace) {
        Iterator<String> keys = namespace.keySet().iterator();

        while (keys.hasNext()) {
            String key = keys.next();
            Object obj = namespace.get(key);
            if (obj == null) {
                throw new NullPointerException("The value of the key '" + key + "' is null.");
            }
            String value = obj.toString();
            text = text.replaceAll("{" + key + "}", value);
        }

        return text;
    }

    private static String fileRead(File file, String encoding) throws IOException {
        StringBuffer buf = new StringBuffer();

        Reader reader = null;

        try {
            if (encoding != null) {
                reader = new InputStreamReader(new FileInputStream(file), encoding);
            } else {
                reader = new InputStreamReader(new FileInputStream(file));
            }
            int count;
            char[] b = new char[512];
            while ((count = reader.read(b)) > 0) {
                buf.append(b, 0, count);
            }
        } finally {
            reader.close();
        }

        return buf.toString();
    }
}
