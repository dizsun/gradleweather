package com.apress.gerber.weather.parse;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeatherParser {
    XmlPullParser xpp;
    List<String> currentTag = new ArrayList<String>();
    Map<String, String> currentAttributes;
    Map<String, String> currentConditions = new HashMap<String, String>();
    String location = "?";
    final static Pattern pattern = Pattern.compile("k-p\\d+h-n(\\d+)-\\d+");

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

    private void addForecatValue(List<Map<String, String>> forecast, int index, String key, String value) {
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
                } else if(tag.equals("start-valid-time")) {
                    addForecatValue(findForecast(timeLayout), forecastCount++, "day", currentAttributes.get("period-name"));
                } else if(tag.equals("weather-conditions") ) {
                    addForecatValue(findForecast(timeLayout), forecastCount++, "shortDescription", currentAttributes.get("weather-summary"));
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
                } else if(closeTag.equals("icon-link") ) {
                    addForecatValue(findForecast(timeLayout), forecastCount++, "iconLink", text);
                } else if(closeTag.equals("text") && currentTag.get(currentTag.size()-1).equals("wordedForecast") ) {
                    addForecatValue(findForecast(timeLayout), forecastCount++, "description", text);
                } else if(closeTag.equals("description") && currentTag.get(currentTag.size()-1).equals("location")) {
                    location = text;
                }
            } else if(eventType == XmlPullParser.TEXT) {
                text = xpp.getText();
            }
            eventType = xpp.next();
        }
    }

    Map<String, List> forecastByTimeLayout = new HashMap<String, List>();

    public List<Map<String, String>> findForecast(String timeLayout) {
        if(!forecastByTimeLayout.containsKey(timeLayout)) {
            forecastByTimeLayout.put(timeLayout, new ArrayList<Map<String, String>>());
        }
        return (List<Map<String, String>>)forecastByTimeLayout.get(timeLayout);
    }

    public String getLocation() {
        return location;
    }

    public String getCurrent(String key) {
        return currentConditions.get(key);
    }

    public List<Map<String, String>> getForecast(String timeLayout) {
        return forecastByTimeLayout.get(timeLayout);
    }

    public Collection<String> getAvailableForcasts() {
        return forecastByTimeLayout.keySet();
    }

    public String lastForcast() {
        int max = 0;
        String last = null;
        for (String eachKey : getAvailableForcasts()) {
            Matcher matcher = pattern.matcher(eachKey);
            if (matcher.matches()) {
                int num = Integer.parseInt(matcher.group(1));
                max = Math.max(num, max);
                if(num==max) last = eachKey;
            }
        }
        return last;
    }

    public List<Map<String, String>> getLastForecast() {
        return getForecast(lastForcast());
    }

    public Map<String, String> getCurrentConditions() {
        return currentConditions;
    }
}

