package net.creeperhost.creeperlauncher.util;

import net.creeperhost.creeperlauncher.Constants;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

//Copied from Minetogether
public class WebUtils
{
    private static List<String> cookies;
    private static boolean logHide;
    /*public static String URLBuilder(String Base, String... parts)
    {
        String URL = Base;
        for(String part : parts)
        {

            URL += "/" + URLEncoder.encode(part.replace("/", ""));
        }
        return URL;
    }*/
    public static String getWebResponse(String urlString)
    {
        try
        {
            URL url = new URL(urlString);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();
            // lul
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            if (cookies != null)
            {
                for (String cookie : cookies)
                {
                    conn.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
                }
            }
            conn.setRequestProperty("User-Agent", "modpacklauncher/" + Constants.APPVERSION + " Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.138 Safari/537.36 Vivaldi/1.8.770.56");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder respData = new StringBuilder();
            while ((line = rd.readLine()) != null)
            {
                respData.append(line);
                respData.append("\n");
            }

            List<String> setCookies = conn.getHeaderFields().get("Set-Cookie");

            if (setCookies != null)
            {
                cookies = setCookies;
            }

            rd.close();
            return respData.toString();
        } catch (Throwable ignored)
        {
        }

        return "error";
    }

    private static String mapToFormString(Map<String, String> map)
    {
        StringBuilder postDataStringBuilder = new StringBuilder();

        String postDataString;

        try
        {
            for (Map.Entry<String, String> entry : map.entrySet())
            {
                postDataStringBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
            }
        } catch (Exception ignored)
        {
        } finally
        {
            postDataString = postDataStringBuilder.toString();
        }
        return postDataString;
    }

    public static String postWebResponse(String urlString, Map<String, String> postDataMap)
    {
        return postWebResponse(urlString, mapToFormString(postDataMap));
    }

    public static String methodWebResponse(String urlString, String postDataString, String method, boolean isJson, boolean silent)
    {
        try
        {
            postDataString.substring(0, postDataString.length() - 1);

            byte[] postData = postDataString.getBytes(Charset.forName("UTF-8"));
            int postDataLength = postData.length;

            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "modpacklauncher/" + Constants.APPVERSION + " Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.138 Safari/537.36 Vivaldi/1.8.770.56");
            conn.setRequestMethod(method);
            if (cookies != null)
            {
                for (String cookie : cookies)
                {
                    conn.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
                }
            }
            conn.setRequestProperty("Content-Type", isJson ? "application/json" : "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setConnectTimeout(5000);
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            try
            {
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
            } catch (Throwable t)
            {
                if (!silent)
                {
                    t.printStackTrace();
                }
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder respData = new StringBuilder();
            while ((line = rd.readLine()) != null)
            {
                respData.append(line);
            }

            List<String> setCookies = conn.getHeaderFields().get("Set-Cookie");

            if (setCookies != null)
            {
                cookies = setCookies;
            }

            rd.close();
            logHide = false;
            return respData.toString();
        } catch (Throwable t)
        {
            if (silent || logHide)
            {
                return "error";
            }
            logHide = true;
        }

        return "error";
    }

    public static String postWebResponse(String urlString, String postDataString)
    {
        return methodWebResponse(urlString, postDataString, "POST", false, false);
    }

    public static String putWebResponse(String urlString, String body, boolean isJson, boolean isSilent)
    {
        return methodWebResponse(urlString, body, "PUT", isJson, isSilent);
    }

    public static boolean checkExist(URL url)
    {
        boolean response = false;
        try
        {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            response = ((connection.getResponseCode() == 200) && (connection.getContentLength() >= 0));
            connection.disconnect();
        } catch (Exception err)
        {
            response = false;
        }
        return response;
    }

    public static long getFileSize(URL url)
    {
        long response = -1;
        try
        {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            response = connection.getContentLength();
            connection.disconnect();
        } catch (Exception err)
        {
            response = -1;
        }
        return response;
    }
}
