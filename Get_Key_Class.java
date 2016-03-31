import java.security.NoSuchProviderException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.MessageDigest;
import java.security.DigestException;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
 
public class Get_Key_Class {
    

    // key struct package contains secret key and secure randoms
    public static Key_Struct_Package create_cryptographic_requirements( byte[] nonce, char[] _character_password ) 
                                                         throws DigestException,
                                                         NoSuchAlgorithmException,
                                                         NoSuchProviderException {
    
        final int _num_of_iterations = (int)(Math.pow(2, 16)) + 100;
        final int _resulting_array_length = 64;  
        final int _output_key_length = _resulting_array_length/4;  

        byte[] _resutl = new byte[_resulting_array_length]; //temporarily storing final hash value
        byte[] salt = new byte[_resulting_array_length];   
        byte[] _byte_passwd = new byte[_character_password.length]; 
        byte[][] _store_in_bytes = new byte[_num_of_iterations][_resulting_array_length];
        byte[]_key_bytes = new byte[_output_key_length];
        byte[]iv = new byte[ _output_key_length ];
        byte[]_random_seed = new byte[ _resutl.length - _key_bytes.length - iv.length ];  
        
        Key_Struct_Package keyPackage = new Key_Struct_Package();

        
        try {
            
            //Convert character password to byte password
            _byte_passwd = Byte_Conversions.convert_char_to_byte( _character_password );
                    
            //hash function for encoding
            MessageDigest _message_digest = MessageDigest.getInstance("SHA-512");
            
            //store salt returned from nonce
            _message_digest.update( nonce );
            _message_digest.digest( salt, 0, salt.length );  
            
            //Create the first hash
            _message_digest.update( _byte_passwd );
            _message_digest.update( salt );
            _message_digest.digest(_store_in_bytes[0], 0, _store_in_bytes[0].length ); //Outputs to byteSTore[0]
                        
            //Iteratively hash a num_of_iterations times
            for( int i = 1; i < _num_of_iterations; i++ ) {
                _message_digest.update( _store_in_bytes[i-1] ); //Previous hash
                _message_digest.update( _byte_passwd );
                _message_digest.update( salt );
                _message_digest.digest( _store_in_bytes[i], 0, _store_in_bytes[i].length ); //Store current hash
            }
            
            Byte_Conversions.make_zero( salt );
            Byte_Conversions.make_zero( _byte_passwd );

            _message_digest.update( _store_in_bytes[_num_of_iterations-1] );
            for ( int i = 0; i < 4096; i++ ) {
                for( int j = i; j < _num_of_iterations; j+= 4096 ) {
                    _message_digest.update( _store_in_bytes[j] );
                    Byte_Conversions.make_zero( _store_in_bytes[j] ); //Zeroize as we go
                }
            }
            
            _message_digest.digest( _resutl, 0, _resutl.length ); //Stores in _resutl
                        
            int i = 0;
            
            //Copy key over to its own array
            for( int j = 0; j < _key_bytes.length & i < _resutl.length; ) {
                _key_bytes[i] = _resutl[i];
                j++;
                i++;
            }
            
            for( int j = 0; j < iv.length & i < _resutl.length; ) {
                iv[j] = _resutl[i];
                j++;
                i++;
            }
            
            for( int j = 0; j< _random_seed.length & i < _resutl.length; ) {
                _random_seed[j] = _resutl[i];
                j++;
                i++;
            }
            
            Byte_Conversions.make_zero( _resutl );
            
            keyPackage.key = new SecretKeySpec( _key_bytes, "AES" );
            Byte_Conversions.make_zero( _key_bytes );
            
            keyPackage.ivParam = new IvParameterSpec( iv );
            Byte_Conversions.make_zero( iv );
            
            keyPackage.rand = SecureRandom.getInstance( "SHA1PRNG", "SUN" );
            keyPackage.rand.setSeed( _random_seed );
            Byte_Conversions.make_zero( _random_seed );
            
        } catch ( Exception ex ) {
            ex.printStackTrace();
            
            try {
                Byte_Conversions.make_zero( _resutl );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            
            try {
                Byte_Conversions.make_zero( _key_bytes );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            
            try {
                Byte_Conversions.make_zero( iv );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            
            try {
                Byte_Conversions.make_zero( _random_seed );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            
            try {
                keyPackage.make_zero();
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            
            try {
                Byte_Conversions.make_zero( _byte_passwd );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            
            try {
                Byte_Conversions.make_zero( _character_password );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            
            try {
                Byte_Conversions.make_zero( salt );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            
            try {
                Byte_Conversions.make_zero( nonce );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
           
            for ( int i = 0; i < _num_of_iterations; i++ ) {
                try {
                    Byte_Conversions.make_zero( _store_in_bytes[i] );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
            
            throw ex;
        } 
                
        return keyPackage;
    }
    

    // returns hash of desired length
    public static byte[] fetch_SHA_512_hash( byte[] input, int _apt_byte_length ) throws NoSuchAlgorithmException {
        MessageDigest _message_digest = MessageDigest.getInstance("SHA-512");

        byte[] _hash_bytes = _message_digest.digest( input );
        int size = Math.min( _hash_bytes.length, _apt_byte_length );
        byte[] output = new byte[size];
        
        for( int i = 0; i < size; i++ ) {
            output[i] = _hash_bytes[i];
        }
        
        Byte_Conversions.make_zero( _hash_bytes );
        
        return output;
    }

    // validates the hash by checking calculated hash against expected hash
    public static boolean _validation_of_SHA_512_hash( byte[] input, byte[] _expected_hash_value ) throws NoSuchAlgorithmException {
        boolean result = false;

        if ( _expected_hash_value.length > 0 ) {
            byte[] calculatedHash = Get_Key_Class.fetch_SHA_512_hash( input, _expected_hash_value.length );
            result = Byte_Conversions.array_comparison( calculatedHash, _expected_hash_value );
            Byte_Conversions.make_zero( calculatedHash );
        }
        
        return result; 

    }
    
}