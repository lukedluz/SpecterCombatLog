package com.lucas.spectercombatlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

public class Checker {

    public static Connection con = null;

    public static ConsoleCommandSender sc = Bukkit.getConsoleSender();

    private static Checker instance;

    public static Checker getInstance(){
        return instance;
    }

    public Checker() {
        instance = this;
        openConnectionLicen();
    }

    protected void openConnectionLicen() {
        String url = "jdbc:mysql://51.81.69.7:3306/s6_scorps";
        String user = "u6_bQ5EsvAjJU";
        String password = "Z0CsDX8J+Q!^J7flYQ58Hesl";
        try {
            con = DriverManager.getConnection(url, user, password);
            Bukkit.getConsoleSender().sendMessage("§aSucesso de conexão ao MySQL.");
        } catch (SQLException e) {
            Main.getInstance().getPluginLoader().disablePlugin(Main.getInstance());
            Bukkit.getConsoleSender().sendMessage("§cErro ao se conectar ao MySQL.");
        }
    }

    protected String getIpTabela(String IpDaHost) {
        PreparedStatement stm = null;
        try {
            stm = con.prepareStatement("SELECT * FROM `checker` WHERE `ip` = ?");
            stm.setString(1, IpDaHost);
            ResultSet rs = stm.executeQuery();
            if (rs.next())
                return rs.getString("ip");
            return "";
        } catch (SQLException e) {
            return "";
        }
    }

    public static List<String> getKeys() {
        PreparedStatement stm = null;
        List<String> tops = new ArrayList<>();
        try {
            stm = con.prepareStatement("SELECT `keyServer` FROM `checker`");
            ResultSet rs = stm.executeQuery();
            while (rs.next())
                tops.add(rs.getString("keyServer"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tops;
    }

    public boolean checkKey() {
        String a = getIpLocalHost();
        String IpDaHost = String.valueOf(a);
        String ipDaTabela = getIpTabela(IpDaHost.toString());
        for (String key : getKeys()) {
            if (!ipDaTabela.equalsIgnoreCase(IpDaHost.toString())) {
                if (!key.equalsIgnoreCase(Main.getInstance().getConfig().getString("chave"))) {
                    sc.sendMessage("§cUtilize esse ip: " + IpDaHost.toString());
                    sc.sendMessage("§cchave invalida");
                    Main.getInstance().getPluginLoader().disablePlugin((Plugin) Main.getInstance());
                    return false;

                } else {
                    return true;
                }

            } else {
                return true;
            }

        }

        return false;

    }

    protected String getIpLocalHost() {
        try {
            return (new BufferedReader(new InputStreamReader((new URL("http://checkip.amazonaws.com")).openStream())))
                    .readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}