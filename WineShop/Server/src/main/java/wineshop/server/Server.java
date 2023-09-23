package wineshop.server;

import wineshop.model.GlobalVarAndUtilities;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class of the main server of the application
 */
public class Server implements Runnable {
    /**
     * Max pool of the server
     */
    private static final int MAXPOOL = 100;
    /**
     * Idle time of the server
     */
    private static final long IDLETIME = 5000;
    /**
     * Server socket of the server
     */
    private ServerSocket srvSock;
    /**
     * Thread pool executor of the server
     */
    private ThreadPoolExecutor pool;
    /**
     * Socket of the server
     */
    private Socket s;

    /**
     * Constructor to instantiate a new server
     * @throws IOException
     */
    public Server() throws IOException
    {
        this.srvSock = new ServerSocket(GlobalVarAndUtilities.SPORT);
    }

    /**
     * Server activation
     */
    public void run()
    {
        this.pool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), MAXPOOL, IDLETIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        boolean error = false;

        do
        {
            try
            {
                s = srvSock.accept();
                this.pool.execute(new ServerThread(this, s));
            }
            catch (Exception e)
            {
                error = true;
            }
        } while (!error);
    }


    /**
     * Server deactivation
     */
    public void stop()
    {
        try
        {
            this.srvSock.close();
            this.pool.shutdown();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
