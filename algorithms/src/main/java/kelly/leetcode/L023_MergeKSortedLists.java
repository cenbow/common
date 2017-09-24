package kelly.leetcode;

/**
 * Created by kelly.li on 17/7/20.
 */
public class L023_MergeKSortedLists {


    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }
        return mergeKLists(lists, 0, lists.length - 1);

    }

    ListNode mergeKLists(ListNode[] lists, int start, int end) {
        if (start == end) {
            return lists[start];
        }
        int mid = (start + end) / 2;
        ListNode l1 = mergeKLists(lists, 0, mid);
        ListNode l2 = mergeKLists(lists, mid + 1, mid);
        return mergeTwoLists(l1, l2);
    }


    ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if (l1 == null && l2 == null) {
            return null;
        }
        if (l1 == null) {
            return l2;
        }
        if (l2 == null) {
            return l1;
        }
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                tail.next = l1;
                l1 = l1.next;
            } else {
                tail.next = l2;
                l2 = l2.next;
            }
            tail = tail.next;
        }


        tail.next = (l1 != null) ? l1 : l2;
        return dummy.next;
    }

}
//方法二 使用堆