package kelly.leetcode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by kelly.li on 17/7/19.
 */
public class L225_ImplementStackUsingQueues {

    private Queue<Integer> cur;
    private Queue<Integer> top;

    /**
     * Initialize your data structure here.
     */
    public L225_ImplementStackUsingQueues() {
        cur = new LinkedList<Integer>();
        top = new LinkedList<Integer>();
    }

    /**
     * Push element x onto stack.
     */
    public void push(int x) {
        if (!top.isEmpty()) {
            cur.offer(top.poll());
        }
        cur.offer(x);
    }

    /**
     * Removes the element on top of the stack and returns that element.
     */
    public int pop() {
        if (top.isEmpty()) {
            int size = cur.size() - 1; //cur的size在循环poll时是变化的,所以提前定义
            for (int i = 0; i < size; i++) {
                top.offer(cur.poll());
            }
            Queue<Integer> tmp = cur;
            cur = top;
            top = tmp;
        }
        return top.poll();
    }

    /**
     * Get the top element.
     */
    public int top() {
        if (top.isEmpty()) {
            int size = cur.size() - 1; //cur的size在循环poll时是变化的,所以提前定义
            for (int i = 0; i < size; i++) {
                top.offer(cur.poll());
            }
            Queue<Integer> tmp = cur;
            cur = top;
            top = tmp;
        }
        return top.peek();
    }

    /**
     * Returns whether the stack is empty.
     */
    public boolean empty() {
        return cur.isEmpty() && top.isEmpty();
    }
}

/**
 * Your MyStack object will be instantiated and called as such:
 * MyStack obj = new MyStack();
 * obj.push(x);
 * int param_2 = obj.pop();
 * int param_3 = obj.top();
 * boolean param_4 = obj.empty();
 */


//方式2 push的时候对调顺序