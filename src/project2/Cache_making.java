package project2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Cache_making {
    //Cache memory 만들어놓기
    static CacheSet2_3[] L3 = new CacheSet2_3[64];
    static CacheSet2_3[] L2 = new CacheSet2_3[4];
    static CacheSet1[] L1 = new CacheSet1[1];


    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        //첫번째, 데이터 정리해서 String[](memory)에 넣기
        int inputMem = 0;
        String[] memArr = new String[4096];
        BufferedReader inputFile;
        try {
            inputFile = new BufferedReader(new FileReader("EnglishWord.txt"));
            String str = null;
            while ((str = inputFile.readLine()) != null) {
                memArr[inputMem] = str;
                inputMem++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e){
            System.out.println("Memory is full!");
        }
//        Arrays.sort(memArr);

        //각 cache layer마다 cacheSet 생성자 만들어주기
        L1[0] = new CacheSet1();
        for (int i=0; i<4; i++) {
            L2[i] = new CacheSet2_3();
        }
        for (int i=0; i<64; i++) {
            L3[i] = new CacheSet2_3();
        }

        //memory에서 읽을 input값 가져오기
        int count = 0;
        int L1HitCount = 0;
        int L2HitCount = 0;
        int L3HitCount = 0;
        int memoryRejectCount = 0;
        System.out.println("initial cache memory");
        viewCache(memArr);
        while (true) {
            count++;
            System.out.print("Input your word that you want to find in memory : ");
            String findWord = scanner.next();
            if (findWord.equals("exit")){
                break;
            }
//            if (count>1000) {
//                break;
//            }
            else {
                //찾으려는 word가 memory에서 몇 번째 index인지 (주소값찾기)
                int memIndex = 0;
                boolean boundCheck = false;
                for (int i = 0; i < 4096; i++) {
                    if (memArr[i].equals(findWord)) {
                        memIndex = i;
                        boundCheck = true;
                        break;
                    } else {
                        continue;
                    }
                }
                if (!boundCheck) {
                    memoryRejectCount++;
                    System.out.println("No data in memory. Please input again.");
                    continue;
                }

//                //랜덤하게 input넣기
//                memIndex = (int)(Math.random()*256);

                //index를 나눠서 tag랑 set으로 나눠보기
                String tenToBin = Integer.toBinaryString(memIndex);
                while (tenToBin.length() < 12) tenToBin = "0" + tenToBin;

                // 2진수 위치에 따라 나누기
                int tagOfL1 = Integer.parseInt(tenToBin.substring(0,11),2);
                int tagOfL2 = Integer.parseInt(tenToBin.substring(0,9),2);
                int setOfL2 = Integer.parseInt(tenToBin.substring(9,11),2);
                int tagOfL3 = Integer.parseInt(tenToBin.substring(0,5),2);
                int setOfL3 = Integer.parseInt(tenToBin.substring(5,11),2);

//                if (testCache1(tagOfL1, L1[0])) {
//                    L1HitCount++;
//                }
//                if (testCache2_3(tagOfL2, L2[setOfL2])) {
//                    L2HitCount++;
//                }
//                if (testCache2_3(tagOfL3, L3[setOfL3])) {
//                    L3HitCount++;
//                }

                change1(tagOfL1, count, L1[0]);
                change2_3(tagOfL2, count, L2[setOfL2]);
                change2_3(tagOfL3, count, L3[setOfL3]);

//                System.out.println("L1's hit ratio : " + String.valueOf(((double)L1HitCount/(double)count) * 100) + "%");
//                System.out.println("L2's hit ratio : " + String.valueOf(((double)L2HitCount/(double)count) * 100) + "%");
//                System.out.println("L3's hit ratio : " + String.valueOf((((double)L3HitCount/(double)count)) * 100) + "%");
//                System.out.println("-----------------------------------------");
                viewCache(memArr);
            }

        }
//        viewCache(memArr);
    }

    static boolean testCache1 (int tag, CacheSet1 cacheSet1) {
        if (cacheSet1.cacheLines.keySet().contains(tag)) {
            return true;
        }
        else {
            return false;
        }
    }
    static boolean testCache2_3 (int tag, CacheSet2_3 cacheSet2_3) {
        if (cacheSet2_3.cacheLines.keySet().contains(tag)) {
            return true;
        }
        else {
            return false;
        }
    }

    static void change2_3(int tag,  int count, CacheSet2_3 cacheSet2_3){
        //우선 set 안에 같은 태그가 있는지 확인
        if (cacheSet2_3.cacheLines.keySet().contains(tag)){
            //있으면 hit
            //count 업데이트
            cacheSet2_3.cacheLines.put(tag, count);
        }
        else {
            //없으면 miss
            // cacheLines가 꽉 찼는지 확인하기.(이때 cacheLines는 key가 2개여야함.)
            if (cacheSet2_3.cacheLines.keySet().size() == 2) {
                //가득 차있으면 cacheLines의 key value(count)를 확인해서 제일 작은 걸 없애고, 새로운 태그랑 count 추가
                int[] tagArr = new int[2];
                int[] valueArr = new int[2];
                int i = 0;
                for (int tagKey : cacheSet2_3.cacheLines.keySet()){
                    tagArr[i] = tagKey;
                    valueArr[i] = cacheSet2_3.cacheLines.get(tagKey);
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
                cacheSet2_3.cacheLines.remove(minTag);
                cacheSet2_3.cacheLines.put(tag, count);
            }
            else {
                //set이 비어있거나, 한개만 차있으면 그냥 추가
                cacheSet2_3.cacheLines.put(tag,count);
            }
        }
    }

    static void change1 (int tag, int count, CacheSet1 cacheSet1) {
        //set이 1개, E도 1이기 때문에, tag가 같으면 hit
        //count 업데이트
        cacheSet1.cacheLines.clear();
        cacheSet1.cacheLines.put(tag, count);
    }

    public static class CacheSet2_3 {
        //L2와 L3에서 쓰는 cacheSet
        Map<Integer, Integer> cacheLines;

        CacheSet2_3(){
            this.cacheLines = new HashMap<>();
        }
    }

    public static class CacheSet1 {
        Map<Integer, Integer> cacheLines;

        CacheSet1 () {
            this.cacheLines = new HashMap<>();
        }
    }

    static void viewCache (String[] memArr) {
        if (!L1[0].cacheLines.keySet().isEmpty()) {
            String binTag1 = "";
            for (int tag : L1[0].cacheLines.keySet()) {
                binTag1 = Integer.toBinaryString(tag);
                String l1_1 = binTag1 + "0";
                String l1_2 = binTag1 + "1";
                System.out.println("L1 : " + memArr[Integer.parseInt(l1_1, 2)] + ", " + memArr[Integer.parseInt(l1_2, 2)]);
            }
            System.out.println("-------------------------------------------------------");
            String binTag2 = "";
            for (int i = 0; i < 4; i++) {
                int line = 0;
                for (int tag : L2[i].cacheLines.keySet()) {
                    binTag2 = Integer.toBinaryString(tag);
                    while (binTag2.length() < 9) binTag2 = "0" + binTag2;
                    String setBin2 = Integer.toBinaryString(i);
                    while (setBin2.length() < 2) setBin2 = "0" + setBin2;
                    String l2_1 = binTag2 + setBin2 + "0";
                    String l2_2 = binTag2 + setBin2 + "1";
                    System.out.println("L2's set" + String.valueOf(i) + " : " + "cacheLine " + line + " : "
                            + memArr[Integer.parseInt(l2_1, 2)] +
                            "," + memArr[Integer.parseInt(l2_2, 2)]);
                    line++;
                }
            }
            System.out.println("-------------------------------------------------------");
            String binTag3 = "";
            for (int i = 0; i < 64; i++) {
                int line = 0;
                for (int tag : L3[i].cacheLines.keySet()) {
                    binTag3 = Integer.toBinaryString(tag);
                    while (binTag3.length() < 5) binTag3 = "0" + binTag3;
                    String setBin3 = Integer.toBinaryString(i);
                    while (setBin3.length() < 6) setBin3 = "0" + setBin3;
                    String l3_1 = binTag3 + setBin3 + "0";
                    String l3_2 = binTag3 + setBin3 + "1";
                    System.out.println("L3's set" + String.valueOf(i) + " : " + "cacheLine " + line + " : "
                            + memArr[Integer.parseInt(l3_1, 2)] +
                            "," + memArr[Integer.parseInt(l3_2, 2)]);
                    line++;
                }
            }
        }
        else {
            System.out.println("!!!empty!!!");
        }
    }

}


