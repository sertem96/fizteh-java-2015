package ru.fizteh.fivt.students.sertem96.twitter;

import com.beust.jcommander.*;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import twitter4j.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.*;

public class Twitter {
    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_BLUE = "\u001B[34m";
    static final int RT_PREFIX_LENGTH = 3;
    static final double NEARBY_RADIUS = 5.0d;
    static final int GET_LAST_NUM = 10;
    static final int ONE = 1;
    static final int TWO = 2;
    static final int FOUR = 4;
    static final int FIVE = 5;
    static final int NINE = 9;
    static final int ELEVEN = 11;
    static final int NINETEEN = 19;
    static final int EARTH_RADIUS = 6371;

    enum ETime {
        MINUTE,
        HOUR,
        DAY
    }

    public static String getTimeString(long number, ETime type) {
        if (type == ETime.MINUTE) {
            if (number % GET_LAST_NUM == 0 || number % GET_LAST_NUM == ONE
                    || (number >= ELEVEN && number <= NINETEEN)) {
                return " ����� �����";
            } else if (number % GET_LAST_NUM > ONE && number % GET_LAST_NUM < FIVE) {
                return " ������ �����";
            } else {
                return " ����� �����";
            }
        } else if (type == ETime.HOUR) {
            if (number % GET_LAST_NUM == 0 || (number % GET_LAST_NUM > FOUR && number % GET_LAST_NUM <= NINE)
                    || (number >= ELEVEN && number <= NINETEEN)) {
                return " ����� �����";
            } else if (number % GET_LAST_NUM == ONE) {
                return " ��� �����";
            } else {
                return " ���� �����";
            }
        } else {
            if (number >= ELEVEN && number <= NINETEEN) {
                return " ���� �����";
            } else if (number % GET_LAST_NUM >= 0 && number % GET_LAST_NUM < TWO) {
                return " ���� �����";
            } else if (number % GET_LAST_NUM >= TWO && number % GET_LAST_NUM < FIVE) {
                return " ��� �����";
            } else {
                return " ���� �����";
            }
        }
    }

