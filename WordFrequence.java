import javax.net.ssl.SNIHostName;
import javax.sound.sampled.Line;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.*;
import java.util.Map.Entry;

public class WordFrequence{

    public static void main(String[] args)throws Exception{
        if(args.length > 0){
            String order = args[0];
            if(order.equals("-c")){
                StringBuffer sb = new StringBuffer();
                for(int i = 1;i<args.length;i++){
                    sb.append(args[i]);
                    if(i != args.length - 1){
                        sb.append(" ");
                    }
                }
                String path = sb.toString();
                getFrequence(path);
            }
        }
    }

    /**
     * 第0步 统计词频
     *
     * @param path
     * @throws Exception
     */
    public static void getFrequence(String path)throws Exception{
        //使用TreeMap储存结果 方便后面的排序操作
        Map<Integer,Integer> map = new TreeMap<>();
        //读取文件
        FileReader reader = new FileReader(path);
        BufferedReader bf = new BufferedReader(reader);
        //读取文件中的每一行
        String line;
        //预存字母表
        for(int i = 97;i<=122;i++){
            map.put(i,0);
        }
        //对每一行进行统计
        while((line = bf.readLine()) != null){
            char[] words = line.toLowerCase().toCharArray();
            for(int i = 0;i<words.length;i++){
                if((int)words[i] >= 97 && (int)words[i] <= 122 ){
                    map.put((int)words[i] , map.get((int)words[i])+1);
                }
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
}