import javax.crypto.IllegalBlockSizeException;
import java.security.NoSuchAlgorithmException;
import java.awt.image.BufferedImage;
import javax.crypto.BadPaddingException;

public class File_Decoder_Administrator extends Abstract_Administrator {

    private File_Decoder _file_decoder;
    
    
    private byte[] payload;
    
    private String _file_extension;
    
    
    public File_Decoder_Administrator( BufferedImage img ) throws Weird_Exception {
        super();
        this._file_decoder = new File_Decoder( img );
        this.payload = null;
        this._file_extension = null;
    }

    // tries to decode the payload from image
    public boolean decode( char[] _input_password ) throws Weird_Exception {
        boolean answer = false;
        if ( this.has_completed() == true ) {
            throw new Weird_Exception( "User requested for decode operation, but File_Decoder_Administrator has already done decoding (successful/unsuccessful)" );
        }
        
        try {
            this._completed = true;   
                        this.initialize(_input_password, this._file_decoder, false);
            
            boolean _sync_success = false;
            _sync_success = this._file_decoder.synchronize();
            
            if ( _sync_success == false ) {
                throw new Weird_Exception( "Not able to synchronize file decoder." );
            }
            
            byte[] _black_array = this._file_decoder.fetch_bytes( BLOCK_SIZE_OF_ENCRYPTION );
            byte[] _red_array = this._cipher_text.doFinal( _black_array );
            Byte_Conversions.make_zero( _black_array );
            
            //Split the header (since the header is composed of a fileLength then an expected hash)
            byte[] _red_file_length = Byte_Conversions.fetch_sub_array(_red_array, FILE_OFFSET_SIZE_OF_HEADER, FILE_LENGTH_OF_HEADER );
            byte[] red_file_extension = Byte_Conversions.fetch_sub_array( _red_array, FILE_EXTENSION_OFFSET_HEADER, FILE_EXTENSION_LENGTH_OF_HEADER );
            byte[] _red_expected_hash = Byte_Conversions.fetch_sub_array(_red_array, HASH_OFFSET_OF_HEADER, HASH_LENGTH_OF_HEADER );
            Byte_Conversions.make_zero( _red_array );
            
            this._file_extension = Byte_Conversions.divide_three_bytes( red_file_extension );
            
            Byte_Conversions.make_zero( red_file_extension );
            if ( this._file_extension.length() != 0 ) {
                // files without extension are left extension-less
                this._file_extension = "." + this._file_extension;
            }
            
            // Parse and validate the file length
            int _file_length_int = Byte_Conversions.convert_byte_array_to_int( _red_file_length );
            Byte_Conversions.make_zero( _red_file_length );
            if ( _file_length_int < 0 ) {
                throw new Weird_Exception( "file length of header is not valid : " + _file_length_int );
            } else {
                System.out.println( "Length of decrypted file is : " + _file_length_int );
            }
            
           
            int _length_of_int_payload = ( _file_length_int - ( _file_length_int % BLOCK_SIZE_OF_ENCRYPTION ) + BLOCK_SIZE_OF_ENCRYPTION );
            
            byte[] _black_file_bytes = this._file_decoder.fetch_bytes( _length_of_int_payload );
            
            byte[] _red_file_bytes = this._cipher_text.doFinal( _black_file_bytes );
            Byte_Conversions.make_zero( _black_file_bytes );
            
            //Validate the hash of the file payload
            if ( Get_Key_Class._validation_of_SHA_512_hash( _red_file_bytes, _red_expected_hash ) == false ) {
                byte[] calcHash = Get_Key_Class.fetch_SHA_512_hash(_red_file_bytes, _red_expected_hash.length );
                Byte_Conversions.make_zero( _red_file_bytes );
                throw new Weird_Exception( "File length: " + _file_length_int + "\n" +
                                         "Excpected  Hash: " + Byte_Conversions.convert_byte_array_to_string( _red_expected_hash ) + "\n" +
                                         "Calculated Hash: " + Byte_Conversions.convert_byte_array_to_string( calcHash ) +
                                         "File hashes did not match.\n" );
            } else {
                System.out.println( "File hash is successful." );
            }
            
            this.payload = _red_file_bytes;
            _red_file_bytes = null; 
            
            this._completed_successfully = true;
            answer = true;
        
        } catch ( BadPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException e ) {
            e.printStackTrace();
            throw new Weird_Exception( "Unable to decode the file. Might be another cryptographic errors", e );
        }
        
        return answer;
    }
    
    
    @Override
    public BufferedImage fetch_original_image() {
        return this._file_decoder.fetch_original_image();
    }
    
    
    public byte[] fetch_pay_load() {
        if ( this._completed_successfully == false ) {
            throw new UnsupportedOperationException( "User requests to return the decrypted payload file , " +
                                                     "but File_Decoder_Administrator was unsuccessful in decrypting the payload." );
        }
        return this.payload;
    }
    
    
    public String fetch_file_extension  () {
        if ( this._completed_successfully == false ) {
            throw new UnsupportedOperationException( "User requests to return the decrypted payload file name, " +
                                                     "but File_Decoder_Administrator was unsuccessful in decrypting the payload." );
        }
        return this._file_extension;
    }
    
    
    public int result_of_number_of_bytes_read() {
        if ( this._completed == false ) {
            throw new UnsupportedOperationException( "User requested to return the number of bytes read, " +
                    "but File_Decoder_Administrator hasn't attempted to decrypt payload." );
        }
        return this._file_decoder.number_of_bytes_read();
    }

    // no. of pixels used to decode the payload per bit 
    public int result_of_num_of_pixel_in_one_bit() {
        if ( this._completed_successfully == false ) {
            throw new UnsupportedOperationException( "User requested for the decrypted payload file absolute path, " +
                                                     "but File_Decoder_Administrator was unabe to decrypt the payload." );
        }
        return this._file_decoder._Pixels_in_one_Bit();
    }

    
    @Override
    public boolean make_zero() {
        return false;
    }

    
    
}