package project2;

import java.util.Map;
import java.util.Scanner;

public class Cache_making {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //첫번째, 데이터 정리해서 String[](memory)에 넣기


        //Cache memory 만들어놓기
        CacheSet[] L3 = new CacheSet[64];
        CacheSet[] L2 = new CacheSet[4];
        CacheSet[] L1 = new CacheSet[1];

        //memory에서 읽을 input값 가져오기
        while (true) {
            System.out.print("Input your word that you want to find in memory : ");
            String findWord = scanner.next();


            //memory에 있는 값을 불러오자

            CacheSet cacheSet = new CacheSet(0, 1);
            L3[0] = cacheSet;

        }
    }

    public static class CacheSet {
        Map<Integer, Integer> cacheLines;
//        CacheLine[] setArr;

        CacheSet(int tag,  int count){
            //우선 set 안에 같은 태그가 있는지 확인
            if (cacheLines.keySet().contains(tag)){
                //있으면 hit
                //count 업데이트
                cacheLines.put(tag, count);
            }
            else {
                //없으면 miss
                // cacheLines가 꽉 찼는지 확인하기.(이때 cacheLines는 key가 2개여야함.)
                if (cacheLines.keySet().size() == 2) {
                    //가득 차있으면 cacheLines의 key value(count)를 확인해서 제일 작은 걸 없애고, 새로운 태그랑 count 추가
                    int[] tagArr = new int[2];
                    int[] valueArr = new int[2];
                    int i = 0;
                    for (int tagKey : cacheLines.keySet()){
                        tagArr[i] = tagKey;
                        valueArr[i] = cacheLines.get(tagKey);
                        i++;
                    }
                    int minTag = 0;
                    //count가 같을 일은 없음
                    if (valueArr[0] > valueArr[1]){
                        minTag = tagArr[1];
                    }
                    else {
                        minTag = tagArr[0];
                    }
                    cacheLines.remove(minTag);
                    cacheLines.put(tag, count);
                }
                else {
                    //set이 비어있거나, 한개만 차있으면 그냥 추가
                    cacheLines.put(tag,count);
                }
            }
        }
    }

//    public static class CacheLine {
//        Map<Integer,Integer> map;
//    }
}


