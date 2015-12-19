package ru.fizteh.fivt.students.sertem96.twitter;

import com.beust.jcommander.*;

public class Parameters {
    @Parameter(names = "--hideRetweets",
            description = "����������� �������")
    private boolean hideRetweets = false;

    @Parameter(names = { "--stream", "-s" },
            description = "���������� �������� ����� �� ����� �� �������� �������")
    private boolean stream = false;

    @Parameter(names = { "--query", "-q" },
            description = "������ ��� �������� ����� ��� ������",
            required = true)
    private String query = "";

    @Parameter(names = { "--place", "-p" },
            description = "������ ����� � ����� �������")
    private String place = "";

    @Parameter(names = { "--limit", "-l" },
            description = "�������� ���� ��������� ������ (�� �������� ��� --stream)",
            validateWith = Limit.class)
    private int limit = 0;

    @Parameter(names = { "--help", "-h" },
            description = "�������� ��� �������", help = true)
    private boolean help = false;

    public final boolean isHideRetweets() {
        return hideRetweets;
    }
    public final boolean isStream() {
        return stream;
    }
    public final String getQuery() {
        return query;
    }
    public final String getPlace() {
        return place;
    }
    public final int getLimit() {
        return limit;
    }
    public final boolean isHelp() {
        return help;
    }
}
