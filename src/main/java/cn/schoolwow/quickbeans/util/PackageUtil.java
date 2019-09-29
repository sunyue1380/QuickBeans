package cn.schoolwow.quickbeans.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**包工具类*/
public class PackageUtil {
    private static Logger logger = LoggerFactory.getLogger(PackageUtil.class);
    /**
     * 根据包名获取其下所有类
     * @param packageName 包名
     * */
    public static List<Class> scanPackage(String packageName) {
        List<Class> classList = new ArrayList<>();
        String packageNamePath = packageName.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> urlEnumeration = classLoader.getResources(packageNamePath);
            while(urlEnumeration.hasMoreElements()){
                URL url = urlEnumeration.nextElement();
                if("file".equals(url.getProtocol())){
                    handleFile(url,packageName,classList);
                }else if("jar".equals(url.getProtocol())){
                    handleJar(url,packageNamePath,classList);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return classList;
    }

    /**处理文件路径*/
    private static void handleFile(URL url,String packageName,List<Class> classList) throws ClassNotFoundException {
        File file = new File(url.getFile());
        //TODO 对于有空格或者中文路径会无法识别
        logger.info("[扫描路径]{}",file.getAbsolutePath());
        if(!file.isDirectory()){
            throw new IllegalArgumentException("包名不是合法的文件夹!"+url.getFile());
        }
        Stack<File> stack = new Stack<>();
        stack.push(file);

        String indexOfString = packageName.replace(".","/");
        while(!stack.isEmpty()){
            file = stack.pop();
            for(File f:file.listFiles()){
                if(f.isDirectory()){
                    stack.push(f);
                }else if(f.isFile()&&f.getName().endsWith(".class")){
                    String path = f.getAbsolutePath().replace("\\","/");
                    int startIndex = path.indexOf(indexOfString);
                    String className = path.substring(startIndex,path.length()-6).replace("/",".");
                    classList.add(Class.forName(className));
                }
            }
        }
    }

    /**处理jar包路径*/
    private static void handleJar(URL url,String packageNamePath,List<Class> classList) throws ClassNotFoundException, IOException {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        if (null != jarURLConnection) {
            JarFile jarFile = jarURLConnection.getJarFile();
            if (null != jarFile) {
                Enumeration<JarEntry> jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                    JarEntry jarEntry = jarEntries.nextElement();
                    String jarEntryName = jarEntry.getName();
                    if (jarEntryName.contains(packageNamePath) && jarEntryName.endsWith(".class")) {
                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                        classList.add(Class.forName(className));
                    }
                }
            }
        }
    }
}
