package info.tregmine.web;

import info.tregmine.Tregmine;
import info.tregmine.WebHandler;
import info.tregmine.api.GenericPlayer;
import org.eclipse.jetty.server.Request;
import org.json.JSONException;
import org.json.JSONWriter;

import java.io.PrintWriter;
import java.util.List;

public class PlayerListAction implements WebHandler.Action {
    private List<GenericPlayer> players;

    public PlayerListAction() {
    }

    @Override
    public void generateResponse(PrintWriter writer) throws WebHandler.WebException {
        try {
            JSONWriter json = new JSONWriter(writer);
            json.array();
            for (GenericPlayer player : players) {
                Tregmine.LOGGER.info("Web: " + player.getName() + " - ID: " + player.getId());
                json.object().key("id").value(player.getId()).key("name").value(player.getName()).key("rank")
                        .value(player.getRank().toString()).key("softwarned")
                        .value(player.hasFlag(GenericPlayer.Flags.SOFTWARNED)).key("hardwarned")
                        .value(player.hasFlag(GenericPlayer.Flags.HARDWARNED)).key("hidden")
                        .value(player.hasFlag(GenericPlayer.Flags.INVISIBLE)).key("playtime")
                        .value(player.getTimeOnline()).endObject();
            }
            json.endArray();

            writer.close();
        } catch (JSONException e) {
            throw new WebHandler.WebException(e);
        }
    }

    @Override
    public void queryGameState(Tregmine tregmine) {
        players = tregmine.getOnlinePlayers();
    }

    public static class Factory implements WebHandler.ActionFactory {
        public Factory() {
        }

        @Override
        public WebHandler.Action createAction(Request request) {
            return new PlayerListAction();
        }

        @Override
        public String getName() {
            return "/playerlist";
        }
    }
}
