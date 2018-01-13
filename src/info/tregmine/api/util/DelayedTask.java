package info.tregmine.api.util;

public class DelayedTask {

    public static void setTimeout(Runnable runnable, Number delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay.intValue());
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }

}
