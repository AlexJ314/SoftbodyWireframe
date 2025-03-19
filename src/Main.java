import java.io.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;

public class Main {
  private static Wireframe wireframe;
  private static Screen screen;
  private static Reader reader;
  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public static void main(String[] args) {
    String filename = "filename";
    if (args.length > 0) {
      filename = args[0];
    } else {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("Enter filename: ");
      try {
        filename = br.readLine();
      } catch (IOException e) {
        System.out.println("Invalid filename");
        System.exit(1);
      }
    }
    wireframe = new Wireframe();
    reader = new Reader(filename, wireframe);
    screen = new Screen(wireframe);

    final Runnable ticker = new Runnable() {
      public void run() {
        screen.tick();
      }
    };
    scheduler.scheduleAtFixedRate(ticker, Constants.delay, Constants.delay, TimeUnit.MILLISECONDS);
    /*
    Instant lastTick = Instant.now();
    while (true) {
      if (Instant.now().minusMillis(Constants.delay).isAfter(lastTick)) {
        synchronized(Constants.lock) {
          // Make some number of threads and call tick
          // Tick takes an argument for how many points to deal with
          lastTick = Instant.now();
          screen.tick();
        }
      }
    }
    */
  }
}
