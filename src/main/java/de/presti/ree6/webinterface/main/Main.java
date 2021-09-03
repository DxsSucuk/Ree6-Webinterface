package de.presti.ree6.webinterface.main;

import de.presti.ree6.webinterface.json.JSONApi;
import de.presti.ree6.webinterface.json.Requests;
import de.presti.ree6.webinterface.sql.SQLConnector;
import de.presti.ree6.webinterface.sql.SQLWorker;
import de.presti.ree6.webinterface.utils.Config;
import de.presti.ree6.webinterface.utils.SecurityUtil;
import de.presti.ree6.webinterface.utils.Setting;
import fi.iki.elonen.NanoHTTPD;
import io.mokulu.discord.oauth.DiscordAPI;
import io.mokulu.discord.oauth.DiscordOAuth;
import io.mokulu.discord.oauth.model.TokensResponse;
import io.mokulu.discord.oauth.model.User;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Main extends NanoHTTPD {

    /**
     * Instances of SQL-Helpers and Config-Helpers.
     */
    public static SQLConnector sqlConnector;
    public static SQLWorker sqlWorker;
    public static Config config;
    public static DiscordOAuth discordOAuth;

    /**
     * Start the NanoHTTPD Server.
     *
     * @throws IOException   If the Port is already in use or if it could bind it.
     */
    public Main() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }

    /**
     * Start of the Program.
     *
     * @param args     Given start Arguments.
     */
    public static void main(String[] args) {
        try {

            // Create a new Config Instance.
            config = new Config();

            // Create a new DiscordOAuth Instance.
            discordOAuth = new DiscordOAuth(config.getConfig().getString("discord.client_id"), config.getConfig().getString("discord.client_secret"),"http://localhost:8080", new String[] { "guilds", "guilds.join", "identify" } );

            // Create a new SQL-Connector Instance.
            sqlConnector = new SQLConnector(config.getConfig().getString("mysql.user"), config.getConfig().getString("mysql.pw"), config.getConfig().getString("mysql.host"), config.getConfig().getString("mysql.db"), config.getConfig().getInt("mysql.port"));

            // Create a new SQL-Worker Instance.
            sqlWorker = new SQLWorker();

            // Start the NanoHTTPD Server.
            new Main();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Default header of the Site.
     */
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

    /**
     * Default footer of the Site.
     */
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

    /**
     * Called when a new Connection is established.
     *
     * @param session The current User Session.
     * @return Response Returns an HTML Response for the User.
     */
    @Override
    public Response serve(IHTTPSession session) {

        String body = "";

        Map<String, String> parms = session.getParms();

        boolean isLoggedin = session.getCookies().read("state") != null || session.getCookies().read("2oauth") != null;
        boolean loginFailed = false;
        boolean logoutSuccess = false;

        if (session.getCookies().read("state") == null) {
            session.getCookies().set("state", SecurityUtil.en(SecurityUtil.randomString(50)), 7);
        }

        if (parms.get("code") != null && parms.get("state") != null) {
            if (checkForCSRF(parms.get("state"), session)) {
                if (session.getCookies().read("ree6_login") != null) {
                    session.getCookies().delete("ree6_login");
                }

                if (session.getCookies().read("2oauth") != null && !session.getCookies().read("2oauth").equals( SecurityUtil.en(parms.get("code")))) {
                    session.getCookies().delete("2oauth");
                }

                session.getCookies().set("2oauth", SecurityUtil.en(parms.get("code")), 7);
                isLoggedin = true;
            } else {
                session.getCookies().delete("state");
                session.getCookies().delete("2oauth");
                session.getCookies().delete("auth");
                isLoggedin = false;
                loginFailed = true;
            }
        }

        if (isLoggedin) {
            // Check if a Cookies are saved.
            if (session.getCookies().read("2oauth") != null && session.getCookies().read("state") != null) {

                //TODO check if valid

            } else if (session.getCookies().read("state") != null && parms.get("code") != null) {
                // Check if a State and a Code have been given to the User.
                if (parms.get("state") != null) {
                    // Check for an CSRF exploit.
                    if (checkForCSRF(parms.get("state"), session)) {
                            //TODO check if valid
                    } else {
                        isLoggedin = false;
                        loginFailed = true;
                    }
                } else {
                    isLoggedin = false;
                }
            } else {
                isLoggedin = false;
            }
        }

        if (isLoggedin && parms.get("logout") != null && !parms.get("logout").matches("('(''|[^'])*')|(\\)\\;)|(--)|(ALTER|CREATE|DELETE|DROP|EXEC(UTE){0,1}|INSERT( +INTO){0,1}|MERGE|SELECT|UPDATE|VERSION|ORDER|UNION( +ALL){0,1})")) {
            if (parms.get("logout").equalsIgnoreCase("confirm")) {
                if (sqlWorker.getAuthToken(getGuildID(session)).equalsIgnoreCase(getAuthToken(session))) {
                    sqlWorker.deleteAuthToken(getGuildID(session));
                }
                session.getCookies().delete("ree6_login");
                isLoggedin = false;
                logoutSuccess = true;
            }
        } else if (isLoggedin && parms.get("name") != null && parms.get("value") != null && !parms.get("name").matches("('(''|[^'])*')|(\\)\\;)|(--)|(ALTER|CREATE|DELETE|DROP|EXEC(UTE){0,1}|INSERT( +INTO){0,1}|MERGE|SELECT|UPDATE|VERSION|ORDER|UNION( +ALL){0,1})") && !parms.get("value").matches("('(''|[^'])*')|(\\)\\;)|(--)|(ALTER|CREATE|DELETE|DROP|EXEC(UTE){0,1}|INSERT( +INTO){0,1}|MERGE|SELECT|UPDATE|VERSION|ORDER|UNION( +ALL){0,1})")) {
            sqlWorker.setSetting(getGuildID(session), parms.get("name"), parms.get("value"));
        } else if (isLoggedin && parms.get("welcomemessage") != null  && !parms.get("welcomemessage").matches("('(''|[^'])*')|(\\)\\;)|(--)|(ALTER|CREATE|DELETE|DROP|EXEC(UTE){0,1}|INSERT( +INTO){0,1}|MERGE|SELECT|UPDATE|VERSION|ORDER|UNION( +ALL){0,1})")) {
            String message = replaceHTMLEncoding(parms.get("welcomemessage"));
            if (message.length() < 250) {
                sqlWorker.setMessage(getGuildID(session), message);
            }
        } else if (isLoggedin && parms.get("addwords") != null && !parms.get("addwords").matches("('(''|[^'])*')|(\\)\\;)|(--)|(ALTER|CREATE|DELETE|DROP|EXEC(UTE){0,1}|INSERT( +INTO){0,1}|MERGE|SELECT|UPDATE|VERSION|ORDER|UNION( +ALL){0,1})")) {
            String words = replaceHTMLEncoding(parms.get("addwords"));

            if (words.contains(",")) {
                String[] splits = words.split(",");
                for (String word : splits) {
                    Main.sqlWorker.addChatProtector(getGuildID(session), word);
                }
            } else {
                Main.sqlWorker.addChatProtector(getGuildID(session), words);
            }
        } else if (isLoggedin && parms.get("removeword") != null && !parms.get("removeword").matches("('(''|[^'])*')|(\\)\\;)|(--)|(ALTER|CREATE|DELETE|DROP|EXEC(UTE){0,1}|INSERT( +INTO){0,1}|MERGE|SELECT|UPDATE|VERSION|ORDER|UNION( +ALL){0,1})")) {
            String word = replaceHTMLEncoding(parms.get("removeword"));
            Main.sqlWorker.removeChatProtector(getGuildID(session), word);
        } else if (isLoggedin && parms.get("setprefix") != null && !parms.get("setprefix").matches("('(''|[^'])*')|(\\)\\;)|(--)|(ALTER|CREATE|DELETE|DROP|EXEC(UTE){0,1}|INSERT( +INTO){0,1}|MERGE|SELECT|UPDATE|VERSION|ORDER|UNION( +ALL){0,1})")) {
            String message = replaceHTMLEncoding(parms.get("setprefix"));
            Main.sqlWorker.setSetting(getGuildID(session), "chatprefix", message);
        }

        body = "  <nav class=\"navbar navbar-light navbar-expand-lg fixed-top bg-secondary text-uppercase\" id=\"mainNav\">\n" +
                "         <div class=\"container\">\n" +
                "            <a class=\"navbar-brand js-scroll-trigger\">Ree6</a><button data-toggle=\"collapse\" data-target=\"#navbarResponsive\" class=\"navbar-toggler navbar-toggler-right text-uppercase bg-primary text-white rounded\" aria-controls=\"navbarResponsive\"\n" +
                "               aria-expanded=\"false\" aria-label=\"Toggle navigation\"><i class=\"fa fa-bars\"></i></button>\n" +
                "            <div class=\"collapse navbar-collapse\" id=\"navbarResponsive\">\n" +
                "               <ul class=\"nav navbar-nav ml-auto\">\n" +
                "                  <li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"><a class=\"nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger\" href=\"/\">Home</a></li>\n" +
                "                  " + (isLoggedin ? "<li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"><a class=\"nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger\" href=\"/logout/?logout\">Logout</a></li>\n" : "") +
                "                  " + (isLoggedin ? "<li class=\"list-inline-item nav-item mx-0 mx-lg-1\" role=\"presentation\"><img class=\"rounded-circle\" src=\"" + (getCurrentUser(getCurrentToken(session)) != null ? getCurrentUser(getCurrentToken(session)).getAvatar() : "https://preview.redd.it/nx4jf8ry1fy51.gif?format=png8&s=a5d51e9aa6b4776ca94ebe30c9bb7a5aaaa265a6") + "\" height=\"50\" width=\"50\"></li>\n" : "") +
                "                  <li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"></li>\n" +
                "               </ul>\n" +
                "            </div>\n" +
                "         </div>\n" +
                "      </nav>\n" +
                "      <header class=\"masthead bg-secondary text-white text-center\">\n" +
                "         <h1>Ree6</h1>\n" +
                "         <h2 class=\"font-weight-light mb-0\">" + (isLoggedin ? "Welcome " + (getCurrentUser(getCurrentToken(session)) != null ? getCurrentUser(getCurrentToken(session)).getUsername() : "Reload") + " !" : "The alternative to Mee6!") + "</h2>\n" +
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

                for (Setting settings : sqlWorker.getAllSettings(getGuildID(session))) {
                    if (settings.getName().startsWith("logging_")) {

                        body += "               <div class=\"col-md-6 col-lg-4\">\n" +
                                "                  <h1>" + (settings.getName().replace("logging_", "").charAt(0) + "").toUpperCase() + settings.getName().replace("logging_", "").substring(1).toLowerCase() + "</h1>\n" +
                                "                  <br  />\n" +
                                "                  <p class=\"lead mb-4\">Currently: " + (settings.getBooleanValue() ? "active!" : "disabled!") + "</p>\n" +
                                "                  <p class=\"lead mb-4\"><a class=\"btn btn-outline-dark text-center\" role=\"button\" href = \"/logging/?name=" + settings.getName() + "&value=" + !settings.getBooleanValue() + "\">Change</a></p>\n" +
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
                        "   <h1>Command Prefix</h1>\n" +
                        "   <br  />\n" +
                        "   <h4>Current Prefix: " + Main.sqlWorker.getSetting(getGuildID(session), "chatprefix").getStringValue() + "</h4>" +
                        "   <form action=\"/moderation/\" method=\"get\">\n" +
                        "      <label class=\"lead mb-4\" for=\"setprefix\">Prefix:</label>\n" +
                        "      <input class=\"outline-light bg-secondary text-primary\" type=\"text\" id=\"setprefix\" name=\"setprefix\">\n" +
                        "      <input class=\"btn btn-outline-dark text-center\" type=\"submit\" value=\"Add\">\n" +
                        "   </form>" +
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

                for (Setting settings : sqlWorker.getAllSettings(getGuildID(session))) {
                    if (settings.getName().toLowerCase().startsWith("command_")) {

                        body += "               <div class=\"col-md-6 col-lg-4\">\n" +
                                "                  <h1>" + (settings.getName().replace("command_", "").charAt(0) + "").toUpperCase() + settings.getName().replace("command_", "").substring(1).toLowerCase() + "</h1>\n" +
                                "                  <br  />\n" +
                                "                  <p class=\"lead mb-4\">Currently: " + (settings.getBooleanValue() ? "active!" : "disabled!") + "</p>\n" +
                                "                  <p class=\"lead mb-4\"><a class=\"btn btn-outline-dark text-center\" role=\"button\" href = \"/moderation/?name=" + settings.getName() + "&value=" + !settings.getBooleanValue() + "\">Change</a></p>\n" +
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

            if (logoutSuccess) {
                body += "<script>\n" +
                        "   alert(\"You have been logged out!\")\n" +
                        "</script>\n";
            }

            body += "      <section id = \"nologin\" class = \"bg-tertiary\">\n" +
                    "        <h1 class=\"text-center text-secondary\">You aren't loggedin!<h1/>\n" +
                    "          <div class=\"container\">\n" +
                    "            <div class=\"text-center mt-4\">\n" +
                    "               <p class=\"lead mb-4 text-center\">Do you want to login?</p>\n" +
                    "               <br  />\n"+
                    "               <p class=\"lead mb-0\"><a class=\"btn btn-outline-light text-center\" role=\"button\" href = \"" + discordOAuth.getAuthorizationURL(getState(session)) + "\">Login with Discord!</a></p>\n" +
                    "            </div>\n" +
                    "         </div>\n" +
                    "      </section>";
        }

        return newFixedLengthResponse(head + body + foot);
    }

    /**
     * Check if someone has been Click-jacked (someone used the CSRF exploit)
     *
     * @param state     The given Request State from Discord.
     * @param session   The current Session of the User
     *
     * @return boolean  Returns if everything is alright
     */
    private boolean checkForCSRF(String state, IHTTPSession session) {
        return session.getCookies().read("state") != null && SecurityUtil.de(session.getCookies().read("state")) != null && SecurityUtil.de(session.getCookies().read("state")).equals(state);
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

    public String getState(IHTTPSession session) {
        return (SecurityUtil.de(session.getCookies().read("state")) != null) ? decryptString(session.getCookies().read("state")) : session.getParms().get("state") != null ? session.getParms().get("state") : "0";
    }

    public String getCode(IHTTPSession session) {
        return (SecurityUtil.de(session.getCookies().read("2oauth")) != null) ? decryptString(session.getCookies().read("2oauth")) : session.getParms().get("code") != null ? session.getParms().get("code") : "0";
    }

    public String getAuth(IHTTPSession session) {
        return (SecurityUtil.de(session.getCookies().read("auth")) != null) ? decryptString(session.getCookies().read("auth")) : "0";
    }

    public String decryptString(String base64String) {
        return (SecurityUtil.de(base64String) != null) ?  SecurityUtil.de(base64String) : "0";
    }

    public TokensResponse getCurrentToken(IHTTPSession session) {
        try {
            TokensResponse response = discordOAuth.getTokens("BqsWA9eIqbnIv6MqhLJAIzFsRgd7yV");

            System.out.println(response);

            response = discordOAuth.refreshTokens(response.getRefreshToken());
            return response;
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return null;
    }

    public User getCurrentUser(TokensResponse response) {
        try {
            return new DiscordAPI(response.getAccessToken()).fetchUser();
        } catch (Exception ignore) {}
        return null;
    }

    public String getGuildID(IHTTPSession session) {
        return (SecurityUtil.de(session.getCookies().read("ree6_login")) != null) ? SecurityUtil.de(session.getCookies().read("ree6_login")).split("-")[0] != null ? SecurityUtil.de(session.getCookies().read("ree6_login")).split("-")[0] : "0" : session.getParms().get("login") != null ? getGuildID(session.getParms().get("login")) : "0";
    }

    public String getGuildID(String base64String) {
        return (SecurityUtil.de(base64String) != null) ? SecurityUtil.de(base64String).split("-")[0] != null ? SecurityUtil.de(base64String).split("-")[0] : "0" : "0";
    }

    public String getAuthToken(IHTTPSession session) {
        return (SecurityUtil.de(session.getCookies().read("ree6_login")) != null) ? SecurityUtil.de(session.getCookies().read("ree6_login")).split("-")[1].split(":")[0] != null ? SecurityUtil.de(session.getCookies().read("ree6_login")).split("-")[1].split(":")[0] : "0" : session.getParms().get("login") != null ? getAuthToken(session.getParms().get("login")) : "0";
    }

    public String getAuthToken(String base64String) {
        return (SecurityUtil.de(base64String) != null) ? SecurityUtil.de(base64String).split("-")[1].split(":")[0] != null ? SecurityUtil.de(base64String).split("-")[1].split(":")[0] : "0" : "0";
    }

    public String getDiscordID(IHTTPSession session) {
        return (SecurityUtil.de(session.getCookies().read("ree6_login")) != null) ? SecurityUtil.de(session.getCookies().read("ree6_login")).split(":")[1] != null ? SecurityUtil.de(session.getCookies().read("ree6_login")).split(":")[1] : "0" : session.getParms().get("login") != null ? getDiscordID(session.getParms().get("login")) : "0";
    }

    public String getDiscordID(String base64String) {
        return (SecurityUtil.de(base64String) != null) ? SecurityUtil.de(base64String).split(":")[1] != null ? SecurityUtil.de(base64String).split(":")[1] : "0" : "0";
    }

    public static String getAvatarUrl(String userID) {
        JSONObject js = JSONApi.GetData(Requests.GET, "https://discord.com/api/users/" + userID, "Bot " + Main.config.getConfig().getString("discordapi.token"));
        return (js.has("avatar") && js.get("avatar") instanceof String ? "https://cdn.discordapp.com/avatars/" + userID + "/" + js.getString("avatar") : "https://preview.redd.it/nx4jf8ry1fy51.gif?format=png8&s=a5d51e9aa6b4776ca94ebe30c9bb7a5aaaa265a6");
    }

    public static String getUsername(String userID) {
        JSONObject js = JSONApi.GetData(Requests.GET, "https://discord.com/api/users/" + userID, "Bot " + Main.config.getConfig().getString("discordapi.token"));
        return (js.has("username") ? js.getString("username") : "Please reload");
    }

    /**
     * Format a given URL-Encode String to a normal String.
     *
     * @param string     The given URL-Encoded String
     *
     * @return string    Returns a URL-decoded String.
     */
    public String replaceHTMLEncoding(String string) {
        return string.replace("+", " ").replaceAll("%20", " ").replaceAll("%21", "!").replaceAll("%22", "\"").replaceAll("%23", "#").replaceAll("%24", "$").replace("%25", "%").replaceAll("%26", "&").replaceAll("%27", "'").replaceAll("%28", "(")
                .replaceAll("%29", ")").replaceAll("%2B", "+").replaceAll("%2F", "/").replaceAll("%3A", ":").replaceAll("%3B", ";").replaceAll("%3C", "<").replaceAll("%3D", "=").replaceAll("%3E", ">").replaceAll("%3F", "?").replaceAll("%40", "@")
                .replaceAll("%5B", "[").replaceAll("%5C", "\\").replaceAll("%5D", "]").replaceAll("%5E", "^").replaceAll("%5F", "_").replaceAll("%60", "'").replaceAll("%7B", "{").replace("%7C", "-").replaceAll("%7D", "}").replaceAll("%7E", "~");
    }

}
