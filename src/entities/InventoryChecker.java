package entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.PriorityQueue;

public class InventoryChecker {
    public List<FoodItem> weekCheck(Inventory userInventory) {
        PriorityQueue<FoodItem> inv = userInventory.getQueue();
        List<FoodItem> res = new ArrayList<>();
        Calendar plusWeek = Calendar.getInstance();
        plusWeek.add(Calendar.DAY_OF_MONTH, 7);

        for (FoodItem item: inv) {
            if (item.getCalendarObject().compareTo(plusWeek) < 0) {
                res.add(item);
            } else {
                break;
            }
        }

        return res;
    }
}
