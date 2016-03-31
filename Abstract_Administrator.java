import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.InvalidAlgorithmParameterException;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.crypto.Cipher;
import java.security.DigestException;
import java.security.InvalidKeyException;


public abstract class Abstract_Administrator {

    protected static final int BLOCK_SIZE_OF_ENCRYPTION = 16;
    protected static final int LENGTH_OF_HEADER = BLOCK_SIZE_OF_ENCRYPTION -1;
    protected static final int FILE_OFFSET_SIZE_OF_HEADER = 0;
    protected static final int FILE_LENGTH_OF_HEADER = 4; //Size of an integer in bytes
    protected static final int HASH_OFFSET_OF_HEADER = FILE_LENGTH_OF_HEADER;  //4
    protected static final int FILE_EXTENSION_LENGTH_OF_HEADER = 3;
    protected static final int HASH_LENGTH_OF_HEADER = LENGTH_OF_HEADER - HASH_OFFSET_OF_HEADER - FILE_EXTENSION_LENGTH_OF_HEADER;  //8
    protected static final int FILE_EXTENSION_OFFSET_HEADER = HASH_OFFSET_OF_HEADER + HASH_LENGTH_OF_HEADER;
    protected static final int MIN_NUM_OF_PIXELS_FOR_NONCE = 5;
    protected static final int MAX_NUM_OF_PIXELS_FOR_NONCE = 22;
    protected static final int PORTION_OF_PIXELS_FOR_NONCE = 1000;  //1 in 1,000
    protected static final int NON_ALPHA_CHANNELS_PER_PIXEL = 3;
    protected static final int BITS_IN_ONE_BYTE = 8;
    
    protected boolean _started;
    
    protected boolean _completed;
    
    protected boolean _completed_successfully;
    
    protected Cipher _cipher_text;
    
    public Abstract_Administrator(  ) {
        this._started = false;
        this._completed = false;
        this._completed_successfully = false;
    }

    // called by abstract administrator to initialize cipher text and give securerandom to 
    // scatter administrator
    protected boolean initialize( char[] _character_password, Class_Abstract _abstract, boolean encrypt ) throws Weird_Exception {
        boolean result = false;
        
        //Avoid nullPointerException
        if ( ( _abstract != null ) && ( _character_password != null ) ) {
            
            //number of pixels required to generate the nonce
            Dimension _dimension = _abstract.fetch_Original_Image_Dimension();
            int numPixels = ( _dimension.height * _dimension.width ) / PORTION_OF_PIXELS_FOR_NONCE; //Use 0.1% of the pixels (rounded down)
            numPixels = Math.max( MIN_NUM_OF_PIXELS_FOR_NONCE, numPixels );  //At least 5 (15 bytes)
            numPixels = Math.min( MAX_NUM_OF_PIXELS_FOR_NONCE, numPixels );  //But no more than 22 (66 bytes)
            
            //Fetch the nonce
            byte[] nonce = _abstract.fetch_Nonce( numPixels * NON_ALPHA_CHANNELS_PER_PIXEL ); //That input value is the number of bytes wanted
            Key_Struct_Package kps = null;

            try {
                kps = Get_Key_Class.create_cryptographic_requirements( nonce, _character_password);                
                result = _abstract.make_random_scat( kps.rand );
                
                if ( result == true ) {
                    this._cipher_text = Cipher.getInstance("AES/PCBC/PKCS5Padding");
                    
                    if ( encrypt == true ) {
                        this._cipher_text.init( Cipher.ENCRYPT_MODE, kps.key, kps.ivParam );
                    } else {
                        this._cipher_text.init( Cipher.DECRYPT_MODE, kps.key, kps.ivParam );
                    }
                    
                    this._started = true;
                }
                
                kps.make_zero();
                
            } catch ( DigestException | NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e ) {
                result = false;
                
                try {
                    if ( kps != null ) {
                        kps.make_zero();
                    }
                } catch ( Exception e2 ) {
                    e2.printStackTrace();
                }
                
                try {  
                    if ( _character_password != null ) {
                        Byte_Conversions.make_zero( _character_password );
                    }
                } catch ( Exception e2 ) {
                    e2.printStackTrace();
                }
                
                Weird_Exception se = new Weird_Exception( "There was an error with Java cryptographic services that is outside of our control", e );
                se.getStackTrace();
                throw se;
            } catch ( InvalidKeyException | InvalidAlgorithmParameterException e ) {
                result = false;
                
                try {  
                    if ( kps != null ) {
                        kps.make_zero();
                    }
                } catch ( Exception e2 ) {
                    e2.printStackTrace();
                }
                
                try {  
                    if ( _character_password != null ) {
                        Byte_Conversions.make_zero( _character_password );
                    }
                } catch ( Exception e2 ) {
                    e2.printStackTrace();
                }
                
                Weird_Exception se = new Weird_Exception( "Invalid parameters were generated for Java cryptographic services", e );
                se.getStackTrace();
                throw se;
            }
        }
        
        return result;
    }
    
    public static boolean Is_Image_Valid( BufferedImage img ) {
        return Class_Abstract.Is_Image_Valid( img );
    }
    

    // returns true if all required variables are initialized
    public boolean has_started() {
        return this._started;
    }
    
    public boolean has_completed() {
        return this._completed;
    }
    
    public boolean has_completed_successfully() {
        return this._completed_successfully;
    }
    

    public abstract BufferedImage fetch_original_image();
    
    public abstract boolean make_zero();

    // determines max image payload size that can be written with 
    // given dimensions of image
    public static int Max_Image_Pay_Load( Dimension _dimension ) {
        int answer = Class_Abstract.Max_Image_Pay_Load( _dimension );
        answer -= ( BLOCK_SIZE_OF_ENCRYPTION * BITS_IN_ONE_BYTE ); //Minus Header Size
        return answer;
    }
    
    // same as previous but with given number of pixels in one bit
    public static int Max_Image_Pay_Load( Dimension _dimension, int _Pixels_in_one_Bit ) {
        if ( _Pixels_in_one_Bit <= 0 ) {
            throw new IllegalArgumentException( "The value _Pixels_in_one_Bit provided to Abstract_Administrator.Max_Image_Pay_Load was non-positive: " + _Pixels_in_one_Bit + "." );
        }
        
        int answer = Class_Abstract.Max_Image_Pay_Load( _dimension, _Pixels_in_one_Bit );
        answer -= ( BLOCK_SIZE_OF_ENCRYPTION * BITS_IN_ONE_BYTE );

        return answer;
    }
    
    
    public static int Max_Pixels_in_One_Bit( Dimension _dimension, int byte_size_of_file ) {
        int _length_of_int_payload = ( byte_size_of_file - ( byte_size_of_file % BLOCK_SIZE_OF_ENCRYPTION ) + BLOCK_SIZE_OF_ENCRYPTION );
        int answer = Class_Abstract.Max_Pixels_in_One_Bit( _dimension, ( _length_of_int_payload + BLOCK_SIZE_OF_ENCRYPTION  ) ); //Count the header
        return answer;
    }
    
}
