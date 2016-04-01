import java.awt.image.BufferedImage;
import java.io.File;
import javax.crypto.BadPaddingException;
import java.nio.file.*;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class File_Encoder_Administrator extends Abstract_Administrator {

    private File_Encoder file_encoder;
    private File payload;
    
    public File_Encoder_Administrator( BufferedImage img, File _new_pay_load ) throws Weird_Exception {
        //File_Encoder constructor validates img
        super();
        this.file_encoder = new File_Encoder( img );
        this.payload = _new_pay_load;
        
        //Validate file is readable (not null)
        if ( _new_pay_load == null ) {
            throw new IllegalArgumentException ("The File passed to File_Encoder_Administrator is null.");
        }
    }
    
    /**
     * Writes the file (provided in the constructor) to the BufferedImage
     * (provided in the constructor) and utilizes the provided password.
     * @param password A character array password.
     * @return True if successful, false otherwise.
     */
    public boolean encode( char[] password ) throws Weird_Exception{
        
        if ( this._completed == true ) {
            throw new Weird_Exception( "The user has ordered an encode operation, but this File_Encoder_Administrator has already _completed encoding (successfully or otherwise)." );
        } else {
            this._completed = true;
        }
        
        try{  
            //get the bytes of the payload
            Path path = Paths.get(this.payload.getAbsolutePath());
            byte[] _pay_load_bytes = Files.readAllBytes(path);
            int payloadSize = _pay_load_bytes.length;
            
            //initialize _cipher_text
            this.initialize(password, this.file_encoder,true);
           
            //build payload size block of the header
            byte[] _pay_load_size_block = Byte_Conversions.convert_int_to_byte_array(payloadSize);
            //check if fileSizeBlock will fit in header
            if (_pay_load_size_block.length > FILE_LENGTH_OF_HEADER){
                throw new Weird_Exception("File size will not fit in the "
                        + "HEADER_FILE_SIZE section of header");
            }
            //build hash block of header
            byte[] _red_hash_block = Get_Key_Class.fetch_SHA_512_hash(_pay_load_bytes, HASH_LENGTH_OF_HEADER );
            
            //Gets the 3-byte representation of the file extension from the path    
            byte[] _red_file_extension = Byte_Conversions.combine_four_characters( File_Encoder_Administrator.fetch_file_extension_from_path( path ) );
            
            //Concatenates the payload and the hash
            byte[] _intermediate_red_header = Byte_Conversions.concatenation_of_arrays(_pay_load_size_block, _red_hash_block);
            
            //Concatenates the (payload and hash) and file extension
            byte[] _red_header = Byte_Conversions.concatenation_of_arrays(_intermediate_red_header, _red_file_extension);
            //encrypt header
            byte[] _encrypted_header = this._cipher_text.doFinal(_red_header);
            //encrypt the payload
            byte[] _encrypted_payload = this._cipher_text.doFinal(_pay_load_bytes);
            //combine the header and payload
            byte[] headerAndPayload = Byte_Conversions.concatenation_of_arrays(_encrypted_header, _encrypted_payload);
            
            //encode the header + payload
            this.file_encoder.encode(headerAndPayload);
            
            //make_zero all arrays
            Byte_Conversions.make_zero(_pay_load_bytes);
            Byte_Conversions.make_zero(_pay_load_size_block);
            Byte_Conversions.make_zero(_red_hash_block);
            Byte_Conversions.make_zero(_red_file_extension); //Although there *is* an immutable String floating around 
            Byte_Conversions.make_zero(_intermediate_red_header); 
            Byte_Conversions.make_zero(_red_header);
            Byte_Conversions.make_zero(_encrypted_header);
            Byte_Conversions.make_zero(_encrypted_payload);
            Byte_Conversions.make_zero(headerAndPayload);
            //make_zero password
            //Byte_Conversions.make_zero(password);
             
            
        } catch ( Weird_Exception ex ) {
            throw new Weird_Exception("Encode failed", ex);
        } catch( IOException ex ) {
            throw new Weird_Exception("IOException thrown when reading all bytes from payload", ex);
        } catch(NoSuchAlgorithmException ex ) {
            throw new Weird_Exception("NoSuchAlgorithmException thrown when "
                    + "when geting hash of payload", ex);
        } catch(IllegalBlockSizeException ex ) {
            throw new Weird_Exception("IllegalBlockSizeException thrown "
                    + "when encrypting", ex);
        } catch(BadPaddingException ex ) {
            throw new Weird_Exception("BadPaddingException thrown "
                    + "when encrypting", ex);
        }
        
        this._completed_successfully = true;
        return true;
    }
    
    /**
     * Returns a deep copy of the BufferedImage with the payload encoded.
     * Throws a Weird_Exception if no encoding has taken place.
     * @return A deep copy of the BufferedImage with the payload encoded
     */
    public BufferedImage get_required_image() throws Weird_Exception {
       if ( this._completed_successfully == false ){
            throw new Weird_Exception("Enocding has not taken place yet");
        }else {
          return Class_Abstract.deep_image_copy(this.file_encoder.get_required_image() );
        }
    }
    
    /**
     * Returns a deep copy of the BufferedImage provided in the constructor
     * @return A deep copy of the BufferedImage provided in the constructor
     */
    @Override
    public BufferedImage fetch_original_image() {
        return this.file_encoder.fetch_original_image();
    }
    
    /**
     * Returns the number of bits written to the BufferedImage after encoding.
     * If called before encoding, returns -1.
     * @return The number of bits written to the BufferedImage or -1 if no encoding has occurred.
     */
    public int result_num_of_bytes_written() {
        if ( this._completed == false ) {
            throw new UnsupportedOperationException( "The user has ordered this File_Encoder_Administrator to return the number of bytes written to an image, " +
                                                     "but this File_Encoder_Administrator has not yet attempted to encode a payload." );
        }
        return this.file_encoder.fetch_num_of_bits_written() / Class_Abstract.BITS_IN_ONE_BYTE;
    }
    
    /**
     * Returns the number of pixels changed in the BufferedImage after encoding.
     * If called before encoding, returns -1
     * @return The number of pixels changed in the BufferedImage or -1 if no encoding has occurred.
     */
    public int result_of_num_of_pixels_changed() {
        if ( this._completed == false ) {
            throw new UnsupportedOperationException( "The user has ordered this File_Encoder_Administrator "
                    + "to return the number of pixels changed in an image, " 
                    + "but this File_Encoder_Administrator has not yet attempted to encode a payload." );
        }
        return this.file_encoder.fetch_num_of_pixels_changed();
    }
    
    /**
     * Returns the number of pixels used to encode each bit during encoding.
     * If called before encoding, returns -1.  If 3 bits per pixel were encoded, returns 0.
     * @return The number of pixels used to encode each bit, 0 if 3 bits were encoded in
     *         each pixel, or -1 if no encoding has occurred.
     */
    public int result_of_num_of_pixel_in_one_bit() {
        if ( this._completed == false ) {
            throw new UnsupportedOperationException( "The user has ordered this "
                    + "File_Encoder_Administrator to return the number pixels used to encode "
                    + "each bit in an image, " 
                    + "but this File_Encoder_Administrator has not yet attempted to encode a payload." );
        }
        return this.file_encoder._Pixels_in_one_Bit();
    }
    
    /**
     * Returns the lowercase representation of up to the next four characters past
     * the last index of a period on this path.  Returns an empty String if no periods
     * are found.
     * @param _path The Path of the file
     * @return A lowercase file extension without a period.
     */
    public static String fetch_file_extension_from_path( Path _path ) {
        String answer = "";
        String str1 = _path.getFileName().toString();
        int _last_period = str1.lastIndexOf('.');

        if (_last_period > 0 &&  _last_period < str1.length() - 1) {
            answer = str1.substring(_last_period+1).toLowerCase();
        }
        if ( answer.length() > 4 ) {
            answer = answer.substring(0,4);
        }
        return answer;
    }
    
    /**
     * Zeroizes all sensitive data.
     * @return True if successful, false if an error occurred.
     */
    @Override
    public boolean make_zero() {
        // TODO Auto-generated method stub
        return false;
    }
    
    
}