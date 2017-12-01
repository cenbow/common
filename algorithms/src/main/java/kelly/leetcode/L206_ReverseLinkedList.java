package kelly.leetcode;

/**
 * Created by kelly-lee on 17/7/23.
 */
public class L206_ReverseLinkedList {

    public ListNode reverseList(ListNode head) {
        ListNode temp = null;
        ListNode prev = null;

        while (head != null) {
            temp = head.next;
            head.next = prev;
            prev = head;
            head = temp;
        }
        return prev;
    }

}
