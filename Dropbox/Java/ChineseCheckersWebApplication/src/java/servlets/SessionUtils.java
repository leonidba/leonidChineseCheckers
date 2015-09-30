/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Yaron
 */
public class SessionUtils
{
    public final static String USERNAME = "username";

    public static String getUsername(HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);
        Object sessionAttribute = (session != null) ? session.getAttribute(USERNAME) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static void setUserName(String PlayerName, HttpServletRequest request)
    {
        request.getSession(true).setAttribute(USERNAME, PlayerName);
    }

    public static void clearSession(HttpServletRequest request)
    {
        request.getSession().invalidate();
    }
}
