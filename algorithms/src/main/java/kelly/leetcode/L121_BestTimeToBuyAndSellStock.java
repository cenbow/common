package kelly.leetcode;

/**
 * Created by kelly-lee on 17/8/15.
 */
public class L121_BestTimeToBuyAndSellStock {


    public int maxProfit(int[] prices) {
        if (prices == null || prices.length < 1) {
            return 0;
        }
        int min = prices[0];
        int maxProfit = 0;
        for (int i = 1; i < prices.length; i++) {
            int val = prices[i];
            if (min > val) {
                min = val;
            } else {
                if (maxProfit < val - min) {
                    maxProfit = val - min;
                }
            }
        }
        return maxProfit;
    }
}
