public final class Util {

    static int i = 0;

    private Util() {
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty() || s.replaceAll("\\t", "").isEmpty();
    }

    public static String nextDataName() {
        i++;
        return "_" + i;
    }
}
