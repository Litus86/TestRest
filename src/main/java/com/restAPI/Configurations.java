package com.restAPI;

import sun.management.counter.ByteArrayCounter;

import javax.servlet.ServletContext;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;
import org.json.XML;

public class Configurations {
    private final String fileName ="/WEB-INF/data/configurations.db";
    private ConcurrentMap<Integer, Configuration> configurations;
    private ServletContext sctx;
    private AtomicInteger mapKey;

    public Configurations(){
        configurations = new ConcurrentHashMap<Integer, Configuration>() ;
        mapKey = new AtomicInteger();
    }

    public void setServletContext(ServletContext sctx){ this.sctx = sctx; }
    public ServletContext getServletContext () {return this.sctx;}

    public ConcurrentMap<Integer,Configuration> getConfigurationsMap() {
        if (getServletContext() == null) return null;
        if (configurations.size() < 1) populate();
        return this.configurations;
    }

    public String toXml(Object obj){
        String xml = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XMLEncoder encoder = new XMLEncoder(out);
            encoder.writeObject(obj);
            encoder.close();
            xml=out.toString();
        }
        catch (Exception e){}
        return xml;
    }

    public String toJson(String xml){
        try{
            JSONObject jobt = XML.toJSONObject(xml);
            return jobt.toString(3);
        }
        catch (Exception e){}
        return null;
    }

    public int addConfiguration (Configuration configuration){
        int id = mapKey.incrementAndGet();
        configuration.setId(id);
        configurations.put(id,configuration);
        return id;
    }

    private void populate(){
        InputStream in = sctx.getResourceAsStream(this.fileName);

        if (in != null){
            try{
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader reader =new BufferedReader(isr);

                String record = null;
                while ((record = reader.readLine()) != null){
                    String [] parts = record.split(" ");
                    if (parts.length ==3){
                        Configuration conf = new Configuration();
                        conf.setPresetTitle(parts[0]);
                        conf.setTemperature(Integer.parseInt(parts[1]));
                        conf.setTime(parts[2]);
                        addConfiguration(conf);
                    }

                }
            }
            catch(IOException e){ System.out.println("");}
        }
    }

}
