package de.presti.ree6.webinterface.main;

import de.presti.ree6.webinterface.json.JSONApi;
import de.presti.ree6.webinterface.json.Requests;
import de.presti.ree6.webinterface.sql.SQLConnector;
import de.presti.ree6.webinterface.sql.SQLWorker;
import de.presti.ree6.webinterface.utils.Config;
import de.presti.ree6.webinterface.utils.Crypter;
import fi.iki.elonen.NanoHTTPD;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Main extends NanoHTTPD {

    public static SQLConnector sqlConnector;
    public static SQLWorker sqlWorker;
    public static Config config;

    public Main() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }

    public static void main(String[] args) {
        try {

            config = new Config();

            sqlConnector = new SQLConnector(config.getConfig().getString("mysql.user"), config.getConfig().getString("mysql.pw"), config.getConfig().getString("mysql.host"), config.getConfig().getString("mysql.db"), config.getConfig().getInt("mysql.port"));

            sqlWorker = new SQLWorker();
            new Main();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    String head = "<html lang=\"de\">\n" +
            "   <head>\n" +
            "      <meta charset=\"utf-8\">\n" +
            "      <title>Ree6</title>\n" +
            "      <link rel=\"icon\" type=\"image/png\" href=\"https://utils.ree6.de/img/ree.png\">\n" +
            "      <link rel=\"stylesheet\" href=\"https://utils.ree6.de/css/bootstrap.css\">\n" +
            "      <link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Montserrat:400,700\">\n" +
            "      <link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic\">\n" +
            "      <link rel=\"stylesheet\" href=\"https://use.fontawesome.com/releases/v5.12.0/css/all.css\">\n" +
            "      <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n" +
            "   </head>" +
            "   <body id=\"page-top\">";

    String foot = "      <footer class=\"footer text-center\">\n" +
            "         <div class=\"container\">\n" +
            "            <div class=\"row\">\n" +
            "               <div class=\"col-md-4 mb-5 mb-lg-0\">\n" +
            "                  <h4 class=\"text-uppercase mb-4\">Bot Creator</h4>\n" +
            "                  <p>平和#0240</p>\n" +
            "               </div>\n" +
            "               <div class=\"col-md-4 mb-5 mb-lg-0\">\n" +
            "                  <h4 class=\"text-uppercase\">Support</h4>\n" +
            "                  <ul class=\"list-inline\">\n" +
            "                     <li class=\"list-inline-item\"><a class=\"btn btn-outline-light btn-social text-center rounded-circle\" role=\"button\" href=\"https://github.com/DxsSucuk/Ree6/issues\" target=\"_blank\"><i class=\"fa fa-github fa-fw\"></i></a></li>\n" +
            "                     <li class=\"list-inline-item\"><a class=\"btn btn-outline-light btn-social text-center rounded-circle\" role=\"button\" href=\"https://support.ree6.de/\" target=\"_blank\"><i class=\"fab fa-discord\"></i></a></li>\n" +
            "                     <li class=\"list-inline-item\"><a class=\"btn btn-outline-light btn-social text-center rounded-circle\" role=\"button\" href=\"mailto:dxssucuk@hotmail.com\"><i class=\"fas fa-envelope\"></i></a></li>\n" +
            "                  </ul>\n" +
            "               </div>\n" +
            "               <div class=\"col-md-4\">\n" +
            "                  <h4 class=\"text-uppercase mb-4\">Special Links</h4>\n" +
            "                  <p class=\"lead mb-0\"><a href=\"https://ree6.de/inforeq.html\" target = \"_blank\">Data Information Request</a></p>\n" +
            "                  <p class=\"lead mb-0\"><a href=\"https://ree6.de/deletereq.html\" target = \"_blank\">Data Remove Request</a></p>\n" +
            "               </div>\n" +
            "            </div>\n" +
            "         </div>\n" +
            "      </footer>\n" +
            "   </body>\n" +
            "</html>";

    @Override
    public Response serve(IHTTPSession session) {

        String body = "";

        Map<String, String> parms = session.getParms();

        boolean isLoggedin = session.getCookies().read("ree6_login") != null;
        boolean loginFailed = false;

        if (isLoggedin) {
            if (!sqlWorker.getAuthToken(getGuildID(session)).equalsIgnoreCase(getAuthToken(session))) {
                session.getCookies().delete("ree6_login");
                isLoggedin = false;
                loginFailed = true;
            }
        }

        if (!isLoggedin && parms.get("login") != null) {
            if (!parms.get("login").isEmpty()) {
                if (sqlWorker.getAuthToken(getGuildID(parms.get("login"))).equalsIgnoreCase(getAuthToken(parms.get("login")))) {
                    session.getCookies().set("ree6_login", parms.get("login"), (Integer.MAX_VALUE));
                    isLoggedin = true;
                    loginFailed = false;
                } else {
                    isLoggedin = false;
                    loginFailed = true;
                }
            }
        } else if (isLoggedin && parms.get("logout") != null) {
            if (parms.get("logout").equalsIgnoreCase("confirm")) {
                if (sqlWorker.getAuthToken(getGuildID(session)).equalsIgnoreCase(getAuthToken(session))) {
                    sqlWorker.deleteAuthToken(getGuildID(session));
                }
                session.getCookies().delete("ree6_login");
                isLoggedin = false;
            }
        } else if (isLoggedin && parms.get("name") != null && parms.get("value") != null) {
            sqlWorker.setSetting(getGuildID(session), parms.get("name"), Boolean.parseBoolean(parms.get("value")));
        } else if (isLoggedin && parms.get("welcomemessage") != null) {
            String message = parms.get("welcomemessage").replace("%25", "%").replace("+", " ");
            if (message.length() < 250) {
                sqlWorker.setMessage(getGuildID(session), message);
            }
        } else if (isLoggedin && parms.get("addwords") != null) {
            String words = parms.get("addwords").replace("%25", "%").replace("+", " ").replace("%2C", ",");

            if (words.contains(",")) {
                String[] splits = words.split(",");
                for (String word : splits) {
                    Main.sqlWorker.addChatProtector(getGuildID(session), word);
                }
            } else {
                Main.sqlWorker.addChatProtector(getGuildID(session), words);
            }
        } else if (isLoggedin && parms.get("removeword") != null) {
            String word = parms.get("removeword").replace("%25", "%").replace("+", " ").replace("%2C", ",");
            Main.sqlWorker.removeChatProtector(getGuildID(session), word);
        }

        body = "  <nav class=\"navbar navbar-light navbar-expand-lg fixed-top bg-secondary text-uppercase\" id=\"mainNav\">\n" +
                "         <div class=\"container\">\n" +
                "            <a class=\"navbar-brand js-scroll-trigger\">Ree6</a><button data-toggle=\"collapse\" data-target=\"#navbarResponsive\" class=\"navbar-toggler navbar-toggler-right text-uppercase bg-primary text-white rounded\" aria-controls=\"navbarResponsive\"\n" +
                "               aria-expanded=\"false\" aria-label=\"Toggle navigation\"><i class=\"fa fa-bars\"></i></button>\n" +
                "            <div class=\"collapse navbar-collapse\" id=\"navbarResponsive\">\n" +
                "               <ul class=\"nav navbar-nav ml-auto\">\n" +
                "                  <li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"><a class=\"nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger\" href=\"/\">Home</a></li>\n" +
                "                  " + (isLoggedin ? "<li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"><a class=\"nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger\" href=\"/logout/?logout\">Logout</a></li>\n" : "") +
                "                  " + (isLoggedin ? "<li class=\"list-inline-item nav-item mx-0 mx-lg-1\" role=\"presentation\"><img class=\"rounded-circle\" src=\"" + getAvatarUrl(getDiscordID(session)) + "\" height=\"50\" width=\"50\"></li>\n" : "") +
                "                  <li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"></li>\n" +
                "               </ul>\n" +
                "            </div>\n" +
                "         </div>\n" +
                "      </nav>\n" +
                "      <header class=\"masthead bg-secondary text-white text-center\">\n" +
                "         <h1>Ree6</h1>\n" +
                "         <h2 class=\"font-weight-light mb-0\">" + (isLoggedin ? "Welcome " + getUsername(getDiscordID(session)) + " !" : "The alternative to Mee6!") + "</h2>\n" +
                "      </header>";

        if (loginFailed) {

            //Login Failed Page

            body += "      <section id = \"loginfail\" class = \"bg-tertiary\">\n" +
                    "        <h1 class=\"text-center text-secondary\">Invalid LoginKey!<h1/>\n" +
                    "          <p class=\"lead mb-4 text-center\">The given LoginKey is invalid! Please Generate a new one!</p>\n" +
                    "          <div class=\"container\">\n" +
                    "            <div class=\"text-center mt-4\">\n" +
                    "               <p class=\"lead mb-0\"><a class=\"btn btn-outline-light text-center\" role=\"button\" href = \"/\">Go Home</a></p>\n" +
                    "            </div>\n" +
                    "         </div>\n" +
                    "      </section>";
        } else if (isLoggedin) {

            //Login Success Pages

            if (session.getUri().startsWith("/logging")) {

                //Logging Panel

                body += "      <section id = \"panel\" class = \"bg-tertiary\">\n" +
                        "         <h2 class=\"text-uppercase text-center text-secondary\">Logging</h2>\n" +
                        "         <hr class=\"bolt-dark mb-5\">\n" +
                        "         <div class=\"container\">\n" +
                        "            <div class=\"row\">\n";

                for (Map.Entry<String, Boolean> settings : sqlWorker.getAllSettings(getGuildID(session))) {
                    if (settings.getKey().toLowerCase().startsWith("logging_")) {

                        body += "               <div class=\"col-md-6 col-lg-4\">\n" +
                                "                  <h1>" + (settings.getKey().replace("logging_", "").charAt(0) + "").toUpperCase() + settings.getKey().replace("logging_", "").substring(1).toLowerCase() + "</h1>\n" +
                                "                  <br  />\n" +
                                "                  <p class=\"lead mb-4\">Currently: " + (settings.getValue() ? "active!" : "disabled!") + "</p>\n" +
                                "                  <p class=\"lead mb-4\"><a class=\"btn btn-outline-dark text-center\" role=\"button\" href = \"/logging/?name=" + settings.getKey() + "&value=" + !settings.getValue() + "\">Change</a></p>\n" +
                                "               </div>\n";
                    }
                }

                body += "            </div>\n" +
                        "         </div>\n" +
                        "      </section>";
            } else if (session.getUri().startsWith("/moderation")) {

                //Moderation Panel

                body += "      <section id = \"panel\" class = \"bg-tertiary\">\n" +
                        "         <h2 class=\"text-uppercase text-center text-secondary\">Moderation</h2>\n" +
                        "         <hr class=\"bolt-dark mb-5\">\n" +
                        "         <div class=\"container\">\n" +
                        "            <div class=\"row\">\n";

                body += "<div class=\"col-md-6 col-lg-4\">\n" +
                        "   <h1>AutoRole</h1>\n" +
                        "   <br  />\n" +
                        "   <p class=\"lead mb-4\">Setuped: " + (Main.sqlWorker.hasAutoRoles(getGuildID(session)) ? "yes" : "no") + "<br/>To change this settings use ree!setup autorole add/remove @Role</p>\n" +
                        "</div>";

                body += "<div class=\"col-md-6 col-lg-4\">\n" +
                        "   <h1>MuteRole</h1>\n" +
                        "   <br  />\n" +
                        "   <p class=\"lead mb-4\">Setuped: " + (Main.sqlWorker.hasMuteSetuped(getGuildID(session)) ? "yes" : "no") + "<br/>To change this settings use ree!setup mute @Role</p>\n" +
                        "</div>";

                body += "<div class=\"col-md-6 col-lg-4\">\n" +
                        "   <h1>Log-Channel</h1>\n" +
                        "   <br  />\n" +
                        "   <p class=\"lead mb-4\">Setuped: " + (Main.sqlWorker.hasLogSetuped(getGuildID(session)) ? "yes" : "no") + "<br/>To change this settings use ree!setup log #Channel</p>\n" +
                        "</div>";

                body += "<div class=\"col-md-6 col-lg-4\">\n" +
                        "   <h1>Word-Blacklist</h1>\n" +
                        "   <br  />\n" +
                        "   <h4>Blacklisted Words:</h4>" +
                        "   <p class=\"lead mb-4 text-center\">";

                if (!Main.sqlWorker.getChatProtector(getGuildID(session)).isEmpty()) {
                    int i = 0;
                    int j = 0;
                    for (String words : Main.sqlWorker.getChatProtector(getGuildID(session))) {
                        if (i == 2) {
                            body += words + " <a class=\"lead mb-4\" href=\"/moderation/?removeword=" + words + "\">&#10060;</a>";
                            body += "<br  />";
                            i = 0;
                        } else {
                            body += words + " <a class=\"lead mb-4\" href=\"/moderation/?removeword=" + words + "\">&#10060;</a>" + (j != (Main.sqlWorker.getChatProtector(getGuildID(session)).size() - 1) ? "   |   " : "");
                            i++;
                        }
                        j++;
                    }
                } else {
                    body += "Empty";
                }

                body += "</p>\n" +
                        "   <form action=\"/moderation/\" method=\"get\">\n" +
                        "      <label class=\"lead mb-4\" for=\"addwords\">Words:</label>\n" +
                        "      <input class=\"outline-light bg-secondary text-primary\" type=\"text\" id=\"addwords\" name=\"addwords\">\n" +
                        "      <input class=\"btn btn-outline-dark text-center\" type=\"submit\" value=\"Add\">\n" +
                        "   </form>" +
                        "</div>";

                body += "            </div>\n" +
                        "         </div>\n" +
                        "      </section>";

                body += "      <section id = \"commands\" class = \"bg-tertiary\">\n" +
                        "         <h2 class=\"text-uppercase text-center text-secondary\">Commands</h2>\n" +
                        "         <hr class=\"bolt-dark mb-5\">\n" +
                        "         <div class=\"container\">\n" +
                        "            <div class=\"row\">\n";

                for (Map.Entry<String, Boolean> settings : sqlWorker.getAllSettings(getGuildID(session))) {
                    if (settings.getKey().toLowerCase().startsWith("command_")) {

                        body += "               <div class=\"col-md-6 col-lg-4\">\n" +
                                "                  <h1>" + (settings.getKey().replace("command_", "").charAt(0) + "").toUpperCase() + settings.getKey().replace("command_", "").substring(1).toLowerCase() + "</h1>\n" +
                                "                  <br  />\n" +
                                "                  <p class=\"lead mb-4\">Currently: " + (settings.getValue() ? "active!" : "disabled!") + "</p>\n" +
                                "                  <p class=\"lead mb-4\"><a class=\"btn btn-outline-dark text-center\" role=\"button\" href = \"/logging/?name=" + settings.getKey() + "&value=" + !settings.getValue() + "\">Change</a></p>\n" +
                                "               </div>\n";
                    }
                }

                body += "            </div>\n" +
                        "         </div>\n" +
                        "      </section>";
            } else if (session.getUri().startsWith("/social")) {

                //Social Panel

                body += "      <section id = \"panel\" class = \"bg-tertiary\">\n" +
                        "         <h2 class=\"text-uppercase text-center text-secondary\">Social</h2>\n" +
                        "         <hr class=\"bolt-dark mb-5\">\n" +
                        "         <div class=\"container\">\n" +
                        "            <div class=\"row\">\n";

                body += "<div class=\"col-md-6 col-lg-4\">\n" +
                        "   <h1>Ree6-News</h1>\n" +
                        "   <br  />\n" +
                        "   <p class=\"lead mb-4\">Setuped: " + (Main.sqlWorker.hasNewsSetuped(getGuildID(session)) ? "yes" : "no") + "<br/>To change this settings use ree!setup news #Channel</p>\n" +
                        "</div>";

                body += "<div class=\"col-md-6 col-lg-4\">\n" +
                        "   <h1>Mate-Searcher</h1>\n" +
                        "   <br  />\n" +
                        "   <p class=\"lead mb-4\">Setuped: " + (Main.sqlWorker.hasRainbowSetuped(getGuildID(session)) ? "yes" : "no") + "<br/>To change this settings use ree!setup r6 #Channel</p>\n" +
                        "</div>";

                body += "<div class=\"col-md-6 col-lg-4\">\n" +
                        "   <h1>Twitch-Notifier</h1>\n" +
                        "   <br  />\n" +
                        "   <p class=\"lead mb-4\">" + (Main.sqlWorker.hasTwitchNotifySetuped(getGuildID(session)) ? "Setuped for: " + getFromArray(sqlWorker.getTwitchNotifier(getGuildID(session))) + "<br/>" : "") + "To change this settings use ree!twitch TwitchName #Channel</p>\n" +
                        "</div>";

                body += "<div class=\"col-md-6 col-lg-4\">\n" +
                        "   <h1>Level-Rewards</h1>\n" +
                        "   <br  />\n" +
                        "   <p class=\"lead mb-4\">To change this settings use ree!setup rewards chat/vc add/remove LEVEL @Role</p>\n" +
                        "</div>";

                body += "<div class=\"col-md-6 col-lg-4\">\n" +
                        "   <h1>Welcome-Message</h1>\n" +
                        "   <br  />\n" +
                        "   <p class=\"lead mb-4\">" + Main.sqlWorker.getMessage(getGuildID(session)) + "</p>\n" +
                        "   <form action=\"/social/\" method=\"get\">\n" +
                        "      <label class=\"lead mb-4\" for=\"welcomemessage\">New Message:</label>\n" +
                        "      <input class=\"outline-light bg-secondary text-primary\" type=\"text\" id=\"welcomemessage\" name=\"welcomemessage\">\n" +
                        "      <input class=\"btn btn-outline-dark text-center\" type=\"submit\" value=\"Change\">\n" +
                        "   </form>" +
                        "</div>";

                body += "            </div>\n" +
                        "         </div>\n" +
                        "      </section>";
            } else if (session.getUri().startsWith("/logout")) {

                //Logout Page

                body += "      <section id = \"confirm\" class = \"bg-tertiary\">\n" +
                        "        <h1 class=\"text-center text-secondary\">" + "Are you sure that you want to logout?" + "<h1/>\n" +
                        "        <br  />\n" +
                        "          <div class=\"container\">\n" +
                        "            <div class=\"text-center mt-4\">\n" +
                        "               <p class=\"lead mb-0\"><a class=\"btn btn-outline-light text-center\" role=\"button\" href = \"/logout/?logout=confirm\">Confirm Logout</a></p>\n" +
                        "               <br  />\n" +
                        "               <p class=\"lead mb-0\"><a class=\"btn btn-outline-light text-center\" role=\"button\" href = \"/\">Go back</a></p>\n" +
                        "            </div>\n" +
                        "         </div>\n" +
                        "      </section>";
            } else {

                //Main Page

                body += "      <section id = \"panel\" class = \"bg-tertiary\">\n" +
                        "         <h2 class=\"text-uppercase text-center text-secondary\">Control-Panels</h2>\n" +
                        "         <hr class=\"bolt-dark mb-5\">\n" +
                        "         <div class=\"container\">\n" +
                        "            <div class=\"row\">\n" +
                        "               <div class=\"col-md-6 col-lg-4\">\n" +
                        "                  <img class=\"img-fluid mb-5\" src=\"https://utils.ree6.de/img/mod.png\" height=\"200\" width=\"200\">\n" +
                        "                  <h1>Moderation</h1>\n" +
                        "                  <br  />\n" +
                        "                  <p class=\"lead mb-4\">Configurate the Moderation-System Modules such as AutoMute, MuteRole, AutoRole, Command disabling and even more!</p>\n" +
                        "                  <p class=\"lead mb-4\"><a class=\"btn btn-outline-dark text-center\" role=\"button\" href = \"/moderation\">Go to Moderation</a></p>\n" +
                        "               </div>\n" +
                        "               <div class=\"col-md-6 col-lg-4\">\n" +
                        "                  <img class=\"img-fluid mb-5\" src=\"https://utils.ree6.de/img/log.png\" height=\"200\" width=\"200\">\n" +
                        "                  <h1>Logging</h1>\n" +
                        "                  <br  />\n" +
                        "                  <p class=\"lead mb-4\">Manage the Advanced-AuditLogging System by enabling and disabling certain Logging-Modules!</p>\n" +
                        "                  <p class=\"lead mb-4\"><a class=\"btn btn-outline-dark text-center\" role=\"button\" href = \"/logging\">Go to Logging</a></p>\n" +
                        "               </div>\n" +
                        "               <div class=\"col-md-6 col-lg-4\">\n" +
                        "                  <img class=\"img-fluid mb-5\" src=\"https://utils.ree6.de/img/fun.png\" height=\"200\" width=\"200\">\n" +
                        "                  <h1>Social</h1>\n" +
                        "                  <p class=\"lead mb-4\">Manage different Social-Modules such as Ree6-News, Rainbow-MateSearcher, Twitch-Live and more!</p>\n" +
                        "                  <p class=\"lead mb-4\"><a class=\"btn btn-outline-dark text-center\" role=\"button\" href = \"/social\">Go to Social</a></p>\n" +
                        "               </div>\n" +
                        "            </div>\n" +
                        "         </div>\n" +
                        "      </section>";
            }
        } else {

            //Not Loggedin Page

            body += "      <section id = \"nologin\" class = \"bg-tertiary\">\n" +
                    "        <h1 class=\"text-center text-secondary\">You aren't loggedin!<h1/>\n" +
                    "          <div class=\"container\">\n" +
                    "            <div class=\"text-center mt-4\">\n" +
                    "               <p class=\"lead mb-4 text-center\">Please log yourself in by using the Command ree!webinterface!</p>\n" +
                    "            </div>\n" +
                    "         </div>\n" +
                    "      </section>";
        }

        return newFixedLengthResponse(head + body + foot);
    }


    public String getFromArray(ArrayList<String> arrayList) {
        String end = "";

        if (arrayList.isEmpty()) {
            return "Empty";
        }

        int i = 0;
        for (String s : arrayList) {
            if (i != (arrayList.size() - 1)) {
                end += s + ", ";
                i++;
            } else {
                end += s;
            }
        }

        return end;

    }

    public String getGuildID(IHTTPSession session) {
        return (Crypter.de(session.getCookies().read("ree6_login")) != null) ? Crypter.de(session.getCookies().read("ree6_login")).split("-")[0] != null ? Crypter.de(session.getCookies().read("ree6_login")).split("-")[0] : "0" : session.getParms().get("login") != null ? getGuildID(session.getParms().get("login")) : "0";
    }

    public String getGuildID(String base64String) {
        return (Crypter.de(base64String) != null) ? Crypter.de(base64String).split("-")[0] != null ? Crypter.de(base64String).split("-")[0] : "0" : "0";
    }

    public String getAuthToken(IHTTPSession session) {
        return (Crypter.de(session.getCookies().read("ree6_login")) != null) ? Crypter.de(session.getCookies().read("ree6_login")).split("-")[1].split(":")[0] != null ? Crypter.de(session.getCookies().read("ree6_login")).split("-")[1].split(":")[0] : "0" : session.getParms().get("login") != null ? getAuthToken(session.getParms().get("login")) : "0";
    }

    public String getAuthToken(String base64String) {
        return (Crypter.de(base64String) != null) ? Crypter.de(base64String).split("-")[1].split(":")[0] != null ? Crypter.de(base64String).split("-")[1].split(":")[0] : "0" : "0";
    }

    public String getDiscordID(IHTTPSession session) {
        return (Crypter.de(session.getCookies().read("ree6_login")) != null) ? Crypter.de(session.getCookies().read("ree6_login")).split(":")[1] != null ? Crypter.de(session.getCookies().read("ree6_login")).split(":")[1] : "0" : session.getParms().get("login") != null ? getDiscordID(session.getParms().get("login")) : "0";
    }

    public String getDiscordID(String base64String) {
        return (Crypter.de(base64String) != null) ? Crypter.de(base64String).split(":")[1] != null ? Crypter.de(base64String).split(":")[1] : "0" : "0";
    }

    public static String getAvatarUrl(String userID) {
        JSONObject js = JSONApi.GetData(Requests.GET, "https://discord.com/api/users/" + userID, "Bot " + Main.config.getConfig().getString("discordapi.token"));
        return (js.has("avatar") ? "https://cdn.discordapp.com/avatars/" + userID + "/" + js.getString("avatar") : "https://preview.redd.it/nx4jf8ry1fy51.gif?format=png8&s=a5d51e9aa6b4776ca94ebe30c9bb7a5aaaa265a6");
    }

    public static String getUsername(String userID) {
        JSONObject js = JSONApi.GetData(Requests.GET, "https://discord.com/api/users/" + userID, "Bot " + Main.config.getConfig().getString("discordapi.token"));
        return (js.has("username") ? js.getString("username") : "Please reload");
    }

}
