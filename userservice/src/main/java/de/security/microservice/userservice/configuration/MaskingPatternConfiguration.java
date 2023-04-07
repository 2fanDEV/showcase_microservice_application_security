package de.security.microservice.userservice.configuration;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;


/*
    baeldung has a tutorial that helps someone to configure logback in such a way to mask sensitive data
    https://www.baeldung.com/logback-mask-sensitive-data
    https://logback.qos.ch/manual/appenders.html
 */
public class MaskingPatternConfiguration extends PatternLayout {

    private Pattern patternMatched;

    private List<String> allPatterns = new ArrayList<>();

    public void addMaskPattern(String pattern)
    {
        allPatterns.add(pattern);
        patternMatched = Pattern.compile(String.join("|", allPatterns), Pattern.MULTILINE);
    }

    @Override
    public String doLayout(ILoggingEvent event)
    {
        return maskedLog(super.doLayout(event));
    }

    public String maskedLog(String toBeMasked)
    {
        StringBuilder stringBuilder = new StringBuilder(toBeMasked);
        Matcher matcher = patternMatched.matcher(stringBuilder);

        while(matcher.find())
        {
            IntStream.rangeClosed(1, matcher.groupCount()).forEach(currentGroup -> {
                if(matcher.group(currentGroup) != null)
                {
                    IntStream.rangeClosed(matcher.start(currentGroup), matcher.end(currentGroup))
                            .forEach((index) -> stringBuilder.setCharAt(index, '*'));
                }
            });
        }
        return stringBuilder.toString();
    }
}
