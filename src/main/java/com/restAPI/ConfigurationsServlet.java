package com.restAPI;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;

public class ConfigurationsServlet extends HttpServlet {
    static final long serialVersionUID =1L;
    private Configurations confgs;

    @Override
    public void init() {
        this.confgs = new Configurations();
        confgs.setServletContext(this.getServletContext());
    }
    // GET /_
    // GET /_?id=1
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String param = request.getParameter("id");
        Integer key = (param == null)? null:Integer.valueOf(param.trim());

        boolean json = false;
        String accept= request.getHeader("accept");

        if (accept!= null && accept.contains("json")) json= true;

        if (key==null){
            ConcurrentMap<Integer,Configuration> map = confgs.getConfigurationsMap();
            Object[] list = map.values().toArray();
            Arrays.sort(list);

            String payload = confgs.toXml(list);
            if(json) payload = confgs.toJson(payload);
            sendResponse(response,payload);
        }
        else {
            Configuration conf = confgs.getConfigurationsMap().get(key);
            if (conf == null){
                String msg = key + " does not map to a configuration";
                sendResponse(response,confgs.toXml(msg) );
            }
            else{
                if (json) sendResponse(response,confgs.toJson(confgs.toXml(conf)));
                else sendResponse(response,confgs.toXml(conf));
            }
        }
    }

    // POST /_
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String presetTitle = request.getParameter("presetTitle");
        int temperature = Integer.parseInt(request.getParameter("temperature"));
        String time = request.getParameter("time");

        if (presetTitle == null || temperature ==0 || time==null)
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));
        Configuration conf = new Configuration();
        conf.setPresetTitle(presetTitle);
        conf.setTemperature(temperature);
        conf.setTime(time);

        int id = confgs.addConfiguration(conf);

        String msg = "Configuration "+ id + " created.\n";
        sendResponse(response,confgs.toXml(msg));

    }

    // PUT /_
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        String key = null;
        String rest = null;
        boolean presetTitle = false;

        try {
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(request.getInputStream()));
            String data = br.readLine();
	    /*
	          id=33#title=War and Peace
	    */
            String[] args = data.split("#");      // id in args[0], rest in args[1]
            String[] parts1 = args[0].split("="); // id = parts1[1]
            key = parts1[1];

            String[] parts2 = args[1].split("="); // parts2[0] is key
            if (parts2[0].contains("presetTitle")) presetTitle = true;
            rest = parts2[1];
        } catch (Exception e) {
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        }

        if (key == null)
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));

        Configuration c = confgs.getConfigurationsMap().get(Integer.valueOf(key.trim()));
        if (c == null) {
            String msg = key + " does not map to a confgs.\n";
            sendResponse(response, confgs.toXml(msg));
        } else {
            if (presetTitle) c.setPresetTitle(rest);
            else c.setTemperature(Integer.parseInt(rest));

            String msg = "confgs " + key + " has been edited.\n";
            sendResponse(response, confgs.toXml(msg));
        }

    }

    // DELETE /_?id=1
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        String param = request.getParameter("id");
        Integer key = (param == null) ? null : Integer.valueOf((param.trim()));
        if (key == null)
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_BAD_REQUEST));
        try {
            confgs.getConfigurationsMap().remove(key);
            String msg = "confgs " + key + " removed.\n";
            sendResponse(response, confgs.toXml(msg));
        }
        catch (Exception e){
            throw new RuntimeException(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));}
    }

    private void sendResponse(HttpServletResponse response, String payload) {
        try{
            OutputStream out = response.getOutputStream();
            out.write(payload.getBytes());
            out.flush();
        }
      catch (IOException e) {
          throw new RuntimeException(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        }
    }


}
