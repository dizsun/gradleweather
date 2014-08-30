package com.apress.gerber.weather.parse;

import junit.framework.TestCase;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Created by Clifton
 * Copyright 8/28/2014.
 */
public class WeatherParseTest extends TestCase {

    private WeatherParser weather;
    private String givenXml;

    private String asString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        for(String eachLine = reader.readLine(); eachLine != null; eachLine = reader.readLine()) {
            builder.append(eachLine);
        }
        return builder.toString();
    }

    public void setUp() throws IOException, XmlPullParserException {
        URL weatherXml = getClass().getResource("/weather.xml");
        assertNotNull("Test requires weather.xml as a resource at the CP root.", weatherXml);
        this.givenXml = asString(weatherXml.openStream());
        this.weather = new WeatherParser();
        weather.parse(new StringReader(givenXml.replaceAll("<br>", "<br/>")));
    }

    public void testCanSeeCurrentTemp() {
        assertEquals(weather.getCurrent("apparent"), "63");
        assertEquals(weather.getCurrent("minimum"), "59");
        assertEquals(weather.getCurrent("maximum"), "81");
        assertEquals(weather.getCurrent("dew point"), "56");
    }

    public void testCanSeeCurrentLocation() {
        assertEquals("Should see the location in XML", weather.getLocation(), "Sunnyvale, CA");
    }

    public void testCanSeeForecast() {
        List<Map<String, String>> weatherForecast = weather.getForecast();
        int theSize = weatherForecast.size();
        assertTrue( "Should forcast for 13 days",theSize == 13 );
        assertForecasts(asList("Today", "Tonight", "Wednesday", "Wednesday Night", "Thursday"), "day");
        assertForecasts(asList("skc.png", "nskc.png", "few.png", "nbknfg.png", "sctfg.png"), "iconLink");
        assertForecasts(asList("Sunny", "Clear", "Sunny", "Patchy Fog", "Patchy Fog", "Mostly Clear"), "shortDescription");
    }

    public void assertForecasts(List list, String key) {
        for (int idx = 0; idx < list.size(); idx++) {
            String each = (String) list.get(idx);
            String actual = weather.getForecast().get(idx).get(key);
            assertTrue( "$idx Forecast should have key '$key'",null!=actual );
            assertTrue( "${idx} Forecast should end with $each but was $actual",actual.endsWith(each) );
        }
    }
}
