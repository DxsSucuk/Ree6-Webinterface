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

    @Override
    public Response serve(IHTTPSession session) {

        String titleheader = "Welcome";
        String titlefooter = "to our Webinterface ";

        Map<String, String> parms = session.getParms();

        boolean isloggedin = session.getCookies().read("ree6_login") != null;

        if (parms.get("login") != null) {
            if (!parms.get("login").isEmpty()) {
                if (sqlWorker.getAuthToken(getGuildID(parms.get("login"))).equalsIgnoreCase(getAuthToken(parms.get("login")))) {
                    session.getCookies().set("ree6_login", parms.get("login"), (Integer.MAX_VALUE));
                    isloggedin = true;
                }
            }
        }

        if (isloggedin && getDiscordID(session).equalsIgnoreCase("0")) {
            titleheader = "Please Refresh";
            titlefooter = "";
        } else {
            titleheader = "Welcome";
            titlefooter = "to our Webinterface" + (isloggedin ? " " + getUsername(getDiscordID(session)) : "") + "!";
        }

        if(parms.get("deleteAll") != null) {
            if(isloggedin) {
                if (sqlWorker.getAuthToken(getGuildID(session)).equalsIgnoreCase(getAuthToken(session))) {
                    sqlWorker.deleteAllMyData(getGuildID(session));
                    return newFixedLengthResponse("<html lang=\"de\">\n" +
                            "   <head>\n" +
                            "      <!-- Global site tag (gtag.js) - Google Analytics -->\n" +
                            "      <script async src=\"https://www.googletagmanager.com/gtag/js?id=G-0LNY8PVZFC\"></script>\n" +
                            "      <script>\n" +
                            "         window.dataLayer = window.dataLayer || [];\n" +
                            "         function gtag(){dataLayer.push(arguments);}\n" +
                            "         gtag('js', new Date());\n" +
                            "\n" +
                            "         gtag('config', 'G-0LNY8PVZFC');\n" +
                            "      </script>\n" +
                            "      <script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':\n" +
                            "         new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],\n" +
                            "         j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=\n" +
                            "         'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);\n" +
                            "         })(window,document,'script','dataLayer','GTM-TBN3WRL');\n" +
                            "      </script>\n" +
                            "      <script data-ad-client=\"ca-pub-5270328367815951\" async src=\"https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js\"></script>\n" +
                            "      <meta charset=\"utf-8\">\n" +
                            "      <title>Ree6</title>\n" +
                            "      <link rel=\"icon\" type=\"image/png\" href=\"https://utils.ree6.de/img/ree.png\">\n" +
                            "      <link rel=\"stylesheet\" href=\"https://utils.ree6.de/css/bootstrap.css\">\n" +
                            "      <link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Montserrat:400,700\">\n" +
                            "      <link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic\">\n" +
                            "      <link rel=\"stylesheet\" href=\"https://use.fontawesome.com/releases/v5.12.0/css/all.css\">\n" +
                            "      <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n" +
                            "   </head>\n" + "   <body id=\"page-top\">\n" +
                            "      <!-- Google Tag Manager (noscript) -->\n" +
                            "      <noscript><iframe src=\"https://www.googletagmanager.com/ns.html?id=GTM-TBN3WRL\"\n" +
                            "         height=\"0\" width=\"0\" style=\"display:none;visibility:hidden\"></iframe></noscript>\n" +
                            "      <!-- End Google Tag Manager (noscript) -->\n" +
                            "      <nav class=\"navbar navbar-light navbar-expand-lg fixed-top bg-secondary text-uppercase\" id=\"mainNav\">\n" +
                            "         <div class=\"container\">\n" +
                            "            <a class=\"navbar-brand js-scroll-trigger\" href=\"#page-top\">Ree6</a><button data-toggle=\"collapse\" data-target=\"#navbarResponsive\" class=\"navbar-toggler navbar-toggler-right text-uppercase bg-primary text-white rounded\" aria-controls=\"navbarResponsive\"\n" +
                            "               aria-expanded=\"false\" aria-label=\"Toggle navigation\"><i class=\"fa fa-bars\"></i></button>\n" +
                            "            <div class=\"collapse navbar-collapse\" id=\"navbarResponsive\">\n" +
                            "               <ul class=\"nav navbar-nav ml-auto\">\n" +
                            "                  <li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"><a class=\"nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger\" href=\"https://ree6.de\">Home</a></li>\n" +
                            "                  " + (isloggedin ? "<li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"><a class=\"nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger\" href=\"?logout\">Logout</a></li>" : "") + "\n" +
                            "                  " + (isloggedin ? "<img class=\"img-fluid mb-5\" src=\"" + getAvatarUrl(getDiscordID(session)) + "\" height=\"50\" width=\"50\">\n" : "") +
                            "                  <li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"></li>\n" +
                            "               </ul>\n" +
                            "            </div>\n" +
                            "         </div>\n" +
                            "      </nav>\n" +
                            "      <header class=\"masthead bg-secondary text-white text-center\">\n" +
                            "         <h1>Your Data has been deleted!</h1>\n" +
                            "         <h2 class=\"font-weight-light mb-0\">Have a nice day!</h2>\n" +
                            "      </header>\n" +
                            "      <section id = \"features\" class = \"bg-tertiary\">\n" +
                            "         <h2 class=\"text-uppercase text-center text-secondary\">Informations</h2>\n" +
                            "         <div class=\"container\">\n<br/><h2 class=\"font-weight-light mb-0 text-center\"> Its gone! </h2><br/><h3 class=\"font-weight-light mb-0 text-center\"> We cant recover your Data anymore if this wasnt on purpose we are sorry! </h3></div>\n" +
                            "      </section>\n" +
                            "      <footer class=\"footer text-center\">\n" +
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
                            "                     <li class=\"list-inline-item\"><a class=\"btn btn-outline-light btn-social text-center rounded-circle\" role=\"button\" href=\"mailto:prestigemaster@hotmail.com\"><i class=\"fas fa-envelope\"></i></a></li>\n" +
                            "                  </ul>\n" +
                            "               </div>\n" +
                            "               <div class=\"col-md-4\">\n" +
                            "                  <h4 class=\"text-uppercase mb-4\">Special Links</h4>\n" +
                            "                  <p class=\"lead mb-0\"><a href=\"ree6.de/inforeq.html\" target = \"_blank\">Data Information Request</a></p>\n" +
                            "                  <p class=\"lead mb-0\"><a href=\"ree6.de/deletereq.html\" target = \"_blank\">Data Remove Request</a></p>\n" +
                            "               </div>\n" +
                            "            </div>\n" +
                            "         </div>\n" +
                            "      </footer>" + "</body></html>\n");
                } else {
                    return newFixedLengthResponse("<html lang=\"de\">\n" +
                            "   <head>\n" +
                            "      <!-- Global site tag (gtag.js) - Google Analytics -->\n" +
                            "      <script async src=\"https://www.googletagmanager.com/gtag/js?id=G-0LNY8PVZFC\"></script>\n" +
                            "      <script>\n" +
                            "         window.dataLayer = window.dataLayer || [];\n" +
                            "         function gtag(){dataLayer.push(arguments);}\n" +
                            "         gtag('js', new Date());\n" +
                            "\n" +
                            "         gtag('config', 'G-0LNY8PVZFC');\n" +
                            "      </script>\n" +
                            "      <script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':\n" +
                            "         new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],\n" +
                            "         j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=\n" +
                            "         'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);\n" +
                            "         })(window,document,'script','dataLayer','GTM-TBN3WRL');\n" +
                            "      </script>\n" +
                            "      <script data-ad-client=\"ca-pub-5270328367815951\" async src=\"https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js\"></script>\n" +
                            "      <meta charset=\"utf-8\">\n" +
                            "      <title>Ree6</title>\n" +
                            "      <link rel=\"icon\" type=\"image/png\" href=\"https://utils.ree6.de/img/ree.png\">\n" +
                            "      <link rel=\"stylesheet\" href=\"https://utils.ree6.de/css/bootstrap.css\">\n" +
                            "      <link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Montserrat:400,700\">\n" +
                            "      <link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic\">\n" +
                            "      <link rel=\"stylesheet\" href=\"https://use.fontawesome.com/releases/v5.12.0/css/all.css\">\n" +
                            "      <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n" +
                            "   </head>\n" + "   <body id=\"page-top\">\n" +
                            "      <!-- Google Tag Manager (noscript) -->\n" +
                            "      <noscript><iframe src=\"https://www.googletagmanager.com/ns.html?id=GTM-TBN3WRL\"\n" +
                            "         height=\"0\" width=\"0\" style=\"display:none;visibility:hidden\"></iframe></noscript>\n" +
                            "      <!-- End Google Tag Manager (noscript) -->\n" +
                            "      <nav class=\"navbar navbar-light navbar-expand-lg fixed-top bg-secondary text-uppercase\" id=\"mainNav\">\n" +
                            "         <div class=\"container\">\n" +
                            "            <a class=\"navbar-brand js-scroll-trigger\" href=\"#page-top\">Ree6</a><button data-toggle=\"collapse\" data-target=\"#navbarResponsive\" class=\"navbar-toggler navbar-toggler-right text-uppercase bg-primary text-white rounded\" aria-controls=\"navbarResponsive\"\n" +
                            "               aria-expanded=\"false\" aria-label=\"Toggle navigation\"><i class=\"fa fa-bars\"></i></button>\n" +
                            "            <div class=\"collapse navbar-collapse\" id=\"navbarResponsive\">\n" +
                            "               <ul class=\"nav navbar-nav ml-auto\">\n" +
                            "                  <li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"><a class=\"nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger\" href=\"https://ree6.de\">Home</a></li>\n" +
                            "                  " + (isloggedin ? "<li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"><a class=\"nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger\" href=\"?logout\">Logout</a></li>" : "") + "\n" +
                            "                  " + (isloggedin ? "<img class=\"img-fluid mb-5\" src=\"" + getAvatarUrl(getDiscordID(session)) + "\" height=\"50\" width=\"50\">\n" : "") +
                            "                  <li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"></li>\n" +
                            "               </ul>\n" +
                            "            </div>\n" +
                            "         </div>\n" +
                            "      </nav>\n" +
                            "      <header class=\"masthead bg-secondary text-white text-center\">\n" +
                            "         <h1>Your Data couldn't been deleted!</h1>\n" +
                            "         <h2 class=\"font-weight-light mb-0\">Are you trying to bypass our System?</h2>\n" +
                            "      </header>\n" +
                            "      <section id = \"features\" class = \"bg-tertiary\">\n" +
                            "         <h2 class=\"text-uppercase text-center text-secondary\">Informations</h2>\n" +
                            "         <div class=\"container\">\n<br/><h2 class=\"font-weight-light mb-0 text-center\"> You have been loggedout because we are not sure if you are permitted </h2><br/><h3 class=\"font-weight-light mb-0 text-center\"> If you think this was a false flag please contact us dxssucuk@hotmail.com! </h3></div>\n" +
                            "      </section>\n" +
                            "      <footer class=\"footer text-center\">\n" +
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
                            "                     <li class=\"list-inline-item\"><a class=\"btn btn-outline-light btn-social text-center rounded-circle\" role=\"button\" href=\"mailto:prestigemaster@hotmail.com\"><i class=\"fas fa-envelope\"></i></a></li>\n" +
                            "                  </ul>\n" +
                            "               </div>\n" +
                            "               <div class=\"col-md-4\">\n" +
                            "                  <h4 class=\"text-uppercase mb-4\">Special Links</h4>\n" +
                            "                  <p class=\"lead mb-0\"><a href=\"ree6.de/inforeq.html\" target = \"_blank\">Data Information Request</a></p>\n" +
                            "                  <p class=\"lead mb-0\"><a href=\"ree6.de/deletereq.html\" target = \"_blank\">Data Remove Request</a></p>\n" +
                            "               </div>\n" +
                            "            </div>\n" +
                            "         </div>\n" +
                            "      </footer>" + "</body></html>\n");
                }
            }
        }

        if (parms.get("logout") != null) {
            if (sqlWorker.getAuthToken(getGuildID(session)).equalsIgnoreCase(getAuthToken(session))) {
                sqlWorker.deleteAuthToken(getGuildID(session));
            }
            session.getCookies().delete("ree6_login");
            isloggedin = false;
        }

        String msg = "<html lang=\"de\">\n" +
                "   <head>\n" +
                "      <!-- Global site tag (gtag.js) - Google Analytics -->\n" +
                "      <script async src=\"https://www.googletagmanager.com/gtag/js?id=G-0LNY8PVZFC\"></script>\n" +
                "      <script>\n" +
                "         window.dataLayer = window.dataLayer || [];\n" +
                "         function gtag(){dataLayer.push(arguments);}\n" +
                "         gtag('js', new Date());\n" +
                "\n" +
                "         gtag('config', 'G-0LNY8PVZFC');\n" +
                "      </script>\n" +
                "      <script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':\n" +
                "         new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],\n" +
                "         j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=\n" +
                "         'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);\n" +
                "         })(window,document,'script','dataLayer','GTM-TBN3WRL');\n" +
                "      </script>\n" +
                "      <script data-ad-client=\"ca-pub-5270328367815951\" async src=\"https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js\"></script>\n" +
                "      <meta charset=\"utf-8\">\n" +
                "      <title>Ree6</title>\n" +
                "      <link rel=\"icon\" type=\"image/png\" href=\"https://utils.ree6.de/img/ree.png\">\n" +
                "      <link rel=\"stylesheet\" href=\"https://utils.ree6.de/css/bootstrap.css\">\n" +
                "      <link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Montserrat:400,700\">\n" +
                "      <link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic\">\n" +
                "      <link rel=\"stylesheet\" href=\"https://use.fontawesome.com/releases/v5.12.0/css/all.css\">\n" +
                "      <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n" +
                "   </head>\n" +
                "   <body id=\"page-top\">\n" +
                "      <!-- Google Tag Manager (noscript) -->\n" +
                "      <noscript><iframe src=\"https://www.googletagmanager.com/ns.html?id=GTM-TBN3WRL\"\n" +
                "         height=\"0\" width=\"0\" style=\"display:none;visibility:hidden\"></iframe></noscript>\n" +
                "      <!-- End Google Tag Manager (noscript) -->\n" +
                "      <nav class=\"navbar navbar-light navbar-expand-lg fixed-top bg-secondary text-uppercase\" id=\"mainNav\">\n" +
                "         <div class=\"container\">\n" +
                "            <a class=\"navbar-brand js-scroll-trigger\" href=\"#page-top\">Ree6</a><button data-toggle=\"collapse\" data-target=\"#navbarResponsive\" class=\"navbar-toggler navbar-toggler-right text-uppercase bg-primary text-white rounded\" aria-controls=\"navbarResponsive\"\n" +
                "               aria-expanded=\"false\" aria-label=\"Toggle navigation\"><i class=\"fa fa-bars\"></i></button>\n" +
                "            <div class=\"collapse navbar-collapse\" id=\"navbarResponsive\">\n" +
                "               <ul class=\"nav navbar-nav ml-auto\">\n" +
                "                  <li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"><a class=\"nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger\" href=\"https://ree6.de\">Home</a></li>\n" +
                "                  " + (isloggedin ? "<li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"><a class=\"nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger\" href=\"?logout\">Logout</a></li>" : "") + "\n" +
                "                  " + (isloggedin ? "<img class=\"img-fluid mb-5\" src=\"" + getAvatarUrl(getDiscordID(session)) + "\" height=\"50\" width=\"50\">\n" : "") +
                "                  <li class=\"nav-item mx-0 mx-lg-1\" role=\"presentation\"></li>\n" +
                "               </ul>\n" +
                "            </div>\n" +
                "         </div>\n" +
                "      </nav>\n" +
                "      <header class=\"masthead bg-secondary text-white text-center\">\n" +
                "         <h1>" + titleheader + "</h1>\n" +
                "         <h2 class=\"font-weight-light mb-0\">" + titlefooter + "</h2>\n" +
                "      </header>\n" +
                "      <section id = \"features\" class = \"bg-tertiary\">\n" +
                "         <h2 class=\"text-uppercase text-center text-secondary\">Informations</h2>\n" +
                (isloggedin ? "         <hr class=\"heart-dark mb-5\">\n" +
                        "         <div class=\"container\">\n" +
                        "            <div class=\"row\">\n" +
                        "               <div class=\"col-md-6 col-lg-4\">\n" +
                        "                  <img class=\"img-fluid mb-5\" src=\"https://utils.ree6.de/img/welcome.png\" height=\"200\" width=\"200\">\n" +
                        "                  <h1>Welcome Message</h1>\n" +
                        "                  <br  />\n" +
                        "                  <p class=\"lead mb-4\">" + (isloggedin ? (sqlWorker.hasWelcomeSetuped(getGuildID(session)) ? "Your current Welcome Message:<br/>" + sqlWorker.getMessage(getGuildID(session)) : "Not setuped!") : "Not loggedin") + "</p>\n" +
                        "               </div>\n" +
                        "               <div class=\"col-md-6 col-lg-4\">\n" +
                        "                  <img class=\"img-fluid mb-5\" src=\"https://utils.ree6.de/img/chatprot.png\" height=\"200\" width=\"200\">\n" +
                        "                  <h1>Chatprotector</h1>\n" +
                        "                  <br  />\n" +
                        "                  <p class=\"lead mb-4\">" + (isloggedin ? sqlWorker.hasChatProtectorSetuped(getGuildID(session)) ? "Current blacklisted Words:<br/>" + getFromArray(sqlWorker.getChatProtector(getGuildID(session))) : "Not setuped!" : "Not loggedin") + "</p>\n" +
                        "               </div>\n" +
                        "               <div class=\"col-md-6 col-lg-4\">\n" +
                        "                  <img class=\"img-fluid mb-5\" src=\"https://utils.ree6.de/img/news.png\" height=\"200\" width=\"200\">\n" +
                        "                  <h1>NewsSystem</h1>\n" +
                        "                  <p class=\"lead mb-4\">" + (sqlWorker.hasNewsSetuped(getGuildID(session)) ? "Yes the NewsSystem is setuped!" : "Not setuped!") + "</p>\n" +
                        "               </div>\n" +
                        "               <div class=\"col-md-6 col-lg-4\">\n" +
                        "                  <img class=\"img-fluid mb-5\" src=\"https://utils.ree6.de/img/log.png\" height=\"200\" width=\"200\">\n" +
                        "                  <h1>LogSystem</h1>\n" +
                        "                  <p class=\"lead mb-4\">" + (sqlWorker.hasLogSetuped(getGuildID(session)) ? "Yes the Advanced LogSystem is setuped!" : "Not setuped!") + "</p>\n" +
                        "               </div>\n" +
                        "               <div class=\"col-md-6 col-lg-4\">\n" +
                        "                  <img class=\"img-fluid mb-5\" src=\"https://utils.ree6.de/img/r6.png\" height=\"100\" width=\"100\">\n" +
                        "                  <h1>Rainbow-Matesearcher</h1>\n" +
                        "                  <p class=\"lead mb-4\">" + (sqlWorker.hasRainbowSetuped(getGuildID(session)) ? "Yes the Rainbow-Matesearcher is setuped for you Server!" : "Not setuped!") + "</p>\n" +
                        "               </div>\n" +
                        "               <div class=\"col-md-6 col-lg-4\">\n" +
                        "                  <img class=\"img-fluid mb-5\" src=\"https://utils.ree6.de/img/mute.png\" height=\"200\" width=\"200\">\n" +
                        "                  <h1>MuteSystem</h1>\n" +
                        "                  <p class=\"lead mb-4\">" + (sqlWorker.hasMuteSetuped(getGuildID(session)) ? "Yes a MuteRole has been added! (" + sqlWorker.getMuteRoleID(getGuildID(session)) + ")" : "Not setuped!") + "</p>\n" +
                        "               </div>\n" +
                        "               <div class=\"col-md-6 col-lg-4\">\n" +
                        "                  <img class=\"img-fluid mb-5\" src=\"https://utils.ree6.de/img/twitch.png\" height=\"200\" width=\"200\">\n" +
                        "                  <h1>Twitch-Notifier</h1>\n" +
                        "                  <p class=\"lead mb-4\">" + (sqlWorker.hasTwitchNotifySetuped(getGuildID(session)) ? "Setuped for:<br/>" + getFromArray(sqlWorker.getTwitchNotifier(getGuildID(session))) : "Not setuped!") + "</p>\n" +
                        "               </div>\n" +
                        "               <div class=\"col-md-6 col-lg-4\">\n" +
                        "                  <img class=\"img-fluid mb-5\" src=\"https://utils.ree6.de/img/invite.png\" height=\"200\" width=\"200\">\n" +
                        "                  <h1>Logged Invites</h1>\n" +
                        "                  <p class=\"lead mb-4\">" + (sqlWorker.getInviteCount(getGuildID(session)) > 0 ? "Save Invites:<br/>" + sqlWorker.getInviteCount(getGuildID(session)) : "No Invite saved!") + "</p>\n" +
                        "               </div>\n" +
                        "            </div>\n" +
                        "         </div>\n" : "<div class=\"container\">\n<br/><h2 class=\"font-weight-light mb-0 text-center\"> Please Login! </h2><br/><h3 class=\"font-weight-light mb-0 text-center\"> To login send in a Admin Channel the Message ree!webinterface! </h3><h4 class=\"font-weight-light mb-0 text-center\">(Becareful the Link send by Ree6 can be used by everyone! DO NOT SHARE IT!)</h4></div>\n") +
                (isloggedin ? "<section id = \"invite\" class = \"bg-tertiary text-white mb-0\">\n" +
                        "         <div class=\"container\">\n" +
                        "            <h2 class=\"text-uppercase text-center text-white\">Delete All My Data</h2>\n" +
                        "            <hr class=\"star-light mb-5\">\n" +
                        "            <div class=\"text-center mt-4\">\n" +
                        "               <p class=\"lead mb-0\"><a class=\"btn btn-outline-light text-center\" role=\"button\" href = \"?deleteAll\">Click here to Delete</a></p>\n" +
                        "            </div>\n" +
                        "         </div>\n" +
                        "      </section>" : "")+
                "      </section>\n" +
                "      <footer class=\"footer text-center\">\n" +
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
                "                     <li class=\"list-inline-item\"><a class=\"btn btn-outline-light btn-social text-center rounded-circle\" role=\"button\" href=\"mailto:prestigemaster@hotmail.com\"><i class=\"fas fa-envelope\"></i></a></li>\n" +
                "                  </ul>\n" +
                "               </div>\n" +
                "               <div class=\"col-md-4\">\n" +
                "                  <h4 class=\"text-uppercase mb-4\">Special Links</h4>\n" +
                "                  <p class=\"lead mb-0\"><a href=\"ree6.de/inforeq.html\" target = \"_blank\">Data Information Request</a></p>\n" +
                "                  <p class=\"lead mb-0\"><a href=\"ree6.de/deletereq.html\" target = \"_blank\">Data Remove Request</a></p>\n" +
                "               </div>\n" +
                "            </div>\n" +
                "         </div>\n" +
                "      </footer>\n";
        return newFixedLengthResponse(msg + "</body></html>\n");
    }

    public String getFromArray(ArrayList<String> d) {
        String end = "";

        if (d.isEmpty()) {
            return "Empty (maybe not setuped)";
        }

        int kk = 0;
        for (String s : d) {
            if (kk != (d.size() - 1)) {
                end += s + ", ";
                kk++;
            } else {
                end += s;
            }
        }

        return end;

    }

    public String getGuildID(IHTTPSession session) {
        return (Crypter.de(session.getCookies().read("ree6_login")) != null) ? Crypter.de(session.getCookies().read("ree6_login")).split("-")[0] != null ? Crypter.de(session.getCookies().read("ree6_login")).split("-")[0] : "0" : "0";
    }

    public String getGuildID(String base64String) {
        return (Crypter.de(base64String) != null) ? Crypter.de(base64String).split("-")[0] != null ? Crypter.de(base64String).split("-")[0] : "0" : "0";
    }

    public String getAuthToken(IHTTPSession session) {
        return (Crypter.de(session.getCookies().read("ree6_login")) != null) ? Crypter.de(session.getCookies().read("ree6_login")).split("-")[1].split(":")[0] != null ? Crypter.de(session.getCookies().read("ree6_login")).split("-")[1].split(":")[0] : "0" : "0";
    }

    public String getAuthToken(String base64String) {
        return (Crypter.de(base64String) != null) ? Crypter.de(base64String).split("-")[1].split(":")[0] != null ? Crypter.de(base64String).split("-")[1].split(":")[0] : "0" : "0";
    }

    public String getDiscordID(IHTTPSession session) {
        return (Crypter.de(session.getCookies().read("ree6_login")) != null) ? Crypter.de(session.getCookies().read("ree6_login")).split("-")[1].split(":")[1] != null ? Crypter.de(session.getCookies().read("ree6_login")).split("-")[1].split(":")[1] : "0" : "0";
    }

    public String getDiscordID(String base64String) {
        return (Crypter.de(base64String) != null) ? Crypter.de(base64String).split("-")[1].split(":")[1] != null ? Crypter.de(base64String).split("-")[1].split(":")[1] : "0" : "0";
    }

    public static String getAvatarUrl(String userID) {
        JSONObject js = JSONApi.GetData(Requests.GET, "https://discord.com/api/users/" + userID, "Bot ODExOTU3MTI4NzMzMzkyOTU2.YC5wAA.NporGJoAEDSi1uPIth9VqE6a20k");
        return (js.has("avatar") ? "https://cdn.discordapp.com/avatars/" + userID + "/" + js.getString("avatar") : "https://preview.redd.it/nx4jf8ry1fy51.gif?format=png8&s=a5d51e9aa6b4776ca94ebe30c9bb7a5aaaa265a6");
    }

    public static String getUsername(String userID) {
        JSONObject js = JSONApi.GetData(Requests.GET, "https://discord.com/api/users/" + userID, "Bot ODExOTU3MTI4NzMzMzkyOTU2.YC5wAA.NporGJoAEDSi1uPIth9VqE6a20k");
        return (js.has("username") ? js.getString("username") : "Please reload");
    }

}
