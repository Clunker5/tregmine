package info.tregmine.web;

import info.tregmine.Tregmine;
import info.tregmine.WebHandler;
import org.bukkit.Server;
import org.eclipse.jetty.server.Request;
import org.json.JSONException;
import org.json.JSONWriter;

import java.io.PrintWriter;

public class VersionAction implements WebHandler.Action {
    private String version;

    public VersionAction() {
    }

    @Override
    public void generateResponse(PrintWriter writer) throws WebHandler.WebException {
        try {
            JSONWriter json = new JSONWriter(writer);
            json.object().key("version").value(version).endObject();

            writer.close();
        } catch (JSONException e) {
            throw new WebHandler.WebException(e);
        }
    }

    @Override
    public void queryGameState(Tregmine tregmine) {
        Server server = tregmine.getServer();
        version = server.getName() + " " + server.getVersion();
    }

    public static class Factory implements WebHandler.ActionFactory {
        public Factory() {
        }

        @Override
        public WebHandler.Action createAction(Request request) {
            return new VersionAction();
        }

        @Override
        public String getName() {
            return "/version";
        }
    }
}
