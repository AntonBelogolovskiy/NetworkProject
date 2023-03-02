import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyReader implements Runnable {
    private final BufferedReader reader;
    private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
    private boolean closed = false;

    public MyReader(BufferedReader reader) {
        this.reader = reader;
    }

    public void run() {
        String line;
        while(true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            queue.add(line);
        }
        closed = true;
    }

    // Returns true iff there is at least one line on the queue
    public boolean ready() {
        return(queue.peek() != null);
    }

    // Returns true if the underlying connection has closed
    // Note that there may still be data on the queue!
    public boolean isClosed() {
        return closed;
    }

    // Get next line
    // Returns null if there is none
    // Never blocks
    public String readLine() {
        return(queue.poll());
    }
}