    public static double[] getCurrentLocation() throws IOException {
        URL url = new URL("http://ipinfo.io/json");
        URLConnection urlConnection = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                urlConnection.getInputStream()));
        String inputLine;
        StringBuilder urlPage = new StringBuilder("");
        while ((inputLine = in.readLine()) != null) {
            urlPage.append(inputLine);
        }
        in.close();
        Pattern pattern = Pattern.compile("\\d+\\.\\d+,\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(urlPage.toString());
        if (matcher.find()) {
            String location = matcher.group();
            String[] locationArray = location.split(",");
            return new double[] {Double.parseDouble(locationArray[0]),
                    Double.parseDouble(locationArray[1])};
        } else {
            System.err.print("Unable to get current location");
            return null;
        }
    }

    public static double[] getPlace(Parameters commander)
            throws Exception {
        GeoApiContext context = new GeoApiContext()
                .setApiKey("AIzaSyBSDOenl2KKAaFFWOqk_OhGxTvHeH8SV1o");
        GeocodingResult[] result = GeocodingApi.geocode(context,
                commander.getPlace()).await();

        double lat1 = result[0].geometry.bounds.northeast.lat;
        double lng1 = result[0].geometry.bounds.northeast.lng;
        double lat2 = result[0].geometry.bounds.southwest.lat;
        double lng2 = result[0].geometry.bounds.southwest.lng;
        double radius = (EARTH_RADIUS * Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lng1) * Math.cos(lng2) * Math.cos(Math.abs(lng1 - lng2)))) / 2;
        return new double[] {result[0].geometry.location.lat,
                result[0].geometry.location.lng, radius};
    }

    public static void printTweet(Status tweet) throws Exception {
        StringBuilder tweetText = new StringBuilder("[").append(getTime(tweet))
                .append("] @").append(ANSI_BLUE).append(tweet.getUser()
                        .getScreenName()).append(ANSI_RESET).append(": ");
        if (tweet.isRetweet()) {
            tweetText.append("��������� ").append(
                    tweet.getText().substring(RT_PREFIX_LENGTH));
        } else if (tweet.getRetweetCount() > 0) {
            if (tweet.getRetweetCount() % GET_LAST_NUM > ONE && tweet.getRetweetCount() % GET_LAST_NUM < FIVE) {
                tweetText.append(tweet.getText()).append(" (")
                        .append(tweet.getRetweetCount()).append(" �������)");
            } else if (tweet.getRetweetCount() % GET_LAST_NUM == ONE) {
                tweetText.append(tweet.getText()).append(" (")
                        .append(tweet.getRetweetCount()).append(" ������)");
            } else {
                tweetText.append(tweet.getText()).append(" (")
                        .append(tweet.getRetweetCount()).append(" ��������)");
            }
        } else {
            tweetText.append(tweet.getText()).append(" (��� ��������)");
        }

        System.out.println(tweetText.toString());
    }

    public static String getTime(Status tweet) throws Exception {
        LocalDateTime tweetDate = tweet.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime curDate = LocalDateTime.now();

        if (ChronoUnit.MINUTES.between(tweetDate, curDate) < 2) {
            return "������ ���";
        } else if (ChronoUnit.HOURS.between(tweetDate, curDate) == 0) {
            return Long.toString(ChronoUnit.MINUTES.between(tweetDate, curDate))
                    + getTimeString(ChronoUnit.MINUTES.between(tweetDate, curDate), ETime.MINUTE);
        } else if (ChronoUnit.DAYS.between(tweetDate, curDate) == 0) {
            return Long.toString(ChronoUnit.HOURS.between(tweetDate, curDate))
                    + getTimeString(ChronoUnit.HOURS.between(tweetDate, curDate), ETime.HOUR);
        } else if (ChronoUnit.DAYS.between(tweetDate, curDate) == ONE) {
            return "�����";
        } else {
            return Long.toString(ChronoUnit.DAYS.between(tweetDate, curDate))
                    + getTimeString(ChronoUnit.DAYS.between(tweetDate, curDate), ETime.DAY);
        }
    }

    public static void makeStream(Parameters commander) throws Exception {
        TwitterStream stream = new TwitterStreamFactory().getInstance();
        stream.addListener(new StatusListener() {
            @Override
            public void onStatus(Status status) {
                try {
                    printTweet(status);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            @Override
            public void onTrackLimitationNotice(int i) {
            }

            @Override
            public void onScrubGeo(long l, long l1) {
            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {
            }

            @Override
            public void onException(Exception e) {
                System.out.println(e.getMessage());
            }
        });
        FilterQuery tweetFilterQuery = new FilterQuery();
        if (commander.getQuery().contains(",")) {
            String[] tags = commander.getQuery().split(",");
            tweetFilterQuery.track(tags);
        } else {
            tweetFilterQuery.track(commander.getQuery());
        }
        stream.filter(tweetFilterQuery);
    }

    public static void printTweets(Parameters commander) throws Exception {
        TwitterFactory tf = new TwitterFactory();
        twitter4j.Twitter twitter = tf.getInstance();
        Query query = new Query();
        query.setQuery(commander.getQuery());

        if (commander.isHideRetweets()) {
            query.setQuery(query.getQuery() + " +exclude:retweets");
        }
        if (commander.getLimit() > 0) {
            query.setCount(commander.getLimit());
        }
        if (!commander.getPlace().isEmpty()) {
            if (commander.getPlace().matches("nearby")) {
                double[] result = getCurrentLocation();
                if (result != null) {
                    GeoLocation location = new GeoLocation(result[0], result[1]);
                    query.setGeoCode(location, NEARBY_RADIUS, Query.Unit.km);
                }
            } else {
                double[] placeCoord = getPlace(commander);
                GeoLocation location = new GeoLocation(placeCoord[0], placeCoord[1]);
                query.setGeoCode(location, placeCoord[2], Query.Unit.km);
            }
        }
        QueryResult result = twitter.search(query);
        List<Status> status = result.getTweets();

        String response = "����� �� ������� " + commander.getQuery();
        if (!commander.getPlace().isEmpty()) {
            response += " ��� ����� " + commander.getPlace() + ": ";
        }
        System.out.println(response);

        if (status.isEmpty()) {
            System.out.println("����� �� �������");
            System.exit(0);
        }

        for (Status tweet : status) {
            printTweet(tweet);
        }
    }

    public static void main(String[] args) {
        try {
            Parameters commander = new Parameters();
            JCommander jcommander = new JCommander(commander, args);
            jcommander.setProgramName("TwitterStreamer");
            if (commander.isHelp()) {
                jcommander.usage();
            } else if (commander.isStream()) {
                makeStream(commander);
            } else {
                printTweets(commander);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + ". ����������� ���� --help ��� ��������� ������.");
        }
    }
}
