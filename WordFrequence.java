
import javax.net.ssl.SNIHostName;
import javax.sound.sampled.Line;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.CheckedOutputStream;


public class WordFrequence{

    /**
     *  -c : 统计字母频率
     *  java WordFrequence -c (filePath)
     *
     *  -f : 统计不重复的单词
     *  java WordFrequence -f (filePath) <-n>
     *
     *  -d : 指定文件目录，对目录下每个文件统计不重复的单词
     *      -s : 递归目录下所有的子目录
     *      -n : 输出前n个单词的频率
     *  java WrodFrequence -d <-s> (filePath) <-n>
     *
     *  -x : 停词<filePath>
     *      -f : 停词过后的<filePath>
     *  java WordFrequence -x (stopFile) -f (filePath) <-n>
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)throws Exception{
        if(args.length <= 1){
            System.out.println("请输入参数！");
        }else{
            //字母统计
            if(args[0].equals("-c")){
                System.out.println("字母统计");
                String path = getPath(args,1);
                System.out.println("文件为：" + path);
                getLetterRate(path);
            }
            //单词统计
            else if (args[0].equals("-f")){
                System.out.println("单词统计");
                String path = getPath(args,1);
                System.out.println("文件为：" + path);
                int n = 0;
                if(args[args.length-1].startsWith("-")) {
                    n = Math.abs(Integer.parseInt(args[args.length-1].substring(1)));
                }
                getWordFrequence(path,n);
            }
            //整个文件夹下的文件的单词统计
            else if(args[0].equals("-d")){
                //递归所有子目录
                if(args[1].equals("-s")){
                    String directory = getPath(args,2);
                    //如果有-n命令
                    int n = 0;
                    if(args[args.length-1].startsWith("-")){
                        n = Math.abs(Integer.parseInt(args[args.length-1].substring(1)));
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
                    String directory = getPath(args,1);;
                    //如果有-n命令
                    System.out.println(directory);
                    if(args[args.length-1].startsWith("-")){
                        n = Math.abs(Integer.parseInt(args[args.length-1].substring(1)));
                    }
                    List<String> filePath = getFilePaths(directory);
                    for(String path : filePath){
                        System.out.println("文件为：" + path);
                        getWordFrequence(path,n);
                    }
                }
            }
            else if(args[0].equals("-x")){
                System.out.println("有停词表的单词统计");
                /**
                 * stopFile.txt无空格视为一个参数
                 * -f 后的路径直接取
                 * -n 在最后
                 */
                String stopWord = getPath(args,1);
                String filePath = getPath(args,3);
                int n = 0;
                if(args[args.length-1].startsWith("-")){
                    n = Math.abs(Integer.parseInt(args[args.length-1]));
                }
                getWordFrequenceWithoutStopWord(stopWord,filePath,n);
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
        File[] files = file.listFiles();
        List<String> filePaths = new ArrayList<>();
        for(File f : files){
            if (f.isFile()){
                System.out.println(f.getName());
                filePaths.add(f.getName());
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

    public static void getWordFrequenceWithoutStopWord(String stopWordPath,String filePath,int n)throws Exception{
        Map<String,Integer> map = new TreeMap<>();
        String stopWordText = getFileText(stopWordPath);
        String text = getFileText(filePath);
        String[] stopWords = stopWordText.split("\\W+");
        String[] words = text.split("\\W+");
        for(String stopWord : stopWords){
            if(!map.containsKey(stopWord)){
                map.put(stopWord,0);
            }
        }
        for(String word : words){
            if(map.containsKey(word)){
                if(!(map.get(word).intValue() == 0)){
                    map.put(word,map.get(word)+1);
                }
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
            if(!(list.get(i).getValue().intValue() == 0)){
                System.out.println(list.get(i).getKey() + ":" + list.get(i).getValue());
            }
        }
    }
}