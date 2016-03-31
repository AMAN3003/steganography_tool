public class Image_Exception extends Weird_Exception {
    
    Image_Exception( String message, Exception originalException ) {
        super( message, originalException );
    }
    
    Image_Exception( String message ) {
        super ( message );
    }
    
    Image_Exception() {
        super();
    }
}