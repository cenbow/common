package kelly.leetcode;

/**
 * Created by kelly-lee on 17/7/21.
 */
public class L002_AddTwoNumbers {

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {

        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        boolean flag = false;
        while (l1 != null && l2 != null) {
            int sum = l1.val + l2.val + (flag ? 1 : 0);
            flag = (sum >= 10);
            sum = (flag ? sum - 10 : sum);
            cur.next = new ListNode(sum);
            l1 = l1.next;
            l2 = l2.next;
            cur = cur.next;
        }
        while (l1 != null) {
            int sum = l1.val + (flag ? 1 : 0);
            flag = (sum >= 10);
            cur.next = new ListNode(flag ? sum - 10 : sum);
            l1 = l1.next;
            cur = cur.next;
        }
        while (l2 != null) {
            int sum = l2.val + (flag ? 1 : 0);
            flag = (sum >= 10);
            cur.next = new ListNode(flag ? sum - 10 : sum);
            l2 = l2.next;
            cur = cur.next;
        }
        if(flag ){ //最后可能进1
            cur.next = new ListNode(1);
        }
        return dummy.next;

    }


}
