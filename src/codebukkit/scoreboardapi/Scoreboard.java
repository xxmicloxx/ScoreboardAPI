package codebukkit.scoreboardapi;

import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_7_R1.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_7_R1.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_7_R1.PlayerConnection;

import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: ml Date: 19.03.13 Time: 19:57 To change this template use File | Settings | File Templates.
 */
public class Scoreboard {
    Scoreboard(String name, int priority, ScoreboardAPI plugin) {
        this.name = name;
        this.priority = priority;
        this.plugin = plugin;
    }

    public enum Type {
        PLAYER_LIST, SIDEBAR
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
        for (Player p : players) {
            updatePosition(p);
        }
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public String getScoreboardName() {
        return displayName;
    }

    public void setScoreboardName(String displayName) {
        this.displayName = displayName;
        PacketPlayOutScoreboardScore pack = new PacketPlayOutScoreboardScore();
        setReflectionValue(pack.getClass(), "a", name);
        setReflectionValue(pack.getClass(), "b", displayName);
        setReflectionValue(pack.getClass(), "c", 2);
        for (Player p : players) {
            if (!isUnique(p)) {
                continue;
            }
            sendPacket(p, pack);
        }

    }

    public void sendPacket(Player p, Packet packet) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    public void setReflectionValue(Class<?> clazz, String to_get, Object value) {
        try {
            Field f = clazz.getDeclaredField(to_get);
            f.setAccessible(true);
            f.set(null, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setItem(String name2, int value) {
        items.put(name2, value);
        PacketPlayOutScoreboardScore pack = new PacketPlayOutScoreboardScore();
        setReflectionValue(pack.getClass(), "a", name2);
        setReflectionValue(pack.getClass(), "b", name);
        setReflectionValue(pack.getClass(), "c", value);
        setReflectionValue(pack.getClass(), "d", 0);

        for (Player p : players) {
            if (!isUnique(p)) {
                continue;
            }
            sendPacket(p, pack);
        }
    }

    public void removeItem(String name2) {
        if (items.remove(name2) != null) {
            PacketPlayOutScoreboardScore pack = new PacketPlayOutScoreboardScore();
            setReflectionValue(pack.getClass(), "a", name2);
            setReflectionValue(pack.getClass(), "b", name);
            setReflectionValue(pack.getClass(), "c", 0);
            setReflectionValue(pack.getClass(), "d", 1);
            for (Player p : players) {
                if (!isUnique(p)) {
                    continue;
                }
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(pack);
            }
        }
    }

    public boolean hasPlayerAdded(Player p) {
        return players.contains(p);
    }

    public List<Player> getAddedPlayers() {
        return players;
    }

    public void showToPlayer(Player p, boolean show) {
        if (show) {
            if (!players.contains(p)) {
                players.add(p);
                plugin.updateForPlayer(p);
            }
        } else {
            if (players.remove(p)) {
                PacketPlayOutScoreboardObjective pack = new PacketPlayOutScoreboardObjective();
                setReflectionValue(pack.getClass(), "a", name);
                setReflectionValue(pack.getClass(), "b", "");
                setReflectionValue(pack.getClass(), "c", 1);
                sendPacket(p, pack);
                plugin.updateForPlayer(p);
            }
        }
    }

    public void showToPlayer(Player p) {
        showToPlayer(p, true);
    }

    public void stopShowingAllPlayers() {
        for (Player p : players) {
            showToPlayer(p, false);
        }
    }

    private void updatePosition(Player p) {
        if (!isUnique(p)) {
            return;
        }
        PacketPlayOutScoreboardDisplayObjective pack2 = new PacketPlayOutScoreboardDisplayObjective();
        setReflectionValue(pack2.getClass(), "a", type.ordinal());
        setReflectionValue(pack2.getClass(), "b", name);
        sendPacket(p, pack2);
    }

    public void checkIfNeedsToBeDisabledForPlayer(Player p) {
        if (!players.contains(p)) {
            return;
        }
        if (!isUnique(p)) {
            PacketPlayOutScoreboardObjective pack = new PacketPlayOutScoreboardObjective();
            setReflectionValue(pack.getClass(), "a",name);
            setReflectionValue(pack.getClass(), "b", displayName);
            setReflectionValue(pack.getClass(), "c", 1);
            sendPacket(p, pack);
        }
    }

    public void checkIfNeedsToBeEnabledForPlayer(Player p) {
        if (!players.contains(p)) {
            return;
        }
        if (isUnique(p)) {
            PacketPlayOutScoreboardObjective pack = new PacketPlayOutScoreboardObjective();
            setReflectionValue(pack.getClass(), "a",name);
            setReflectionValue(pack.getClass(), "b", displayName);
            setReflectionValue(pack.getClass(), "c", 0);
            sendPacket(p, pack);
            for (String name2 : items.keySet()) {
                Integer valObj = items.get(name2);
                if (valObj == null) {
                    continue;
                }
                int val = valObj.intValue();
                PacketPlayOutScoreboardScore pack2 = new PacketPlayOutScoreboardScore();
                setReflectionValue(pack.getClass(), "a",name2);
                setReflectionValue(pack.getClass(), "b", name);
                setReflectionValue(pack.getClass(), "c", val);
                setReflectionValue(pack.getClass(), "d", 0);
                sendPacket(p, pack2);
            }
            updatePosition(p);
        }

    }

    private boolean isUnique(Player p) {
        int myPos = 0;
        for (int i = 0; i < plugin.getScoreboards().size(); i++) {
            if (plugin.getScoreboards().get(i) == this) {
                myPos = i;
                break;
            }
            Scoreboard s = plugin.getScoreboards().get(i);
            if (s != this && s.hasPlayerAdded(p) && s.getType() == type && (s.getPriority() > priority || (i > myPos && s.getPriority() == priority))) {
                return false;
            }
        }
        return true;
    }

    private List<Player> players = new LinkedList<Player>();

    private HashMap<String, Integer> items = new HashMap<String, Integer>();

    private Type type = Type.SIDEBAR;

    private String name;

    private String displayName = "§4Not initialized";

    private int priority = 10;

    private ScoreboardAPI plugin;
}
