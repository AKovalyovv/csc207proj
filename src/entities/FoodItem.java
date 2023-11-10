package entities;
import java.util.Calendar;
public class FoodItem {
    private final String name;
    private final Calendar expirationDate;
    private Float amount;

    public FoodItem(String name, int year, int month, int day, float amount) {
        // January is considered as month 0
        this.name = name;
        this.expirationDate = Calendar.getInstance();
        this.expirationDate.set(Calendar.YEAR, year);
        this.expirationDate.set(this.expirationDate.MONTH, month - 1);
        this.expirationDate.set(this.expirationDate.DAY_OF_MONTH, day);
        this.amount = amount;
    }

    public String getExpirationDate() {
        int year = this.expirationDate.get(this.expirationDate.YEAR);
        int month = this.expirationDate.get(this.expirationDate.MONTH) + 1;
        int day = this.expirationDate.get(this.expirationDate.DAY_OF_MONTH);
        return year + "/" + month + "/" + day;
    }

    public Calendar getCalendarObject() {
        return expirationDate;
    }

    public String getName() {
        return name;
    }

    public Float getAmount() {
        return this.amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public static void main(String[] args) {
        FoodItem test = new FoodItem("pasta", 2023, 12, 5, 2);
        System.out.println(test.getExpirationDate());
    }
}