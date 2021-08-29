package de.presti.ree6.webinterface.sql;



import de.presti.ree6.webinterface.main.Main;
import de.presti.ree6.webinterface.utils.Setting;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SQLWorker {

    //Leveling Chat

    public Long getXP(String gid, String uid) {
        String xp = "0";

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM Level WHERE GID='" + gid + "' AND UID='" + uid + "'");
                rs = st.executeQuery("SELECT * FROM Level WHERE GID='" + gid + "' AND UID='" + uid + "'");
            } catch (Exception ignore) {
                //ex.printStackTrace();
            }

            if (rs != null && rs.next()) {
                xp = rs.getString("XP");
            }

        } catch (Exception ignore) {
            //ex.printStackTrace();
        }

        return Long.parseLong(xp);
    }

    public boolean existsXP(String gid, String uid) {

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM Level WHERE GID='" + gid + "' AND UID='" + uid + "'");
                rs = st.executeQuery("SELECT * FROM Level WHERE GID='" + gid + "' AND UID='" + uid + "'");
            } catch (Exception ignore) {
                //ex.printStackTrace();
            }

            return rs != null && rs.next();

        } catch (Exception ignore) {
            //ex.printStackTrace();
        }

        return false;
    }

    public void addXP(String gid, String uid, int addxp) throws SQLException {

        addxp += getXP(gid, uid);

        if (existsXP(gid, uid)) {
            Main.sqlConnector.query("UPDATE Level SET XP='" + addxp + "' WHERE GID='" + gid + "' AND UID='" + uid + "'");
        } else {
            Main.sqlConnector.query("INSERT INTO Level (GID, UID, XP) VALUES ('" + gid + "', '" + uid + "', '" + addxp + "');");
        }
    }

    public ArrayList<String> getTop(int amount, String gid) {

        ArrayList<String> ids = new ArrayList<>();

        try {

            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM `Level` WHERE GID='" + gid + "' ORDER BY cast(xp as unsigned) DESC LIMIT " + amount);
                rs = st.executeQuery("SELECT * FROM `Level` WHERE GID='" + gid + "' ORDER BY cast(xp as unsigned) DESC LIMIT " + amount);
            } catch (Exception ignore) {
            }

            while (rs != null && rs.next()) {
                ids.add(rs.getString("UID"));
            }

        } catch (Exception ignore) {
        }

        return ids;
    }

    //Leveling VoiceChannel

    public Long getXPVC(String gid, String uid) {
        String xp = "0";

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM VCLevel WHERE GID='" + gid + "' AND UID='" + uid + "'");
                rs = st.executeQuery("SELECT * FROM VCLevel WHERE GID='" + gid + "' AND UID='" + uid + "'");
            } catch (Exception ignore) {
                //ex.printStackTrace();
            }

            if (rs != null && rs.next()) {
                xp = rs.getString("XP");
            }

        } catch (Exception ignore) {
            //ex.printStackTrace();
        }

        return Long.parseLong(xp);
    }

    public boolean existsXPVC(String gid, String uid) {

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM VCLevel WHERE GID='" + gid + "' AND UID='" + uid + "'");
                rs = st.executeQuery("SELECT * FROM VCLevel WHERE GID='" + gid + "' AND UID='" + uid + "'");
            } catch (Exception ignore) {
                //ex.printStackTrace();
            }

            return rs != null && rs.next();

        } catch (Exception ignore) {
            //ex.printStackTrace();
        }

        return false;
    }

    public void addXPVC(String gid, String uid, int addxp) throws SQLException {

        addxp += getXPVC(gid, uid);

        if (existsXPVC(gid, uid)) {
            Main.sqlConnector.query("UPDATE VCLevel SET XP='" + addxp + "' WHERE GID='" + gid + "' AND UID='" + uid + "'");
        } else {
            Main.sqlConnector.query("INSERT INTO VCLevel (GID, UID, XP) VALUES ('" + gid + "', '" + uid + "', '" + addxp + "');");
        }
    }

    public ArrayList<String> getTopVC(int amount, String gid) {

        ArrayList<String> ids = new ArrayList<>();

        try {

            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM `VCLevel` WHERE GID='" + gid + "' ORDER BY cast(xp as unsigned) DESC LIMIT " + amount);
                rs = st.executeQuery("SELECT * FROM `VCLevel` WHERE GID='" + gid + "' ORDER BY cast(xp as unsigned) DESC LIMIT " + amount);
            } catch (Exception ignore) {
            }

            while (rs != null && rs.next()) {
                ids.add(rs.getString("UID"));
            }

        } catch (Exception ignore) {
        }

        return ids;
    }

    //Logging

    public void setLogWebhook(String gid, String cid, String token) throws SQLException {
        if (hasLogSetuped(gid)) {

            String[] d = getLogWebhook(gid);

            Main.sqlConnector.query("DELETE FROM LogWebhooks WHERE GID='" + gid + "'");
        }
        Main.sqlConnector.query("INSERT INTO LogWebhooks (GID, CID, TOKEN) VALUES ('" + gid + "', '" + cid + "', '" + token + "');");
    }

    public boolean hasLogSetuped(String gid) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM LogWebhooks WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM LogWebhooks WHERE GID='" + gid + "'");
            } catch (Exception ignore) {
            }

            return rs != null && rs.next();

        } catch (Exception ignore) {
        }
        return false;
    }

    public String[] getLogWebhook(String gid) {
        if (hasLogSetuped(gid)) {
            try {
                PreparedStatement st;
                ResultSet rs = null;

                try {
                    st = Main.sqlConnector.con.prepareStatement("SELECT * FROM LogWebhooks WHERE GID='" + gid + "'");
                    rs = st.executeQuery("SELECT * FROM LogWebhooks WHERE GID='" + gid + "'");
                } catch (Exception ignore) {
                }

                if (rs != null && rs.next()) {
                    return new String[]{rs.getString("CID"), rs.getString("TOKEN")};
                }

            } catch (Exception ignore) {
            }
        }
        return new String[]{"Error", "Not setuped!"};
    }


    //Welcome

    public void setWelcomeWebhook(String gid, String cid, String token) throws SQLException {
        if (hasWelcomeSetuped(gid)) {

            String[] d = getWelcomeWebhook(gid);

            Main.sqlConnector.query("DELETE FROM WelcomeWebhooks WHERE GID='" + gid + "'");
        }
        Main.sqlConnector.query("INSERT INTO WelcomeWebhooks (GID, CID, TOKEN) VALUES ('" + gid + "', '" + cid + "', '" + token + "');");
    }

    public boolean hasWelcomeSetuped(String gid) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM WelcomeWebhooks WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM WelcomeWebhooks WHERE GID='" + gid + "'");
            } catch (Exception ignore) {
            }

            if (rs != null && rs.next()) {
                return true;
            }

        } catch (Exception ignore) {
        }
        return false;
    }

    public String[] getWelcomeWebhook(String gid) {
        if (hasWelcomeSetuped(gid)) {
            try {
                PreparedStatement st;
                ResultSet rs = null;

                try {
                    st = Main.sqlConnector.con.prepareStatement("SELECT * FROM WelcomeWebhooks WHERE GID='" + gid + "'");
                    rs = st.executeQuery("SELECT * FROM WelcomeWebhooks WHERE GID='" + gid + "'");
                } catch (Exception ignore) {
                }

                if (rs != null && rs.next()) {
                    return new String[]{rs.getString("CID"), rs.getString("TOKEN")};
                }

            } catch (Exception ignore) {
            }
        }
        return new String[]{"Error", "Not setuped!"};
    }

    //Mute

    public void setMuteRole(String gid, String rid) throws SQLException {
        if (hasMuteSetuped(gid)) {
            Main.sqlConnector.query("UPDATE MuteRoles SET RID='" + rid + "' WHERE GID='" + gid + "'");
        } else {
            Main.sqlConnector.query("INSERT INTO MuteRoles (GID, RID) VALUES ('" + gid + "', '" + rid + "');");
        }
    }

    public boolean hasMuteSetuped(String gid) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM MuteRoles WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM MuteRoles WHERE GID='" + gid + "'");
            } catch (Exception ignore) {
            }

            if (rs != null && rs.next()) {
                return true;
            }

        } catch (Exception ignore) {
        }
        return false;
    }

    public String getMuteRoleID(String gid) {
        if (hasMuteSetuped(gid)) {
            try {
                PreparedStatement st;
                ResultSet rs = null;

                try {
                    st = Main.sqlConnector.con.prepareStatement("SELECT * FROM MuteRoles WHERE GID='" + gid + "'");
                    rs = st.executeQuery("SELECT * FROM MuteRoles WHERE GID='" + gid + "'");
                } catch (Exception ignore) {
                }

                if (rs != null && rs.next()) {
                    return rs.getString("RID");
                }

            } catch (Exception ignore) {
            }
        }
        return "Error";
    }

    //ChatLevelReward

    public boolean hasChatLevelReward(String gid) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM ChatLevelAutoRoles WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM ChatLevelAutoRoles WHERE GID='" + gid + "'");
            } catch (Exception x) {
                x.printStackTrace();
            }

            return (rs != null && rs.next());

        } catch (Exception ignore) {
        }

        return false;
    }

    public HashMap<Integer, String> getChatLevelRewards(String gid) {

        HashMap<Integer, String> rewards = new HashMap<>();

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM ChatLevelAutoRoles WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM ChatLevelAutoRoles WHERE GID='" + gid + "'");
            } catch (Exception x) {
                x.printStackTrace();
            }

            while (rs != null && rs.next()) {
                if (!rewards.containsKey(Integer.valueOf(rs.getString("LVL")))) {
                    rewards.put(Integer.parseInt(rs.getString("LVL")), rs.getString("RID"));
                }
            }

        } catch (Exception ignore) {
        }

        return rewards;
    }

    public void addChatLevelReward(String gid, int level, String rid) {
        Main.sqlConnector.query("INSERT INTO ChatLevelAutoRoles (GID, RID, LVL) VALUES ('" + gid + "', '" + rid + "','" + level + "');");
    }

    public void removeChatLevelReward(String gid, int level) {
        Main.sqlConnector.query("DELETE FROM ChatLevelAutoRoles WHERE GID='" + gid + "' AND LVL='" + level + "'");
    }


    //VoiceLevelReward

    public boolean hasVoiceLevelReward(String gid) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM VCLevelAutoRoles WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM VCLevelAutoRoles WHERE GID='" + gid + "'");
            } catch (Exception x) {
                x.printStackTrace();
            }

            return (rs != null && rs.next());

        } catch (Exception ignore) {
        }

        return false;
    }

    public HashMap<Integer, String> getVoiceLevelRewards(String gid) {

        HashMap<Integer, String> rewards = new HashMap<>();

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM VCLevelAutoRoles WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM VCLevelAutoRoles WHERE GID='" + gid + "'");
            } catch (Exception x) {
                x.printStackTrace();
            }

            while (rs != null && rs.next()) {
                if (!rewards.containsKey(Integer.valueOf(rs.getString("LVL")))) {
                    rewards.put(Integer.parseInt(rs.getString("LVL")), rs.getString("RID"));
                }
            }

        } catch (Exception ignore) {
        }

        return rewards;
    }

    public void addVoiceLevelReward(String gid, int level, String rid) {
        Main.sqlConnector.query("INSERT INTO VCLevelAutoRoles (GID, RID, LVL) VALUES ('" + gid + "', '" + rid + "','" + level + "');");
    }

    public void removeVoiceLevelReward(String gid, int level) {
        Main.sqlConnector.query("DELETE FROM VCLevelAutoRoles WHERE GID='" + gid + "' AND LVL='" + level + "'");
    }

    //Autorole

    public boolean hasAutoRoles(String gid) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM AutoRoles WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM AutoRoles WHERE GID='" + gid + "'");
            } catch (Exception x) {
                x.printStackTrace();
            }

            return (rs != null && rs.next());

        } catch (Exception ignore) {
        }

        return false;
    }

    public ArrayList<String> getAutoRoleIDs(String gid) {

        ArrayList<String> roles = new ArrayList<>();

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM AutoRoles WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM AutoRoles WHERE GID='" + gid + "'");
            } catch (Exception x) {
                x.printStackTrace();
            }

            while (rs != null && rs.next()) {
                roles.add(rs.getString("RID"));
            }

        } catch (Exception ignore) {
        }

        return roles;
    }

    public void addAutoRole(String gid, String rid) {
        Main.sqlConnector.query("INSERT INTO AutoRoles (GID, RID) VALUES ('" + gid + "', '" + rid + "');");
    }

    public void removeAutoRole(String gid, String rid) {
        Main.sqlConnector.query("DELETE FROM AutoRoles WHERE GID='" + gid + "' AND RID='" + rid + "'");
    }

    //Invite

    public boolean existsInvite(String gid, String code, String creator) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM Invites WHERE GID='" + gid + "' AND UID='" + creator + "' AND CODE='" + code + "'");
                rs = st.executeQuery("SELECT * FROM Invites WHERE GID='" + gid + "' AND UID='" + creator + "' AND CODE='" + code + "'");
            } catch (Exception x) {
                x.printStackTrace();
            }

            return rs != null && rs.next();

        } catch (Exception ignore) {
        }

        return false;
    }

    public int getInviteCount(String gid) {
        int i = 0;
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM Invites WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM Invites WHERE GID='" + gid + "'");
            } catch (Exception x) {
                x.printStackTrace();
            }

            while (rs != null && rs.next()) {
                i++;
            }

        } catch (Exception ignore) {
        }

        return i;
    }

    public void deleteAllMyData(String gid) {
        Main.sqlConnector.query("DELETE FROM Invites WHERE GID='" + gid + "'");
        Main.sqlConnector.query("DELETE FROM AutoRoles WHERE GID='" + gid + "'");
        Main.sqlConnector.query("DELETE FROM WelcomeWebhooks WHERE GID='" + gid + "'");
        Main.sqlConnector.query("DELETE FROM LogWebhooks WHERE GID='" + gid + "'");
        Main.sqlConnector.query("DELETE FROM NewsWebhooks WHERE GID='" + gid + "'");
        Main.sqlConnector.query("DELETE FROM JoinMessage WHERE GID='" + gid + "'");
        Main.sqlConnector.query("DELETE FROM MuteRoles WHERE GID='" + gid + "'");
        Main.sqlConnector.query("DELETE FROM ChatProtector WHERE GID='" + gid + "'");
    }

    //News

    public String[] getNewswebhook(String gid) {
        if (hasNewsSetuped(gid)) {
            try {
                PreparedStatement st;
                ResultSet rs = null;

                try {
                    st = Main.sqlConnector.con.prepareStatement("SELECT * FROM NewsWebhooks WHERE GID='" + gid + "'");
                    rs = st.executeQuery("SELECT * FROM NewsWebhooks WHERE GID='" + gid + "'");
                } catch (Exception ignore) {
                }

                if (rs != null && rs.next()) {
                    return new String[]{rs.getString("CID"), rs.getString("TOKEN")};
                }

            } catch (Exception ignore) {
            }
        }
        return new String[]{"Error", "Not setuped!"};
    }

    public boolean hasNewsSetuped(String gid) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM NewsWebhooks WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM NewsWebhooks WHERE GID='" + gid + "'");
            } catch (Exception ignore) {
            }

            return rs != null && rs.next();

        } catch (Exception ignore) {
        }
        return false;
    }

    //Config

    public void setMessage(String gid, String text) {
        if (hasMessageSetuped(gid)) {
            Main.sqlConnector.query("DELETE FROM JoinMessage WHERE GID='" + gid + "'");
        }
        Main.sqlConnector.query("INSERT INTO JoinMessage (GID, MSG) VALUES ('" + gid + "', '" + text + "');");
    }

    public String getMessage(String gid) {
        if (hasMessageSetuped(gid)) {
            try {
                PreparedStatement st;
                ResultSet rs = null;

                try {
                    st = Main.sqlConnector.con.prepareStatement("SELECT * FROM JoinMessage WHERE GID='" + gid + "'");
                    rs = st.executeQuery("SELECT * FROM JoinMessage WHERE GID='" + gid + "'");
                } catch (Exception ignore) {
                }

                if (rs != null && rs.next()) {
                    return rs.getString("MSG");
                }

            } catch (Exception ignore) {
            }
        }
        return "Welcome %user_mention%!\nWe wish you a great time on %guild_name%";
    }

    public boolean hasMessageSetuped(String gid) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM JoinMessage WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM JoinMessage WHERE GID='" + gid + "'");
            } catch (Exception ignore) {
            }

            return rs != null && rs.next();

        } catch (Exception ignore) {
        }
        return false;
    }

    //ChatProtector
    public boolean hasChatProtectorSetuped(String gid) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM ChatProtector WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM ChatProtector WHERE GID='" + gid + "'");
            } catch (Exception ignore) {
            }

            return rs != null && rs.next();

        } catch (Exception ignore) {
        }
        return false;
    }

    public boolean hasChatProtectorWord(String gid, String word) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM ChatProtector WHERE GID='" + gid + "'AND WORD='" + word + "'");
                rs = st.executeQuery("SELECT * FROM ChatProtector WHERE GID='" + gid + "' AND WORD='" + word + "'");
            } catch (Exception ignore) {
            }

            return rs != null && rs.next();

        } catch (Exception ignore) {
        }
        return false;
    }


    public void addChatProtector(String gid, String word) {
        if (hasChatProtectorWord(gid, word)) {
            Main.sqlConnector.query("DELETE FROM ChatProtector WHERE GID='" + gid + "' AND WORD='" + word + "'");
        }
        Main.sqlConnector.query("INSERT INTO ChatProtector (GID, WORD) VALUES ('" + gid + "', '" + word + "');");
    }

    public void removeChatProtector(String gid, String word) {
        if (hasChatProtectorWord(gid, word)) {
            Main.sqlConnector.query("DELETE FROM ChatProtector WHERE GID='" + gid + "' AND WORD='" + word + "'");
        }
    }

    public ArrayList<String> getChatProtector(String gid) {
        ArrayList<String> chatprot = new ArrayList<>();
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM ChatProtector WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM ChatProtector WHERE GID='" + gid + "'");
            } catch (Exception ignore) {
            }

            while (rs != null && rs.next()) {
                chatprot.add(rs.getString("WORD"));
            }

        } catch (Exception ignore) {
        }

        return chatprot;
    }

    //Rainbow

    public String[] getRainbowHooks(String gid) {
        if (hasRainbowSetuped(gid)) {
            try {
                PreparedStatement st;
                ResultSet rs = null;

                try {
                    st = Main.sqlConnector.con.prepareStatement("SELECT * FROM RainbowWebhooks WHERE GID='" + gid + "'");
                    rs = st.executeQuery("SELECT * FROM RainbowWebhooks WHERE GID='" + gid + "'");
                } catch (Exception ignore) {
                }

                if (rs != null && rs.next()) {
                    return new String[]{rs.getString("CID"), rs.getString("TOKEN")};
                }

            } catch (Exception ignore) {
            }
        }
        return new String[]{"Error", "Not setuped!"};
    }

    public boolean hasRainbowSetuped(String gid) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM RainbowWebhooks WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM RainbowWebhooks WHERE GID='" + gid + "'");
            } catch (Exception ignored) {
            }

            return rs != null && rs.next();

        } catch (Exception ignored) {
        }
        return false;
    }

    //Twitch

    public ArrayList<String[]> getTwitchNotifyWebhooks(String gid) {
        ArrayList<String[]> webhooks = new ArrayList<>();
        if (hasTwitchNotifySetuped(gid)) {
            try {
                PreparedStatement st;
                ResultSet rs = null;

                try {
                    st = Main.sqlConnector.con.prepareStatement("SELECT * FROM TwitchNotify WHERE GID='" + gid + "'");
                    rs = st.executeQuery("SELECT * FROM TwitchNotify WHERE GID='" + gid + "'");
                } catch (Exception ignore) {
                }

                while (rs != null && rs.next()) {
                    webhooks.add(new String[]{rs.getString("CID"), rs.getString("TOKEN")});
                }

            } catch (Exception ignore) {
            }
        }
        return webhooks;
    }

    public ArrayList<String> getTwitchNotifier(String gid) {
        ArrayList<String> names = new ArrayList<>();
        if (hasTwitchNotifySetuped(gid)) {
            try {
                PreparedStatement st;
                ResultSet rs = null;

                try {
                    st = Main.sqlConnector.con.prepareStatement("SELECT * FROM TwitchNotify WHERE GID='" + gid + "'");
                    rs = st.executeQuery("SELECT * FROM TwitchNotify WHERE GID='" + gid + "'");
                } catch (Exception ignore) {
                }

                while (rs != null && rs.next()) {
                    names.add(rs.getString("NAME"));
                }

            } catch (Exception ignore) {
            }
        }
        return names;
    }

    public ArrayList<String> getAllTwitchNotifyUsers() {
        ArrayList<String> names = new ArrayList<>();
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM TwitchNotify");
                rs = st.executeQuery("SELECT * FROM TwitchNotify");
            } catch (Exception ignore) {
            }

            while (rs != null && rs.next()) {
                names.add(rs.getString("NAME"));
            }

        } catch (Exception ignore) {
        }
        return names;
    }

    public String[] getTwitchNotifyWebhook(String gid, String name) {
        if (hasTwitchNotifySetuped(gid)) {
            try {
                PreparedStatement st;
                ResultSet rs = null;

                try {
                    st = Main.sqlConnector.con.prepareStatement("SELECT * FROM TwitchNotify WHERE GID='" + gid + "' AND NAME='" + name + "'");
                    rs = st.executeQuery("SELECT * FROM TwitchNotify WHERE GID='" + gid + "' AND NAME='" + name + "'");
                } catch (Exception ignore) {
                }

                if (rs != null && rs.next()) {
                    return new String[]{rs.getString("CID"), rs.getString("TOKEN")};
                }

            } catch (Exception ignore) {
            }
        }
        return new String[]{"Error", "Not setuped!"};
    }

    public String[] getTwitchNotifyWebhookByName(String name) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM TwitchNotify WHERE NAME='" + name + "'");
                rs = st.executeQuery("SELECT * FROM TwitchNotify WHERE NAME='" + name + "'");
            } catch (Exception ignore) {
            }

            if (rs != null && rs.next()) {
                return new String[]{rs.getString("CID"), rs.getString("TOKEN")};
            }

        } catch (Exception ignore) {
        }
        return new String[]{"Error", "Not setuped!"};
    }


    public void addTwitchNotify(String gid, String name, String cid, String token) {
        if (hasTwitchNotifySetupedForUser(gid, name)) {
            String[] d = getTwitchNotifyWebhook(gid, name);


            Main.sqlConnector.query("DELETE FROM TwitchNotify WHERE GID='" + gid + "' AND NAME='" + name + "'");
        }
        Main.sqlConnector.query("INSERT INTO TwitchNotify (GID, NAME, CID, TOKEN) VALUES ('" + gid + "', '" + name + "','" + cid + "', '" + token + "');");
    }

    public void removeTwitchNotify(String gid, String name) {
        if (hasTwitchNotifySetupedForUser(gid, name)) {
            String[] d = getTwitchNotifyWebhook(gid, name);

            Main.sqlConnector.query("DELETE FROM TwitchNotify WHERE GID='" + gid + "' AND NAME='" + name + "'");
        }
    }

    public boolean hasTwitchNotifySetuped(String gid) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM TwitchNotify WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM TwitchNotify WHERE GID='" + gid + "'");
            } catch (Exception ignored) {
            }

            return rs != null && rs.next();

        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean hasTwitchNotifySetupedForUser(String gid, String name) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM TwitchNotify WHERE GID='" + gid + "' AND NAME='" + name + "'");
                rs = st.executeQuery("SELECT * FROM TwitchNotify WHERE GID='" + gid + "' AND NAME='" + name + "'");
            } catch (Exception ignored) {
            }

            return rs != null && rs.next();

        } catch (Exception ignored) {
        }
        return false;
    }

    //Webinterface Auth

    public String getAuthToken(String gid) {
        if (hasAuthToken(gid)) {
            try {
                PreparedStatement st;
                ResultSet rs = null;

                try {
                    st = Main.sqlConnector.con.prepareStatement("SELECT * FROM Webinterface WHERE GID='" + gid + "'");
                    rs = st.executeQuery("SELECT * FROM Webinterface WHERE GID='" + gid + "'");
                } catch (Exception ignore) {
                }

                if (rs != null && rs.next()) {
                    return rs.getString("AUTH");
                }

            } catch (Exception ignore) {
            }
        }
        return "0";
    }

    public void setAuthToken(String gid, String auth) {
        if (hasAuthToken(gid)) {
            Main.sqlConnector.query("DELETE FROM Webinterface WHERE GID='" + gid + "'");
        }
        Main.sqlConnector.query("INSERT INTO Webinterface (GID, AUTH) VALUES ('" + gid + "', '" + auth + "');");
    }

    public void deleteAuthToken(String gid) {
        if (hasAuthToken(gid)) {
            Main.sqlConnector.query("DELETE FROM Webinterface WHERE GID='" + gid + "'");
        }
    }

    public boolean hasAuthToken(String gid) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM Webinterface WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM Webinterface WHERE GID='" + gid + "'");
            } catch (Exception ignored) {
            }

            return rs != null && rs.next();

        } catch (Exception ignored) {
        }
        return false;
    }

    //Stats

    public boolean hasStatsGuild(String gid, String command) {

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM GuildStats WHERE GID='" + gid + "' AND COMMAND='" + command + "'");
                rs = st.executeQuery("SELECT * FROM GuildStats WHERE GID='" + gid + "' AND COMMAND='" + command + "'");
            } catch (Exception ignore) {
            }

            return rs != null && rs.next();

        } catch (Exception ignore) {
        }
        return false;
    }

    public boolean hasStatsCommand(String command) {

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM CommandStats WHERE COMMAND='" + command + "'");
                rs = st.executeQuery("SELECT * FROM CommandStats WHERE COMMAND='" + command + "'");
            } catch (Exception ignore) {
            }

            return rs != null && rs.next();

        } catch (Exception ignore) {
        }
        return false;
    }

    public HashMap<String, Long> getStatsFromGuild(String gid) {

        HashMap<String, Long> data = new HashMap<>();

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM GuildStats WHERE GID='" + gid + "' ORDER BY cast(uses as unsigned) DESC LIMIT 99");
                rs = st.executeQuery("SELECT * FROM GuildStats WHERE GID='" + gid + "' ORDER BY cast(uses as unsigned) DESC LIMIT 99");
            } catch (Exception ignore) {
            }

            while (rs != null && rs.next()) {
                data.put(rs.getString("COMMAND"), Long.parseLong(rs.getString("USES")));
            }

        } catch (Exception ignore) {
        }
        return data;
    }

    public Long getStatsFromCommand(String command) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM CommandStats WHERE COMMAND='" + command + "'");
                rs = st.executeQuery("SELECT * FROM CommandStats WHERE COMMAND='" + command + "'");
            } catch (Exception ignore) {
            }

            if (rs != null && rs.next()) {
                return Long.valueOf(rs.getString("USES"));
            }

        } catch (Exception ignore) {
        }
        return 1L;
    }

    public long getCommandStatsFromGuild(String gid, String command) {

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM GuildStats WHERE GID='" + gid + "' AND COMMAND='" + command + "'");
                rs = st.executeQuery("SELECT * FROM GuildStats WHERE GID='" + gid + "' AND COMMAND='" + command + "'");
            } catch (Exception ignore) {
            }

            if (rs != null && rs.next()) {
                return Long.parseLong(rs.getString("USES"));
            }

        } catch (Exception ignore) {
        }
        return 0L;
    }

    public HashMap<String, Long> getStatsForCommands() {

        HashMap<String, Long> data = new HashMap<>();

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM CommandStats ORDER BY cast(uses as unsigned) DESC LIMIT 99");
                rs = st.executeQuery("SELECT * FROM CommandStats ORDER BY cast(uses as unsigned) DESC LIMIT 99");
            } catch (Exception ignore) {
            }

            while (rs != null && rs.next()) {
                data.put(rs.getString("COMMAND"), Long.parseLong(rs.getString("USES")));
            }

        } catch (Exception ignore) {
        }
        return data;
    }

    //Setting-System

    public boolean hasSetting(String gid, String settingName) {

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM Settings WHERE GID='" + gid + "' AND NAME='" + settingName + "'");
                rs = st.executeQuery("SELECT * FROM Settings WHERE GID='" + gid + "' AND NAME='" + settingName + "'");
            } catch (Exception ignore) {
            }

            if (rs != null && rs.next()) {
                return true;
            } else {
                checkSettings(gid);
                return false;
            }

        } catch (Exception ignore) {
        }

        checkSettings(gid);

        return false;
    }

    public void checkSettings(String gid) {
        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM Settings WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM Settings WHERE GID='" + gid + "'");
            } catch (Exception ignore) {
            }

            if (rs == null || !rs.next()) {
                createSettings(gid);
            }

        } catch (Exception ignore) {
        }
    }

    public boolean settingExists(String gid, String settingName) {

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM Settings WHERE GID='" + gid + "' AND NAME='" + settingName + "'");
                rs = st.executeQuery("SELECT * FROM Settings WHERE GID='" + gid + "' AND NAME='" + settingName + "'");
            } catch (Exception ignore) {
            }

            return rs != null && rs.next();

        } catch (Exception ignore) {
        }
        return false;
    }

    public void setSetting(String gid, String name, Object value) {
        setSetting(gid, new Setting(name, value));
    }

    public void setSetting(String gid, Setting setting) {

        if (settingExists(gid, setting.getName()))
            Main.sqlConnector.query("UPDATE Settings SET VALUE='" + setting.getStringValue() + "' WHERE GID='" + gid + "' AND NAME='" + setting.getName() + "'");
        else
            Main.sqlConnector.query("INSERT INTO Settings (GID, NAME, VALUE) VALUES ('" + gid + "', '" + setting.getName() + "', '" + setting.getStringValue() + "');");
    }

    public Setting getSetting(String gid, String settingName) {
        Object value = null;

        if (!hasSetting(gid, settingName)) new Setting(gid, true);

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM Settings WHERE GID='" + gid + "' AND NAME='" + settingName + "'");
                rs = st.executeQuery("SELECT * FROM Settings WHERE GID='" + gid + "' AND NAME='" + settingName + "'");
            } catch (Exception ignore) {}

            if (rs != null && rs.next())
                value = rs.getString("VALUE");

        } catch (Exception ignore) {}

        return new Setting(settingName, value);
    }

    public void createSettings(String gid) {

        if (!settingExists(gid, "chatprefix")) setSetting(gid, new Setting("chatprefix", "ree!"));

        if (!settingExists(gid, "logging_invite")) setSetting(gid, "logging_invite", true);
        if (!settingExists(gid, "logging_memberjoin")) setSetting(gid, "logging_memberjoin", true);
        if (!settingExists(gid, "logging_memberleave")) setSetting(gid, "logging_memberleave", true);
        if (!settingExists(gid, "logging_memberban")) setSetting(gid, "logging_memberban", true);
        if (!settingExists(gid, "logging_memberunban")) setSetting(gid, "logging_memberunban", true);
        if (!settingExists(gid, "logging_nickname")) setSetting(gid, "logging_nickname", true);
        if (!settingExists(gid, "logging_voicejoin")) setSetting(gid, "logging_voicejoin", true);
        if (!settingExists(gid, "logging_voicemove")) setSetting(gid, "logging_voicemove", true);
        if (!settingExists(gid, "logging_voiceleave")) setSetting(gid, "logging_voiceleave", true);
        if (!settingExists(gid, "logging_roleadd")) setSetting(gid, "logging_roleadd", true);
        if (!settingExists(gid, "logging_roleremove")) setSetting(gid, "logging_roleremove", true);
        if (!settingExists(gid, "logging_voicechannel")) setSetting(gid, "logging_voicechannel", true);
        if (!settingExists(gid, "logging_textchannel")) setSetting(gid, "logging_textchannel", true);
        if (!settingExists(gid, "logging_rolecreate")) setSetting(gid, "logging_rolecreate", true);
        if (!settingExists(gid, "logging_roledelete")) setSetting(gid, "logging_roledelete", true);
        if (!settingExists(gid, "logging_rolename")) setSetting(gid, "logging_rolename", true);
        if (!settingExists(gid, "logging_rolemention")) setSetting(gid, "logging_rolemention", true);
        if (!settingExists(gid, "logging_rolehoisted")) setSetting(gid, "logging_rolehoisted", true);
        if (!settingExists(gid, "logging_rolepermission")) setSetting(gid, "logging_rolepermission", true);
        if (!settingExists(gid, "logging_rolecolor")) setSetting(gid, "logging_rolecolor", true);
        if (!settingExists(gid, "logging_messagedelete")) setSetting(gid, "logging_messagedelete", true);
    }

    public ArrayList<Setting> getAllSettings(String gid) {
        ArrayList<Setting> list = new ArrayList<>();

        try {
            PreparedStatement st;
            ResultSet rs = null;

            try {
                st = Main.sqlConnector.con.prepareStatement("SELECT * FROM Settings WHERE GID='" + gid + "'");
                rs = st.executeQuery("SELECT * FROM Settings WHERE GID='" + gid + "'");
            } catch (Exception ignore) {}

            while (rs != null && rs.next()) {
                list.add(new Setting(rs.getString("NAME"), (rs.getString("VALUE").equalsIgnoreCase("true") || rs.getString("VALUE").equalsIgnoreCase("false") ? Boolean.parseBoolean(rs.getString("VALUE")) : rs.getString("VALUE"))));
            }

        } catch (Exception ignore) {}

        return list;
    }
}