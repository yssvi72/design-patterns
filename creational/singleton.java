/* Singleton pattern means that only a single instance of class can be created ---> it is used in scenarios wherein a single object instance needs to be used across all modules like in */
//Option 1 Synchronized Method (Simple but slower)
public class Singleton {

    private static volatile Singleton instance;  //<-----------------why volatile , without it-- JVM may reorder instructions or Another thread may see partially constructed object

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) { 
            synchronized (Singleton.class) { //<--------------Best for interviews double checking --synchronized to avoid race condition
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

//Option 2 to write singleton pattern ---> Eager Initialization(object created when class loads), thread safe handled by JVM 
public class Singleton {
    // Eager initialization
    private static final Singleton INSTANCE = new Singleton();
    // Private constructor prevents external instantiation
    private Singleton() {
        System.out.println("Singleton instance created");
    }
    // Global access point
    public static Singleton getInstance() {   // <----------------------------------------main part
        return INSTANCE;
    }
    public void showMessage() {
        System.out.println("Hello from Singleton");
    }
}
//Option 3: Using ENUM clas which is best 
public enum Singleton {

    INSTANCE;

    Singleton() {
        System.out.println("Enum Singleton instance created");
    }

    public void showMessage() {
        System.out.println("Hello from Enum Singleton");
    }
}
//------------------------------------------------------------------------------------Usecases-----------------------------------------------------------------------------------------------------------------------
/*
1. DB connection wherein only single connection pool will store all data ----You should not create multiple DB pools. Connection pool should be shared across the entire application.
Used in:
HikariCP in Spring Boot
Custom DBPool class
*/
public class DBConnectionPool {
    private static DBConnectionPool instance;
    private DBConnectionPool() {
        // initialize pool
    }
    public static DBConnectionPool getInstance() {
        if (instance == null) {
            instance = new DBConnectionPool();
        }
        return instance;
    }
}
/*
2. Logger wherein single logger will store all logs at one place. Logging framework should not create logger object again and again.
Used in:
Log4j
SLF4J
They internally manage logger instances in singleton/factory style.
*/
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class Logger {

    private static volatile Logger instance;
    private PrintWriter writer;

    // Private constructor
    private Logger() {
        try {
            writer = new PrintWriter(new FileWriter("application.log", true), true);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize logger", e);
        }
    }

    // Double Checked Locking
    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {  //<-------------------Important
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    public void log(String level, String message) {
        String logMessage = String.format(
                "%s [%s] %s",
                LocalDateTime.now(),
                level,
                message
        );
        writer.println(logMessage);
    }

    public void close() {
        writer.close();
    }
}

/*
3.Configuration Manager
If your project reads:application.properties,environment variables,secret keys,JWT configs
You don't want multiple objects reading same file again and again.
*/
public class ConfigManager {
    private static final ConfigManager INSTANCE = new ConfigManager();
    private ConfigManager() {}
    public static ConfigManager getInstance() {
        return INSTANCE;
    }
}

/*
4.Cache manager
If you're caching:User sessions,JWT blacklist,OTP attempts,API responses.You need one global cache instance.Eg: Redis client , In-memory HashMap cache
*/
import java.util.concurrent.ConcurrentHashMap;
public class CacheManager {
    private static final CacheManager INSTANCE = new CacheManager();
    private ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
    private CacheManager() {}
    public static CacheManager getInstance() {
        return INSTANCE;
    }
    public void put(String key, Object value) {
        cache.put(key, value);
    }
    public Object get(String key) {
        return cache.get(key);
    }
    public void remove(String key) {
        cache.remove(key);
    }
}

/*
5.Kafka Manager: Kafka are expensive to create. You should:Create once,Reuse everywhere,Otherwise:Memory leak,Connection overhead,Performance drop
*/
import org.apache.kafka.clients.producer.KafkaProducer;
import java.util.Properties;

public class KafkaManager {
    private static final KafkaManager INSTANCE = new KafkaManager();
    private KafkaProducer<String, String> producer;
    private KafkaManager() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", 
                  "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", 
                  "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
    }
    public static KafkaManager getInstance() {
        return INSTANCE;
    }
    public KafkaProducer<String, String> getProducer() {
        return producer;
    }
}

/*
6.Thread Pool / ExecutorService
ExecutorService should be singleton.If created multiple times:Too many threads, System crash
*/
ExecutorService executor = Executors.newFixedThreadPool(10);

/*
In:
Spring Boot
All @Service, @Repository, @Component beans are:
✅ Singleton by default
@Service
public class UserService {
}
Spring creates:
Only one UserService object
Injects everywhere
That is Singleton Pattern in action.
  */


