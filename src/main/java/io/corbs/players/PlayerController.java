package io.corbs.players;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

class Player {
    private String id;
    private String firstName;
    private String lastName;
    private String bats;
    private String fields;
    private String firstGame;
    private String lastGame;

    public Player(String id,
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

    public Response(Object data) {
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
}

class PlayersDB {
    public static Map<String, Player> getAll() {

        Map<String, Player> players = new HashMap<>();
        players.put("babe", new Player("ruthba01",
                "George Herman", "Ruth",
                "Left", "Left",
                "July 11, 1914, for the Boston Red Sox",
                "May 30, 1935, for the Boston Braves"));
        players.put("jackson", new Player("jacksjo01",
                "Shoeless Joe", "Jackson",
                "Left", "Right",
                "August 25, 1908, for the Philadelphia Athletics",
                "September 27, 1920, for the Chicago White Sox"));
        players.put("rose", new Player("rosepe01",
                "Pete", "Rose",
                "Switch", "Right",
                "April 8, 1963, for the Cincinnati Reds",
                "August 17, 1986, for the Cincinnati Reds"));
        players.put("oddibe", new Player("mcdowod01",
                "Oddibe", "McDowell",
                "Left", "Left",
                "May 19, 1985, for the Texas Rangers",
                "August 10, 1994, for the Texas Rangers"));
        players.put("eddie", new Player("gaedeed01",
                "Eddie", "Gaedel",
                "Right", "Left",
                "August 19, 1951, for the St. Louis Browns",
                "August 19, 1951, for the St. Louis Browns"));
        players.put("ty", new Player("cobbty01",
                "Ty", "Cobb",
                "Left", "Right",
                "August 30, 1905, for the Detroit Tigers",
                "September 11, 1928, for the Philadelphia Athletics"));

        return players;
    }
}

@RestController
public class PlayerController {

    @Value("${server.port}")
    private Integer port;

    @RequestMapping("/player/{id}")
    public Response getPlayer(@PathVariable String id) throws NotFoundException {

        Player player = lookupPlayer(id);

        if(port != null) {
            Response response = new Response(player);
            response.add("port", port);
            return response;
        }

        return new Response(player);
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

    public NotFoundException(String s) {
        super(s);
    }
}
