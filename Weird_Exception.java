public class Weird_Exception extends Exception {
    
    //public final String what;
    public final Exception e;
    
    Weird_Exception( String message, Exception originalException ) {
        super( Weird_Exception.generateMessage(message, originalException) );
        this.e = originalException;
    }
    
    Weird_Exception( String message ) {
        super ( Weird_Exception.generateMessage(message, null) );
        this.e = null;
    }
    
    Weird_Exception() {
        super();
        this.e = null;
    }
    
    public static String generateMessage( String message, Exception originalException ) {
        if ( originalException != null ) {
            return "Unrecoverable Error: " + message + ":\n" + originalException.getMessage();
        } else {
            return "Unrecoverable Error: " + message + ".";
        }
    }

}