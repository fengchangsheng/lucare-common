package com.fcs.design.algorithm.training;

/**
 * Created by fengcs on 2020/6/9.
 */
public class RecursiveOneTest {

    public static void main(String[] args) {
        char[] a = new char[]{'h','e','l','l','o'};

        new RecursiveOneTest().reverseString(a);
    }

    public void reverseString(char[] s) {
        System.out.print("[");
        help(0, s);
        System.out.print("]");
    }

    public void help(int index, char[] a){
        int len = a.length;

        if(a == null || index >= len){
            return;
        }

        help(index+1, a);
        System.out.print("\""+a[index]+"\"");
        if (index != 0) {
            System.out.print(",");
        }
    }

}
