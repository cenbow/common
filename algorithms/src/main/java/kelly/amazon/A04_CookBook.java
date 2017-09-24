package kelly.amazon;

import java.util.*;

/**
 * Created by kelly.li on 17/8/27.
 */
public class A04_CookBook {

    public String[][] recommendation(String[][] menus, String[][] personPreferences) {
        Map<String, Set<String>> menuMap = new HashMap<String, Set<String>>();
        Set<String> all = new HashSet<String>();
        for (int i = 0; i < menus.length; i++) {
            String key = menus[i][0];
            String value = menus[i][1];
            if (menuMap.containsKey(key)) {
                menuMap.get(key).add(value);
            } else {
                Set<String> set = new HashSet<String>();
                set.add(value);
                menuMap.put(key, set);
            }
            all.add(value);
        }
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        int size = 0;
        for (int i = 0; i < personPreferences.length; i++) {
            String key = personPreferences[i][0];
            String value = personPreferences[i][1];
            Set<String> set = null;
            if ("*".equals(value)) {
                set = all;
            } else {
                set = menuMap.get(value);
            }
            size += set.size();
            result.put(key, set);
        }
        String[][] arr = new String[size][];
        int index = 0;
        for (Map.Entry<String, Set<String>> mapEntry : result.entrySet()) {
            String key = mapEntry.getKey();
            for (String str : mapEntry.getValue()) {
                arr[index++] = new String[]{key, str};
            }
        }
        return arr;
    }

    public static void main(String[] args) {
        A04_CookBook cookBook = new A04_CookBook();
        String[][] menus = new String[][]{{"Italian", "Pizza"}, {"Italian", "Pasta"}, {"American", "Burger"}};
        //      String[][] personPreferences = new String[][]{{"Peter", "Italian"}, {"Adam", "American"}};

        String[][] personPreferences = {{"Peter", "*"}};
        String[][] result = cookBook.recommendation(menus, personPreferences);
        for (String[] s : result) {
            System.out.println(Arrays.toString(s));
        }
    }
}
