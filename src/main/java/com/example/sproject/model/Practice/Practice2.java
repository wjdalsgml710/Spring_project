package com.example.sproject.model.Practice;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public class Practice2 {
	public int[] solution(long n) {
        String n2 = String.valueOf(n);
        String[] num = n2.split("");
        String number = "";
        for(int i = num.length-1; i >= 0; i--) {
            number += num[i];
        }
        int num3 = Integer.parseInt(number);
        int[] answer = Stream.of(String.valueOf(num3).split("")).mapToInt(Integer::parseInt).toArray();
        System.out.println(answer);
        return answer;
    }
}
