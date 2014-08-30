package com.apress.gerber.weather.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class WeatherParser {
    XmlPullParser xpp;
    List<String> currentTag = new ArrayList();
    Map<String, String> currentAttributes;
    Map<String, String> currentConditions = new HashMap<String, String>();
    List<Map<String, String>> forecast = new ArrayList<Map<String, String>>();
    String location = "?";

    public WeatherParser() {
        XmlPullParserFactory factory;
        try {
            factory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Pull parser not available");
        }
        factory.setNamespaceAware(true);
        try {
            xpp = factory.newPullParser();
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Pull parser can not be created.");
        }
    }

    private Map<String, String> attributes() {
        Map<String, String> all = new HashMap<String, String>();
        for(int i=0; i < xpp.getAttributeCount(); i++) {
            all.put(xpp.getAttributeName(i), xpp.getAttributeValue(i));
        }
        return all;
    }

    private void addForecatValue(int index, String key, String value) {
        while(forecast.size()-1 < index)
            forecast.add(new HashMap<String, String>());
        forecast.get(index).put(key, value);
    }

    public void parse(Reader xml) throws XmlPullParserException, IOException {
        xpp.setInput(xml);
        int eventType = xpp.getEventType();
        String text = null;
        String tempType = null;
        String timeLayout = null;
        int forecastCount = 0;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                String tag = xpp.getName();
                currentAttributes = attributes();
                currentTag.add(tag);
                if(tag.equals("temperature")) {
                    tempType = xpp.getAttributeValue(null, "type");
                } else if(tag.equals("start-valid-time") && timeLayout.equals("k-p12h-n13-1")) {
                    addForecatValue(forecastCount++, "day", currentAttributes.get("period-name"));
                } else if(tag.equals("weather-conditions") && timeLayout.equals("k-p12h-n13-1")) {
                    addForecatValue(forecastCount++, "shortDescription", currentAttributes.get("weather-summary"));
                } else if(tag.equals("conditions-icon") || tag.equals("weather")) {
                    timeLayout = currentAttributes.get("time-layout");
                }
            } else if(eventType == XmlPullParser.END_TAG) {
                String closeTag = currentTag.remove(currentTag.size() - 1);
                if(closeTag.equals("value") && tempType!=null && currentTag.get(currentTag.size()-1).equals("temperature")) {
                    currentConditions.put(tempType, text);
                    tempType = null;
                } else if(closeTag.equals("layout-key")) {
                    timeLayout = text;
                } else if(closeTag.equals("time-layout") || closeTag.equals("conditions-icon") || closeTag.equals("weather")) {
                    forecastCount = 0;
                } else if(closeTag.equals("icon-link") && timeLayout.equals("k-p12h-n13-1")) {
                    addForecatValue(forecastCount++, "iconLink", text);
                } else if(closeTag.equals("description") && currentTag.get(currentTag.size()-1).equals("location")) {
                    location = text;
                }
            } else if(eventType == XmlPullParser.TEXT) {
                text = xpp.getText();
            }
            eventType = xpp.next();
        }
    }

    public String getLocation() {
        return location;
    }

    public String getCurrent(String key) {
        return currentConditions.get(key);
    }

    public List<Map<String, String>> getForecast() {
        return forecast;
    }
}

