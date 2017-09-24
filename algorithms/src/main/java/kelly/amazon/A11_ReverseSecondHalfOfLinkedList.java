package kelly.amazon;

/**
 * Created by kelly.li on 17/9/1.
 */
public class A11_ReverseSecondHalfOfLinkedList {

    public ListNode reverseList(ListNode head) {

        if (head == null || head.next == null) {
            return head;
        }
        ListNode slow = head;
        ListNode fast = head;
        ListNode prev = null;
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            prev = slow;
            slow = slow.next;
        }
        prev.next = reverseList2(slow);
        return head;
    }

    public ListNode reverseList2(ListNode head) {
        ListNode temp = null;
        ListNode prev = null;
        while (head.next != null) {
            temp = head.next;
            head.next = prev;
            prev = head;
            head = temp;
        }
        return prev;
    }
}
