package nz.co.manawabay.core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public final class CommonConstants {
    /**
     * Text All
     */
    public static final String TEXT_ALL = "all";

    /**
     * Regex to match hyphen in hyphenated word.
     */
    public static final String REGEX_HYPHENATED = "(?<=\\w)-";
}
