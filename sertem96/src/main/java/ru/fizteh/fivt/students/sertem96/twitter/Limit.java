package ru.fizteh.fivt.students.sertem96.twitter;

import com.beust.jcommander.*;

public class Limit implements IParameterValidator {
    private static final int MAX_TWEETS = 100;
    public final void validate(String name, String value)
        throws ParameterException {
        int n = Integer.parseInt(value);
        if (n <= 0) {
            throw new ParameterException("�������� " + name
                    + " ������ ���� ������������� (������� " + value + ") ");
        } else if (n > MAX_TWEETS) {
            throw new ParameterException("�������� " + name
                    + " ������ ���� ������, ��� 100 (������� " + value + ") ");
        }
    }
}
