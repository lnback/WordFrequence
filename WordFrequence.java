import javax.net.ssl.SNIHostName;
import javax.sound.sampled.Line;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.*;
import java.util.Map.Entry;


public class WordFrequence{

    /**
     *  -c : 统计字母频率
     *  -f : 统计不重复的单词
     *  -d : 指定文件目录，对目录下每个文件统计不重复的单词
     *      -s : 递归目录下所有的子目录
     *      -n : 输出前n个单词的频率
     *  -x : 停词
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)throws Exception{
        if(args.length <= 1){
            System.out.println("请输入参数！");
        }else{
            //字母统计
            if(args[0].equals("-c")){
                String path = getPath(args,1);
                System.out.println("文件为：" + path);
                getLetterRate(path);
            }else if (args[0].equals("-f")){
                if(args[1].startsWith("-")){
                    int n = Math.abs(Integer.parseInt(args[1].substring(1)));
                    String path = getPath(args,2);
                    System.out.println("文件为：" + path);
                    getWordFrequence(path,n);
                }else{
                    String path = getPath(args,1);
                    System.out.println("文件为：" + path);
                    getWordFrequence(path,0);
                }
                //整个文件夹下的文件的单词统计
            }else if(args[0].equals("-d")){
                //递归所有子目录
                if(args[1].equals("-s")){
                    String directory = "";
                    //如果有-n命令
                    int n = 0;
                    if(args[2].startsWith("-")){
                        n = Math.abs(Integer.parseInt(args[2].substring(1)));
                        directory = getPath(args,3);
                    }else{
                        directory = getPath(args,2);
                    }
                    /**
                     * TODO 递归读取所有子目录中的文件
                     */
                    List<String> filePath = new ArrayList<>();
                    filePath = getAllFilePaths(directory,filePath);
                    for(String path : filePath){
                        System.out.println("文件为：" + path);
                        getWordFrequence(path,n);
                    }
                }else {
                    int n = 0;
                    String directory = "";
                    //如果有-n命令
                    if(args[1].startsWith("-")){
                        n = Math.abs(Integer.parseInt(args[1].substring(1)));
                        directory = getPath(args,2);
                    }else {
                        directory = getPath(args,1);
                    }
                    List<String> filePath = getFilePaths(directory);
                    for(String path : filePath){
                        System.out.println("文件为：" + path);
                        getWordFrequence(path,n);
                    }
                }
            }
        }


    }

    /**
     * 第0步 统计词频
     *
     * @param path
     * @throws Exception
     */
    public static void getLetterRate(String path)throws Exception{
        //使用TreeMap储存结果 方便后面的排序操作
        Map<Integer,Integer> map = new TreeMap<>();
        String text = getFileText(path);
        //预存字母表
        for(int i = 97;i<=122;i++){
            map.put(i,0);
        }
        //对每一行进行统计
        char[] letter = text.toCharArray();
        for(int i = 0;i<letter.length;i++){
            if((int)letter[i] >= 97 && (int)letter[i] <= 122 ){
                map.put((int)letter[i] , map.get((int)letter[i])+1);
            }
        }
        /**
         * TreeMap无法直接排序 将其转化为List在排序
         */
        List<Entry<Integer,Integer>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Entry<Integer, Integer>>() {
            @Override
            public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
                return 0 - (o1.getValue().compareTo(o2.getValue()));
            }
        });
        /**
         * 输出
         */
        for (Entry<Integer, Integer> e: list) {
            System.out.println((char)(int)e.getKey()+":"+e.getValue());
        }
    }

    /**
     * 第1步 统计单词出现的频率
     * @param path
     * @throws Exception
     */
    public static void getWordFrequence(String path,int n)throws Exception{
        Map<String,Integer> map = new TreeMap<>();
        String text = getFileText(path);
        String[] words = text.split("\\W+");
        for(String word : words){
            if(map.containsKey(word)){
                map.put(word,map.get(word)+1);
            }else {
                map.put(word,1);
            }
        }
        /**
         * TreeMap无法直接排序 将其转化为List在排序
         */
        List<Entry<String,Integer>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                return 0 - (o1.getValue().compareTo(o2.getValue()));
            }
        });
        /**
         * 输出
         */
        if(n == 0) n = list.size();
        for(int i = 0;i < n && i < list.size();i++){
            System.out.println(list.get(i).getKey() + ":" + list.get(i).getValue());
        }

    }

    /**
     * 获取单个文件的内容
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static String getFileText(String path)throws Exception{
        File file = new File(path);
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null){
            sb.append(line);
        }
        return sb.toString().toLowerCase().trim();
    }

    /**
     * 返回参数中的文件路径 并解决空格
     * @param args
     * @param n
     * @return
     */
    public static String getPath(String[] args,int n){
        StringBuffer sb = new StringBuffer();
        for(int i = n;i<args.length;i++){
            if(!args[i].startsWith("-")){
                sb.append(args[i]);
                if(i != args.length - 1){
                    sb.append(" ");
                }
            }else {
                break;
            }
        }
        String path = sb.toString();
        return path;
    }

    /**
     * 获取目录下的所有文件 非递归
     * @param folder
     * @return
     */
    public static List<String> getFilePaths(String folder){
        File file = new File(folder);
        System.out.println(file.toString());
        File[] files = file.listFiles();
        List<String> filePaths = new ArrayList<>( );
        for(File f : files){
            if (f.isFile()){
                filePaths.add(f.getAbsolutePath());
            }
        }
        return filePaths;
    }

    /**
     * 获取目录下的所有文件 以及所有子目录下的所有文件
     * @param path
     * @param filePath
     * @return
     */
    public static List<String> getAllFilePaths(String path,List<String> filePath){
        File[] files = new File(path).listFiles();
        if(files == null){
            return filePath;
        }

        for(File file : files){
            if(file.isDirectory()){
                getAllFilePaths(file.getAbsolutePath(),filePath);
            }else{
                filePath.add(file.getAbsolutePath());
            }
        }
        return filePath;
    }
}