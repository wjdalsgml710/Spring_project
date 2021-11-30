package com.example.sproject.model.Practice;

import java.util.Arrays;
import java.util.List;

public class Practice {
	    public int[] solution(int[] arr) {
	        List<int[]> intList = Arrays.asList(arr);
	        int[] answer = {};
	        List<int[]> answer2 = Arrays.asList(answer);
	        int min = 0;
	        if(arr.length != 1) {
	            for(int i = 0; i < arr.length; i++) {
	                min = arr[i] > arr[i+1] ?  arr[i+1] : arr[i];
	            }
	            intList.remove(min);
	            answer2 = intList;            
	        }
	        else {
	            answer2.add(-1, answer);
	        }
	        return answer;
	    }
	}
