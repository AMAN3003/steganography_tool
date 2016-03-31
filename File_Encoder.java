import java.awt.Point;
import java.util.TreeSet;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.image.BufferedImage;


public class File_Encoder extends Class_Abstract {
    
    private BufferedImage _modified_image;
    
    private int _num_of_pixels_changed;
        
    private int _num_of_bits_encoded;
    
    private boolean has_completed;
    
    private TreeSet<Integer> existing_colours;
    
    // constructor
    File_Encoder( BufferedImage img ) throws Weird_Exception {
        super( img );
        
        this._num_of_pixels_changed = 0;
        this._num_of_bits_encoded = 0;
        this.has_completed = false;
    }

    // write given byte array to given image
    public boolean encode( byte[] bytes_of_file ) throws Weird_Exception {
        boolean answer = false;
    
        if ( this._scatter.has_started() == false ) {
            throw new Weird_Exception( "Encoding operation requested but scatter administrator  " +
                                     "has not yet started.\nEncode operation aborted for security purposes " +
                                     "\nInitialize the scatter administrator with a SecureRandom before attempting to encode." );
        }
        
        this._Pixels_in_one_Bit = ( this._scatter.num_of_points_remaining() ) / 
                            ( ( bytes_of_file.length + BYTE_LENGTH_OF_INDICATOR ) * BITS_IN_ONE_BYTE );
        this._modified_image = Class_Abstract.deep_image_copy( this._Img_Orignial );

        if ( this._Pixels_in_one_Bit > MAX_PIXELS_PER_BIT ) {
            this._Pixels_in_one_Bit = MAX_PIXELS_PER_BIT;
        }
        //If there are not enough pixels, then this._Pixels_in_one_Bit will have evaluated to zero
        if ( this._Pixels_in_one_Bit <= 0 ) {
            throw new Weird_Exception( "Payload provided is too large to be encoded inside the image.\n" +
                                     "Only " + this._scatter.num_of_points_remaining() + " pixels are available.\n" + this._scatter.no_of_points_used() +
                                     " pixels are already used.\n and User requested for " + (bytes_of_file.length * 8) + " bits to be written.\n" +
                                     "Overhead cost of " + BYTE_LENGTH_OF_INDICATOR + " bytes is also involved for the encoder function.\n" +
                                     "So maximum number of bits that can be written " + (this._scatter.num_of_points_remaining() - BYTE_LENGTH_OF_INDICATOR) );
        }
        
        
        this.existing_colours = Class_Abstract.fetch_palette_colour( this._Img_Orignial ); 
        byte[] _bytes_indicators = Byte_Conversions.convert_long_to_byte_array( this._signal );
        
        //Write the _signal
        for ( int i = 0; i < _bytes_indicators.length; i++ ) {
            this.feed_byte( _bytes_indicators[i] );
        }
        
        for ( int i = 0; i < bytes_of_file.length; i++ ) {
            this.feed_byte( bytes_of_file[i] );
        }
        answer = true;
        this.has_completed = true;
                
        return answer;
    }

    // writes single byte to the image 
    private void feed_byte( byte byte_to_feed ) throws Weird_Exception {
        
        //byte_to_feed must be ANDed with 0xFF as it gives a positive integer
        int temp = MASK_EXCEPT_END_BYTE & byte_to_feed;
        
        for( int i = 0; i < BITS_IN_ONE_BYTE; i++ ) {
            
            if ( (temp & STARTING_BIT_OF_END_BYTE) == 0x00 ) {
                this.write_bit( false );
            } else {
                this.write_bit( true );
            }
            
            temp = temp << 1;
        }
        
    }
    

