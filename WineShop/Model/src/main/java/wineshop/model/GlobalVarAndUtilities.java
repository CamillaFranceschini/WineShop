package wineshop.model;

public abstract class GlobalVarAndUtilities {
    /**
     * Server's port
     */
    public static final int SPORT = 5670;

    /**
     * Enum of available Order's types
     */
    public enum orderTypes {
        Vendita,
        Acquisto
    }

    /**
     * Enum of available Order's states
     */
    public enum orderStates {
        Richiesto,
        Scaduto,
        ConfermaUtente,
        Completato,
        Annullato
    }

    /**
     * Check if a string is a number
     * @param num The string to check if is a number
     * @return True if the string a number, false otherwise
     */
    public static boolean isNumeric(String num) {
        try {
            Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
