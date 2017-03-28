package info.tregmine.web;

import info.tregmine.Tregmine;
import info.tregmine.WebHandler;
import info.tregmine.api.TregminePlayer;
import org.bukkit.ChatColor;
import org.eclipse.jetty.server.Request;
import org.json.JSONException;
import org.json.JSONWriter;

import java.io.PrintWriter;

public class PushNotificationAction implements WebHandler.Action {
    private int sendTo;
    private int sentFrom;
    private String type;
    private boolean status;
    private String error;
    public PushNotificationAction(int sendTo, int sentFrom, String type) {
        this.sendTo = sendTo;
        this.sentFrom = sentFrom;
        this.type = type;

        this.status = true;
        this.error = null;
    }

    @Override
    public void generateResponse(PrintWriter writer) throws WebHandler.WebException {
        try {
            JSONWriter json = new JSONWriter(writer);
            json.object().key("status").value(status ? "ok" : "error").key("error").value(error).endObject();

            writer.close();
        } catch (JSONException e) {
            throw new WebHandler.WebException(e);
        }
    }

    @Override
    public void queryGameState(Tregmine tregmine) {
        TregminePlayer subject = tregmine.getPlayer(sendTo);
        if (subject == null) {
            status = false;
            error = "Subject not found.";
            System.out.println("PNA-WEB-ERR: " + error);
            return;
        }
        TregminePlayer issuer = tregmine.getPlayer(sentFrom);
        if (issuer == null) {
            issuer = tregmine.getPlayerOffline(sentFrom);
        }
        String message;
        if (type.trim().equalsIgnoreCase("mail")) {
            message = ChatColor.AQUA + "You have a message from " + issuer.getChatNameNoHover() + ChatColor.AQUA
                    + "! Do /mail read to view it.";
        } else {
            message = "Generic push notification from " + issuer.getChatNameNoHover() + ChatColor.WHITE + ".";
        }

        subject.sendStringMessage(message);
        Tregmine.LOGGER.info("Push notification issued by " + issuer.getName() + "; sent to " + subject.getName()
                + "; type=" + type);
    }

    public static class Factory implements WebHandler.ActionFactory {
        public Factory() {
        }

        @Override
        public WebHandler.Action createAction(Request request) throws WebHandler.WebException {
            try {
                int sendTo = Integer.parseInt(request.getParameter("pushTo"));
                int sentFrom = Integer.parseInt(request.getParameter("pushFrom"));
                String message = request.getParameter("type");
                return new PushNotificationAction(sendTo, sentFrom, message);
            } catch (NullPointerException e) {
                throw new WebHandler.WebException(e);
            } catch (NumberFormatException e) {
                throw new WebHandler.WebException(e);
            }
        }

        @Override
        public String getName() {
            return "/push";
        }
    }
}
