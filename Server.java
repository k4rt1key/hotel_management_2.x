import Database.Database;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server
{
    private static final int PORT = 8081;

    private static ServerSocket server;

    private final static ExecutorService threadPool = Executors.newFixedThreadPool(8);

    private static boolean running = true;

    public static void main(String[] args)
    {
        try
        {
            Database.populateSeedData();

            Runtime.getRuntime().addShutdownHook(new Thread(Server::shutdown));

            server = new ServerSocket(PORT);

            server.setReuseAddress(true);

            System.out.println("Server started on port [" + PORT + "]");

            acceptConnections();
        }
        catch (Exception e)
        {
            System.out.println("Server error -> " + e.getMessage());
        }

    }

    private static void acceptConnections()
    {
        try
        {
            while (running)
            {
                var client = server.accept();

                System.out.println("Connected IP [" + client.getInetAddress().getHostAddress() + "] PORT [" + client.getPort() + "]");

                threadPool.execute(new Worker(client));
            }
        }
        catch (Exception e)
        {
            if (running)
            {
                System.out.println("Server error -> " + e.getMessage());
            }
        }
    }

    private static void shutdown()
    {
        running = false;

        try
        {
            if (server != null && !server.isClosed())
            {
                server.close();
            }

            if (!threadPool.isShutdown())
            {
                threadPool.shutdown();
            }

            System.out.println("Server shutdown successfully");
        }
        catch (Exception e)
        {
            System.out.println("Server error during shutdown -> " + e.getMessage());
        }
    }

}
