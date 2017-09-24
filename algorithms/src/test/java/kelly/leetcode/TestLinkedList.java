package kelly.leetcode;

import org.junit.Test;

/**
 * Created by kelly.li on 17/7/21.
 */
public class TestLinkedList {

//    [2,4,3]
//    [5,6,4]
    @Test
    public void test002() {
        L002_AddTwoNumbers l002 = new L002_AddTwoNumbers();

        ListNode l1 = new ListNode(2);
        l1.next = new ListNode(4);
        l1.next.next = new ListNode(3);

        ListNode l2 = new ListNode(5);
        l2.next = new ListNode(6);
        l2.next.next = new ListNode(4);

        ListNode result = l002.addTwoNumbers(l1, l2);
    }


    @Test
    public void test003() {
        L003_LongestSubstringWithoutRepeatingCharacters l003 = new L003_LongestSubstringWithoutRepeatingCharacters();
        int length = l003.lengthOfLongestSubstring("abcabcbb");
        System.out.println(length);
    }

    @Test
    public void test146() {
        L146_LRUCache l146 = new L146_LRUCache(2);
        l146.put(1, 1);
        l146.put(2, 2);
        int value = l146.get(1);
        System.out.println(value);

        l146.put(3, 3);
        value = l146.get(2);
        System.out.println(value);
        l146.put(4, 4);
        value = l146.get(3);
        System.out.println(value);
        value = l146.get(4);
        System.out.println(value);
    }


}
