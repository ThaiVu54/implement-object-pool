import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TaxiPool {
    private static final long EXPIRED_TIME_IN_MILISECOND = 1200;
    private static final int NUMBER_OF_TAXI = 4;
    private final List<Taxi> available = Collections.synchronizedList(new ArrayList<>()); //ds taxi dang cho phuc vu
    private final List<Taxi> inUse = Collections.synchronizedList(new ArrayList<>()); //ds taxi ban phuc vu
    private final AtomicInteger count = new AtomicInteger(0);
    private final AtomicBoolean waiting = new AtomicBoolean(false);

    public synchronized void release(Taxi taxi) { // sau khi phuc vu
        inUse.remove(taxi); //inUse taxi dang ban phuc vu
        available.add(taxi); //available taxi dang cho phuc vu
        System.out.println(taxi.getName() + "taxi free");
    }

    /*
    Taxi: đại diện cho một chiếc taxi, là một class định nghĩa các thuộc tính và phương thức của một taxi.
    TaxiPool: Đại diện cho công ty taxi, có:
    Phương thức getTaxi() : để lấy về một thể hiện Taxi đang ở trạng thái rảnh, có thể throw ra một exception nếu chờ lâu mà không lấy được thể hiện.
    Phương thức release() : để trả thể hiện Taxi về Pool sau khi đã phục vụ xong.
    Thuộc tính available : lưu trữ danh sách Taxi rãnh, đang chờ phục vụ.
    Thuộc tính inUse : lưu trữ danh sách Taxi đang bận phục vụ.
    TaxiThread: đại diện cho khách hàng sử dụng dịch vụ Taxi, mô phỏng việc gọi, chở và trả khách.
     */
    public synchronized Taxi getTaxi() {
        if (!available.isEmpty()) {
            Taxi taxi = available.get(0);
            inUse.add(taxi);
            return taxi;
        }
        if (count.get() == NUMBER_OF_TAXI){
            this.waitingUntilTaxiAvailable();
            return this.getTaxi();
        }
        Taxi taxi = this.createTaxi();
        inUse.add(taxi);
        return taxi;

    }

    private void waitingUntilTaxiAvailable() {
        if (waiting.get()){
            waiting.set(false);
            throw new TaxiNotFoundException("No taxi avaiable");
        }
    }

    private Taxi createTaxi(){
        waiting(200);
        Taxi taxi = new Taxi("taxi"+count.incrementAndGet());
        System.out.println(taxi.getName()+"isCreated");
        return taxi;
    }

    private void waiting(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

    }
}
