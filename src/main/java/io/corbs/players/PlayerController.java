package io.corbs.players;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

class Player {
    private String id;
    private String firstName;
    private String lastName;
    private String bats;
    private String fields;
    private String firstGame;
    private String lastGame;

    Player(String id,
        String firstName, String lastName,
        String bats, String fields,
        String firstGame, String lastGame) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bats = bats;
        this.fields = fields;
        this.firstGame = firstGame;
        this.lastGame = lastGame;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBats() {
        return bats;
    }

    public String getFields() {
        return fields;
    }

    public String getFirstGame() {
        return firstGame;
    }

    public String getLastGame() {
        return lastGame;
    }
}

class Response {

    @JsonInclude(Include.NON_EMPTY)
    private Map<String, Object> meta;

    @JsonProperty
    private Object data;

    Response() { }

    Response(Object data) {
        this.data = data;
        this.meta = new HashMap<>();
    }

    public void add(String key, Object value) {
        this.meta.put(key, value);
    }

    public void remove(String key) {
        this.meta.remove(key);
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public Object getData() {
        return data;
    }

    Response meta(String key, Object value) {
        this.meta.put(key, value);
        return this;
    }

    boolean isEmpty() {
        return data == null && meta == null;
    }
}

class PlayersDB {
    static Map<String, Player> getAll() {

        Map<String, Player> players = new HashMap<>();
        players.put("ruthba01", new Player("ruthba01",
                "George Herman", "Ruth",
                "Left", "Left",
                "July 11, 1914, for the Boston Red Sox",
                "May 30, 1935, for the Boston Braves"));
        players.put("jacksjo01", new Player("jacksjo01",
                "Shoeless Joe", "Jackson",
                "Left", "Right",
                "August 25, 1908, for the Philadelphia Athletics",
                "September 27, 1920, for the Chicago White Sox"));
        players.put("rosepe01", new Player("rosepe01",
                "Pete", "Rose",
                "Switch", "Right",
                "April 8, 1963, for the Cincinnati Reds",
                "August 17, 1986, for the Cincinnati Reds"));
        players.put("mcdowod01", new Player("mcdowod01",
                "Oddibe", "McDowell",
                "Left", "Left",
                "May 19, 1985, for the Texas Rangers",
                "August 10, 1994, for the Texas Rangers"));
        players.put("gaedeed01", new Player("gaedeed01",
                "Eddie", "Gaedel",
                "Right", "Left",
                "August 19, 1951, for the St. Louis Browns",
                "August 19, 1951, for the St. Louis Browns"));
        players.put("cobbty01", new Player("cobbty01",
                "Ty", "Cobb",
                "Left", "Right",
                "August 30, 1905, for the Detroit Tigers",
                "September 11, 1928, for the Philadelphia Athletics"));
        players.put("rauchjo01", new Player("rauchjo01",
                "Jon", "Rauch",
                "Right", "Right",
                "April 2, 2002, for the Chicago White Sox",
                "May 17, 2013, for the Miami Marlins"));
        players.put("bellbu01", new Player("bellbu01",
                "Buddy", "Bell",
                "Right", "Right",
                "April 15, 1972, for the Cleveland Indians",
                "June 17, 1989, for the Texas Rangers"));

        return players;
    }

    static void add(String playerId) {
        // TODO implement this
        System.out.println("TODO implement add(playerId)");
    }

    static void remove(String playerId) {
        // TODO implement this
        System.out.println("TODO implement remove(playerId)");
    }

    static int size() {
        return getAll().size();
    }
}

@RestController
public class PlayerController {

    @Value("${server.port}")
    private Integer port;

    private Random randomizer = new Random();

    @RequestMapping("/player/{playerId}")
    public Response getPlayer(@PathVariable String playerId) throws NotFoundException {
        Player player = lookupPlayer(playerId);
        return newResponse(player);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/players/random")
    public Response getRandomPlayer() {
        Integer i = randomizer.nextInt(PlayersDB.size());
        List<Player> players = new ArrayList(PlayersDB.getAll().values());
        return newResponse(players.get(i));
    }

    /**
     * Add a player into this client's memory
     * @param playerId
     */
    @RequestMapping(method = RequestMethod.POST, path="/players/{playerId}")
    void addPlayer(@PathVariable String playerId) {
        if(!StringUtils.isEmpty(playerId)) {
            PlayersDB.add(playerId);
        }
    }

    /**
     * Remove a player from this client's memory
     * @param playerId
     */
    @RequestMapping(method = RequestMethod.DELETE, path="/players/{playerId}")
    void removePlayer(@PathVariable String playerId) {
        if(!StringUtils.isEmpty(playerId)) {
            PlayersDB.remove(playerId);
        }
    }

    @RequestMapping("/players")
    public Response getPlayers() {
        return newResponse(PlayersDB.getAll().values());
    }

    @RequestMapping("/ip")
    public List<String> getIp() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();
            return new ArrayList<>(Arrays.asList(ip.toString(), hostname));
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }

        return Collections.emptyList();
    }

    // shhhh this is private

    private Response newResponse(Object object) {
        if(object == null) {
            return new Response().meta("port", port).meta("ip", getIp());
        }
        return new Response(object).meta("port", port).meta("ip", getIp());
    }

    private Player lookupPlayer(String id) throws NotFoundException {

        for (Map.Entry<String, Player> player : PlayersDB.getAll().entrySet()) {
            if(id.equals(player.getKey())) {
                return player.getValue();
            }
        }

        throw new NotFoundException("Player " + id + " not found");
    }
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException extends Exception {

    NotFoundException(String s) {
        super(s);
    }
}
