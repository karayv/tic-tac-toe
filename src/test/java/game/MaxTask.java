package game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

class MaxTask {

    @Test
    void one() {
        HashSet<String> prefs = new HashSet<>();
        prefs.add("coffee");
        String[][] buildings = { { "coffee" } };
        assertEquals(0, optimalBuilding(prefs, buildings));
    }

    @Test
    void none() {
        HashSet<String> prefs = new HashSet<>();
        prefs.add("tea");
        String[][] buildings = { { "coffee" } };
        assertEquals(-1, optimalBuilding(prefs, buildings));
    }

    @Test
    void noBuildings() {
        HashSet<String> prefs = new HashSet<>();
        prefs.add("coffee");
        String[][] buildings = {};
        assertEquals(-1, optimalBuilding(prefs, buildings));
    }

    @Test
    void test() {
        HashSet<String> prefs = new HashSet<>();
        prefs.add("coffee");
        prefs.add("gym");
        prefs.add("grocery");
        
        String[][] buildings = {
                {"gym", "x", "grocery"}, 
                {"a", "b"}, 
                {}, 
                {"grocery", "gym"}, 
                {"gym"}, 
                {"x", "coffee"}, 
                {"gym", "coffee"}};
        
        assertEquals(4, optimalBuilding(prefs, buildings));
    }

    private int optimalBuilding(HashSet<String> prefs, String[][] buildings) {
        int[] recRes = optimalBuildingRec(0, prefs, buildings);
        
        return recRes == null ? -1 : (recRes[0] + recRes[1]) / 2;
    }

    private int[] optimalBuildingRec(int pos, HashSet<String> prefs, String[][] buildings) {
        HashSet<String> pCopy = new HashSet<>(prefs);
        
        for (int i = pos; i < buildings.length; i++) {
            if (retain(pCopy, buildings[i])) {
                int j = i + 1;
                for (; j < buildings.length && !pCopy.isEmpty() ; j++) {
                    retain(pCopy, buildings[j]);
                }
                if (pCopy.isEmpty()) {
                    int[] tmpRes = optimalBuildingRec(i + 1, prefs, buildings);
                    
                    return (tmpRes == null) || (j - i < tmpRes[1] - tmpRes[0]) ? new int[] { i, j } : tmpRes;
                }
            }
        }
        return null;
    }

    private boolean retain(HashSet<String> pCopy, String[] prefs) {
        boolean res = false;
        for (String pref : prefs) {
            res = res || pCopy.contains(pref);
            pCopy.remove(pref);
        }
        return res;
    }

}
