package kelly.leetcode;

/**
 * Created by kelly.li on 17/7/23.
 */
public class L004_MedianOfTwoSortedArrays {


    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int len = nums1.length + nums2.length;
        if (len % 2 == 1) {
            return findMedianSortedArrays(nums1, 0, nums2, 0, len / 2 + 1);
        } else {
            return (findMedianSortedArrays(nums1, 0, nums2, 0, len / 2) + findMedianSortedArrays(nums1, 0, nums2, 0, len / 2 + 1)) / 2;
        }
    }


    public double findMedianSortedArrays(int[] nums1, int start1, int[] nums2, int start2, int k) {
        if (start1 >= nums1.length) {
            return nums2[start2 + k - 1];
        }
        if (start2 >= nums2.length) {
            return nums1[start1 + k - 1];
        }
        if (k == 1) {
            return Math.min(nums1[start1], nums2[start2]);
        }
        int val1 = start1 + k / 2 - 1 <= nums1.length ? nums1[start1 + k / 2 - 1] : Integer.MAX_VALUE;
        int val2 = start2 + k / 2 - 1 <= nums2.length ? nums2[start1 + k / 2 - 1] : Integer.MAX_VALUE;
        if (val1 < val2) {
            return findMedianSortedArrays(nums1, start1 + k / 2, nums2, start2, k - k / 2);
        } else {
            return findMedianSortedArrays(nums1, start1, nums2, start2 + k / 2, k - k / 2);
        }

    }
}
