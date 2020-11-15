package functions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class FunctionUtils {

    private static int labelId = 0;

    public static List<String> getTrimmedArguments(String argsList) {
        return Arrays.stream(argsList.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public static String lab_() {
        return "lab_" + (labelId++);
    }
}