    // writes a single bit to the image
    private void write_bit( boolean is_bit_one ) throws Weird_Exception {
        Point[] pointArr = this._scatter.fetch_next_point( this._Pixels_in_one_Bit );
                
        byte _current_state = Class_Abstract.points_evaluation( pointArr, this._modified_image );
        
       
        if ( (!is_bit_one) ^ (_current_state == 0x00) ) { 
            double[] possibilities = new double[ this._Pixels_in_one_Bit ];
            double _smallest_till_now = 10;
            int _index_of_smallest = 0;
            
            if ( possibilities.length > 1 ) {
                for( int i = 0; i < possibilities.length; i++ ) {
                    
                    if ( Analyze_Pixels.Pixel_colour_is_near_extreme( this._Img_Orignial.getRGB( pointArr[i].x, pointArr[i].y ) ) == true ) {
                        if ( _smallest_till_now < 0.3 ) { //We add 0.3 to cause us to generally avoid pixels near color extremes
                            possibilities[i] = 0.3;  //If the best match so far is already less than 0.3, then there is no
                        } else {                     //need to analyze this pixel that's near a color extreme
                            possibilities[i] = 0.3 + Analyze_Pixels.usability_point_calculation( this._Img_Orignial, pointArr[i] );
                        }
                    } else {
                        possibilities[i] = Analyze_Pixels.usability_point_calculation( this._Img_Orignial, pointArr[i] );
                    }
                    
                    if ( possibilities[i] < _smallest_till_now ) {
                        _smallest_till_now = possibilities[i];
                        _index_of_smallest = i;
                    }
                }
            } 
            
            int _temp_int = this._modified_image.getRGB( pointArr[_index_of_smallest].x, pointArr[_index_of_smallest].y );
            Color _temp_colour = new Color( _temp_int );
            boolean _temp_is_one = Class_Abstract.evaluation_of_integer( _temp_int );
            int red = _temp_colour.getRed();
            int green = _temp_colour.getGreen();
            int blue = _temp_colour.getBlue();
            
            //if there is a colour extreme then leave them as it is
            boolean _freeze_red = ( red > 252 || red < 3 );
            boolean _freeze_green = ( green > 252 || green < 3 );
            boolean _freeze_blue = ( blue > 252 || blue < 3 );
            ArrayList<Color> _colour_possibilities = new ArrayList<Color>();
            
            //Populate the _colour_possibilities list...
            if ( _freeze_red == false || _freeze_green == false || _freeze_blue == false ) {
                for( int r = -2; r <= 2; r++ ) {
                    if ( ( _freeze_red && r != 0 ) == false ) {
                        for( int g = -2; g <= 2; g++ ) {
                            if ( ( _freeze_green && g != 0 ) == false ) {
                                for( int b = -2; b <= 2; b++ ) {
                                    if ( ( _freeze_blue && b != 0 ) == false ) {
                                        //if ( )
                                        Color _possible_colour = new Color( _temp_colour.getRed() + r, _temp_colour.getGreen() + g, 
                                                                         _temp_colour.getBlue() + b, _temp_colour.getAlpha() );
                                        int _possible_colour_value = _possible_colour.getRGB();
                                        
                                        if ( _temp_is_one != Class_Abstract.evaluation_of_integer( _possible_colour_value ) ) {
                                            _colour_possibilities.add( _possible_colour );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else { 
                
                //0 = Red
                //1 = Green
                //2 = Blue
                int choice = 0;
                choice = (int)( ( this._signal + pointArr[_index_of_smallest].x + pointArr[_index_of_smallest].y ) % 3 );
                
                // modify  a signle bit of the colour
                switch( choice ) {
                case 0:
                    if ( red == 255 ) {
                        red = 254;  
                    } else if ( red == 254 ) {
                        red = 255;  
                    } else if ( red == 253 ) {
                        red = 255;
                    } else if ( red == 0 ) {
                        red = 1; 
                    } else if ( red == 1 ) {
                        red = 3; 
                    } else if ( red == 2 ) {
                        red = 3; 
                    }
                    break;
                case 1: 
                    if ( green == 255 ) {
                        green = 254;  
                    } else if ( green == 254 ) {
                        green = 255;  
                    } else if ( green == 253 ) {
                        green = 255;
                    } else if ( green == 0 ) {
                        green = 1; 
                    } else if ( green == 1 ) {
                        green = 3; 
                    } else if ( green == 2 ) {
                        green = 3; 
                    }
                    break;
                case 2:
                default:
                    if ( blue == 255 ) {
                        blue = 254;  
                    } else if ( blue == 254 ) {
                        blue = 255;  
                    } else if ( blue == 253 ) {
                        blue = 255;
                    } else if ( blue == 0 ) {
                        blue = 1; 
                    } else if ( blue == 1 ) {
                        blue = 3; 
                    } else if ( blue == 2 ) {
                        blue = 3; 
                    }
                        break;
                }
                
                _colour_possibilities.add( new Color( red, green, blue, _temp_colour.getAlpha() ) );
            }
            // ArrayList now contains colour possibilities

            _smallest_till_now = 10;
            Color _smallest_colour_till_now = null; 
            
            double _start_lum = Analyze_Pixels.Luminosity_calculation( _temp_colour );
            for( Color c : _colour_possibilities ) {
                double _value_calculated = 0;
                //penalty for using colour not already in the array list
                if ( this.existing_colours.contains( c.getRGB() ) == false ) {
                    _value_calculated = 1;
                }
                
                double endLum = Analyze_Pixels.Luminosity_calculation( c );
                _value_calculated += Math.abs( _start_lum - endLum );
                
                if ( _smallest_till_now > _value_calculated ) {
                    _smallest_colour_till_now = c;
                }
                
            }
            
            this.existing_colours.add( _smallest_colour_till_now.getRGB() );
            
            //Set the color
            this._modified_image.setRGB( pointArr[_index_of_smallest].x, pointArr[_index_of_smallest].y, _smallest_colour_till_now.getRGB() );
            this._num_of_pixels_changed++;
        }
        
        
        this._num_of_bits_encoded++;

    }

    // returns number of pixels changed in encoding of file
    public int fetch_num_of_pixels_changed() {
        if ( this.has_completed == false ) {
            return -1;
        } else {
            return this._num_of_pixels_changed;
        }
    }
    

    public int fetch_num_of_bits_written() {
        if ( this.has_completed == false ) {
            return -1;
        } else {
            return this._num_of_bits_encoded;
        }
    }
    

    public int fetch_original_unique_colours_count() {
        return 5;
         
    }
    
    public int fetch_new_unique_colours_count() {
        return 5;
    }
    

    // returns a deep copy of the image
    public BufferedImage get_required_image() throws UnsupportedOperationException {
        if ( this.has_completed == false ) {
            throw new UnsupportedOperationException( "The user has requested the completed StegImage " +
                                                     "from this File_Encoder, but this File_Encoder has not yet " +
                                                     "completed any encode operation." );
        }
        return Class_Abstract.deep_image_copy( this._modified_image );
    }
    
    @Override
    public boolean make_zero() {
        return false;
    }

}