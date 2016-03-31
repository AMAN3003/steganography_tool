import java.awt.Point;
import java.awt.image.BufferedImage;

public class File_Decoder extends Class_Abstract {

    private int _num_of_bits_read;
    private boolean is_synced;
    
    public File_Decoder(BufferedImage img) throws Weird_Exception {
        super(img);
        this._num_of_bits_read = 0;
    }
    
    // returns next number_of_bytes in a byte array
    public byte[] fetch_bytes( int number_of_bytes ) throws Weird_Exception {
        
        if ( number_of_bytes <= 0 ) {
            throw new IllegalArgumentException( "The number of bytes to be returned cannot be zero or less than zero" );
        }
        
        byte[] answer = new byte[number_of_bytes];
        for ( int i = 0; i < answer.length; i++ ) {
            answer[i] = this.fetch_next_byte();
        }
        
        return answer;
    }

    // returns the next byte
    public byte fetch_next_byte() throws Weird_Exception {
        
        if ( this.is_synced == false ) {
            throw new Weird_Exception(  "Internal Error Occured\n" + 
                                      "The File Decoder must be synchronized before reading." );
        } 
        
        byte answer = 0x00;
        Point[] _point_array;
        
        for ( int i = 0; i < BITS_IN_ONE_BYTE; i++ ) {
            
            _point_array = this._scatter.fetch_next_point( this._Pixels_in_one_Bit ); //Throws Weird_Exception (no more points remaining)

            answer = (byte) ( ( answer << 1 ) | Class_Abstract.points_evaluation(_point_array, this._Img_Orignial ) );
            this._num_of_bits_read++;
        }
        
        return answer;
    }

    public int number_of_bytes_read() {
        return this._num_of_bits_read / BITS_IN_ONE_BYTE;
    }
    
    public int num_of_bytes_remaining() throws Weird_Exception {
        if ( this.is_synced == false ) {
            throw new Weird_Exception( "To read the bytes or calculate no of bytes remaining from file decoder it has to be synchronized first" );
        }
        return (this._scatter.num_of_points_remaining()/BITS_IN_ONE_BYTE/this._Pixels_in_one_Bit);
    }
    

    public boolean synchronize() throws Weird_Exception {
        if ( this.is_synced == true ) {
            throw new Weird_Exception( "Internal Error occurred.\n" + 
                                 "The File Decoder was tried to be synchronized more than one time" +
                                 "\n It can be synchronized only once" );
        }
        
        byte[] _bit_array = new byte[ BIT_LENGTH_OF_INDICATOR * MAX_PIXELS_PER_BIT ];
        int blocks_of_array_used = 0;
        
        Point[] _single_point = new Point[1];
        
        byte[] _result_array = new byte[BYTE_LENGTH_OF_INDICATOR];
        
        int _calculated_pixels_in_one_bit = -1;
        
        for( int i = 1; ( i <= MAX_PIXELS_PER_BIT ) && ( _calculated_pixels_in_one_bit == -1 ); i++ ) {
            
            //Read BIT_LENGTH_OF_INDICATOR more bits
            for ( int j = 0; j < BIT_LENGTH_OF_INDICATOR; j++ ) {
                try{
                    _single_point[0] = _scatter.fetch_next_point(); //We need to use an array because that's what
                } catch ( Weird_Exception e ) {              //Class_Abstract.points_evaluation expects
                    throw new Not_Able_To_Synchronize_Exception( "Unable to synchronize; tested up to " + i + " pixels per bit before running out of pixels.", e );
                }
                _bit_array[blocks_of_array_used] = Class_Abstract.points_evaluation( _single_point, this._Img_Orignial );
                blocks_of_array_used++;
            }
            
            //Start "calculate potential _signal value"
            
            byte _single_bit;  //Used as working space for the below loop
            //The signal many bytes long,so store it in a array
            //BIT_LENGTH_OF_INDICATOR MUST BE BYTE ALIGNED!
            for( int k = 0; k < BIT_LENGTH_OF_INDICATOR; k++ ) {
                _single_bit = 0x00;
                // i determines how many pixels per bit we are checking
                for( int l = (k*i); l < (k+1)*i; l++ ) {
                    _single_bit = (byte) ( _single_bit ^ _bit_array[l] );
                }
                
                //Shift all bits in current byte left by one, then append the next bit
                // for 0 to 7 bit array index is zero and 8 to 15 - 1 and so on
                _result_array[k/BITS_IN_ONE_BYTE] = (byte) ( ( (byte) ( _result_array[k/BITS_IN_ONE_BYTE] << 1 ) ) | _single_bit );
            }
            
            if ( this._signal == Byte_Conversions.convert_byte_array_to_long( _result_array ) ) {
                _calculated_pixels_in_one_bit = i; 
            }
            
        } //Check all valid values for pixels per bit
        
        //for unsuccessful process execute this 
        if ( _calculated_pixels_in_one_bit <= 0 ) {
            throw new Not_Able_To_Synchronize_Exception( "Unable to synchronize; tested up to the maximum " + Class_Abstract.MAX_PIXELS_PER_BIT + " pixels per bit." );
        } else { // else process was successful
            this.is_synced = true;
            this._Pixels_in_one_Bit = _calculated_pixels_in_one_bit;
            System.out.println( "Successfully synchronized at " + this._Pixels_in_one_Bit() + " pixels per bit." );
            return true;
        }
        
    }
    
    
    @Override
    public boolean make_zero() {
        return false;
    }

}