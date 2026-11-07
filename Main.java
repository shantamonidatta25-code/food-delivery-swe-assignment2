import java.util.ArrayList;
import java.util.List;

interface OrderWatcher {
    void update(String status);
}

class Order {
    private String status;
    private final List<OrderWatcher> watchers = new ArrayList<>();

    public void addWatcher(OrderWatcher watcher) {
        watchers.add(watcher);
    }

    public void removeWatcher(OrderWatcher watcher) {
        watchers.remove(watcher);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("Order is now: " + newStatus);
        // Copy so a watcher can remove itself during notification.
        for (OrderWatcher watcher : new ArrayList<>(watchers)) {
            watcher.update(newStatus);
        }
        System.out.println();
    }
}

class SMSNotifier implements OrderWatcher {
    private final String customerName;

    public SMSNotifier(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public void update(String status) {
        System.out.println("  SMS to " + customerName + ": your order is now " + status);
    }
}

class KitchenScreen implements OrderWatcher {
    private final Order order;

    public KitchenScreen(Order order) {
        this.order = order;
    }

    @Override
    public void update(String status) {
        if ("Delivered".equals(status)) {
            // Kitchen is finished with this order, so it stops watching.
            order.removeWatcher(this);
            return;
        }
        System.out.println("  Kitchen screen: " + status);
    }
}

public class FoodDeliveryApp {
    public static void main(String[] args) {
        Order order = new Order();

        order.addWatcher(new SMSNotifier("Rahim"));
        order.addWatcher(new KitchenScreen(order));

        order.setStatus("Cooking");
        order.setStatus("Out for delivery");
        order.setStatus("Delivered");
    }
}
