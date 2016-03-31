public class Not_Able_To_Synchronize_Exception extends Weird_Exception {
   

    Not_Able_To_Synchronize_Exception( String message, Exception originalException ) {
        super( message, originalException );
    }
    
    Not_Able_To_Synchronize_Exception( String message ) {
        super ( message );
    }
    
    Not_Able_To_Synchronize_Exception() {
        super();
    }
}