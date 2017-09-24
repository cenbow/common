package kelly.amazon;

import java.util.Stack;

/**
 * Created by kelly.li on 17/8/27.
 */
public class A03_Baseball {

//丢棒球，输入一个字符串，其中包括整数，Z，X，或者+。整数代表此轮得分，X：当前成绩是double前面一个分数，+：当前成绩是前两个的和，Z：移除前一个成绩，求最后的总成绩和
//一颗栗子： 输入["5", "-2", "4", "Z","X", 9, "+", "+"]
//output: 27
//5 : sum = 5
//-2 : sum = 5 - 2 = 3
//4 : sum = 3 + 4 = 7
//Z : sum = 7 - 4 = 3
//X : sum = 3 + -2 * 2 = -1 (4被移除了，前一个成绩是-2)
//9 : sum = -1 + 9 = 8
//+ : sum = 8 + 9 - 4 = 13 (前两个成绩是9和-4)
//+ : sum = 13 + 9 + 5 = 27 (前两个成绩是5 和 9)

    public int sum(String[] arr) {
        Stack<Integer> stack = new Stack<Integer>();
        int sum = 0;
        for (String str : arr) {
            try {
                int i = Integer.parseInt(str);
                stack.push(i);
                sum += i;
            } catch (NumberFormatException e) {
                if ("Z".equals(str)) {
                    int i = stack.pop();
                    sum -= i;
                } else if ("+".equals(str)) {
                    //加分这里先顺序弹出来,然后再逆序放进去
                    int i = stack.pop();
                    int j = stack.pop();
                    stack.push(j);
                    stack.push(i);
                    stack.push(i + j);
                    sum += i + j;
                } else if ("X".equals(str)) {
                    int i = stack.peek();
                    stack.push(i * 2);
                    sum += i * 2;

                }
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        A03_Baseball baseball = new A03_Baseball();
        int sum = baseball.sum(new String[]{"5", "-2", "4", "Z", "X", "9", "+", "+"});
        System.out.println(sum);
    }
}
