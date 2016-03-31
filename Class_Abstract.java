import java.security.NoSuchProviderException;
import java.util.Collection;
import java.awt.Point;
import java.awt.image.WritableRaster;
import java.awt.Dimension;
import java.util.TreeSet;
import java.security.NoSuchAlgorithmException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.LinkedList;
import java.security.SecureRandom;

// abstract class for decoding and encoding of files

public abstract class Class_Abstract {
    
    protected static final int ALPHA_SHIFT = 24;
    protected static final int RED_SHIFT = 16;
    protected static final int GREEN_SHIFT = 8;
    protected static final int BLUE_SHIFT = 0;
    protected static final int MASK_EXCEPT_END_BYTE = 0x000000FF;
    protected static final int MASK_EXCEPT_END_BIT =  0x00000001;
    protected static final int STARTING_BIT_OF_END_BYTE = 0x00000080;
    protected static final int BITS_IN_ONE_BYTE = 8;
    protected static final int MAX_PIXELS_PER_BIT = 4096;
    protected static final int BYTE_LENGTH_OF_INDICATOR = 8;
    protected static final int BIT_LENGTH_OF_INDICATOR = BYTE_LENGTH_OF_INDICATOR * BITS_IN_ONE_BYTE;
    protected static final int MAX_VALUE_OF_BYTE = 255;
    protected static final int SIZE_OF_INT_IN_BIT = 32;
    
    protected long _signal;
    
    protected final BufferedImage _Img_Orignial; // pointer to the image

    protected Scatter_Administrator _scatter; // instance to check which pixels to process
    
    protected boolean _nonce_Provided; // becomes true when fetch_nonce is called
    
    protected Collection<String> _cautions;
    
    protected int _Pixels_in_one_Bit; // pixels per bit in file encoder/ decoder for payload
    
    /**
     * Simple constructor.
     * new image is deep copied
     */
    protected Class_Abstract( BufferedImage img ) throws Weird_Exception {
        
        if ( Class_Abstract.Is_Image_Valid( img ) == false ) {
            throw new Weird_Exception( "The image provided to Class_Abstract was of an unrecognized type or null." );
        }
        
        this._Img_Orignial = Class_Abstract.deep_image_copy( img );
        
        this._nonce_Provided = false;
        this._cautions = new LinkedList<String>();
        
        try {
            this._scatter = new Scatter_Administrator( this.fetch_Original_Image_Dimension() );
        } catch ( NoSuchAlgorithmException | NoSuchProviderException e) {
            Weird_Exception se = new Weird_Exception( "There was an error with Java cryptographic services that is outside of our control", e );
            se.getStackTrace();
            throw se;
        }
        
    }
    
    public Dimension fetch_Original_Image_Dimension() {
        return new Dimension( this._Img_Orignial.getWidth(), this._Img_Orignial.getHeight() );
    }
    

    public static TreeSet<Integer> fetch_colour_palette_of_5X5( BufferedImage img, Point _point ) {
        
        int startX = _point.x - 2;
        int endX = _point.x + 2;
        int startY = _point.y - 2;
        int endY = _point.y + 2;
        
        if ( startX < 0 ) {
            startX = 0;
        }
        
        if ( endX >= img.getWidth() ) {
            endX = img.getWidth() - 1;
        }
        
        if ( startY < 0 ) {
            startY = 0;
        }
        
        if ( endY >= img.getHeight() ) {
            endY = img.getHeight() - 1;
        }
        
        int width_comparison = ( 1 + endX - startX );
        int height_comparison = ( 1 + endY - startY );
        
        TreeSet<Integer> colorPalette = new TreeSet<Integer>();
        
        for( int y = 0; ( y < height_comparison ); y++ ) {
            for( int x = 0; ( x < width_comparison ); x++ ) {
                colorPalette.add( img.getRGB( x + startX, y + startY ) );
            }
        }
        
        return colorPalette;
    }


    public static boolean Is_Image_Valid( BufferedImage img ) {
        boolean answer = false;
        if ( img != null ) {
            if ( img.getWidth() > 0 && img.getHeight() > 0 ) {
                switch( img.getType() ) {
                case BufferedImage.TYPE_INT_RGB:
                    answer = true;
                    break;
                case BufferedImage.TYPE_INT_ARGB:
                    answer = true;
                    break;
                case BufferedImage.TYPE_INT_ARGB_PRE:
                    System.out.println( "Image Invalid  Type: TYPE_INT_ARGB_PRE" );
                    break;
                case BufferedImage.TYPE_4BYTE_ABGR_PRE:
                    System.out.println( "Image Invalid Type: TYPE_4BYTE_ABGR_PRE" );
                    break;
                case BufferedImage.TYPE_BYTE_INDEXED:
                    System.out.println( "Image Invalid Type: TYPE_BYTE_INDEXED" ); //.gifs
                    break;
                case BufferedImage.TYPE_BYTE_BINARY: 
                    System.out.println( "Image Invalid Type: TYPE_BYTE_BINARY" );
                    break;
                case BufferedImage.TYPE_INT_BGR:
                    answer = true;
                    break;
                case BufferedImage.TYPE_3BYTE_BGR:
                    answer = true;
                    break;
                case BufferedImage.TYPE_4BYTE_ABGR:
                    answer = true;
                    break;
                case BufferedImage.TYPE_CUSTOM:
                    System.out.println( "Image Invalid Type: TYPE_CUSTOM" );
                    break;
                case BufferedImage.TYPE_BYTE_GRAY:
                    System.out.println( "Image Invalid Type: TYPE_BYTE_GRAY" );
                    break;
                case BufferedImage.TYPE_USHORT_GRAY:
                    System.out.println( "Image Invalid Type: TYPE_USHORT_GRAY" );
                    break;
                case BufferedImage.TYPE_USHORT_565_RGB:
                    System.out.println( "Image Invalid Type: TYPE_USHORT_565_RGB" );
                    break;
                case BufferedImage.TYPE_USHORT_555_RGB:
                    System.out.println( "Image Invalid Type: TYPE_USHORT_555_RGB" );
                    break;
                default:
                    System.out.println( "Image Invalid Type: **unrecognized** " + img.getType() );
                }
            }
        }
        return answer;
    }
    
     public int _Pixels_in_one_Bit() {
        return this._Pixels_in_one_Bit;
    }

    public static String Check_Image_Format( BufferedImage img ) {
        String answer = "Null";
        if ( img != null ) {
            if ( img.getWidth() > 0 && img.getHeight() > 0 ) {
                switch( img.getType() ) {
                case BufferedImage.TYPE_INT_RGB:
                    answer = "RGB (Packed Integer)";
                    break;
                case BufferedImage.TYPE_INT_ARGB:
                    answer = "ARGB (Packed Integer)";
                    break;
                case BufferedImage.TYPE_USHORT_555_RGB:
                    answer = "5-5-5 (Short) RGB";
                    break;
                case BufferedImage.TYPE_CUSTOM:
                    answer = "Custom";
                    break;
                case BufferedImage.TYPE_3BYTE_BGR:
                    answer = "BGR (3 Byte)";
                    break;
                case BufferedImage.TYPE_4BYTE_ABGR:
                    answer = "ABGR (4 Byte)";
                    break;
                case BufferedImage.TYPE_BYTE_BINARY: 
                    answer = "Binary";
                    break;
                case BufferedImage.TYPE_BYTE_INDEXED:
                    answer = "6-Bit RGB (Byte Indexed)";
                    break;
                case BufferedImage.TYPE_USHORT_GRAY:
                    answer = "Grayscale (Short)";
                    break;
                case BufferedImage.TYPE_INT_ARGB_PRE:
                    answer = "ARGB (Packed Integer, Premultiplied)";
                    break;
                case BufferedImage.TYPE_INT_BGR:
                    answer = "BGR (Packed Integer)";
                    break;
                case BufferedImage.TYPE_4BYTE_ABGR_PRE:
                    answer = "ABGR (4 Byte, Premultiplied)";
                    break;
                case BufferedImage.TYPE_BYTE_GRAY:
                    answer = "Grayscale (1 Byte)";
                    break;
                case BufferedImage.TYPE_USHORT_565_RGB:
                    answer = "5-6-5 (Short) RGB";
                    break;
                default:
                    answer = "Unrecognized";
                }
            }
        }
        return answer;
    }
    
    // return a number b/w 0(least suitable) and 1(highly suitable) 
    public static double Is_Image_Suitable( BufferedImage img ) {
        
        return 0.5;
    }
    
    // returns number of unique colour in the image
    public int Unique_original_image_colours() {
        return Class_Abstract.Colour_palette_count( this._Img_Orignial );
    }
     
    // Finds the maximum payload size that can be embedded inside the image
     public static int Max_Image_Pay_Load( Dimension _dimension, int _Pixels_in_one_Bit ) {
        if ( _Pixels_in_one_Bit <= 0 ) {
            throw new IllegalArgumentException( "The value _Pixels_in_one_Bit provided to Class_Abstract.Max_Image_Pay_Load was non-positive: " + _Pixels_in_one_Bit + "." );
        }
        
        int answer = _dimension.height * _dimension.width;
        answer /= _Pixels_in_one_Bit;
        answer -= ( BYTE_LENGTH_OF_INDICATOR * BITS_IN_ONE_BYTE );

        return answer;
    }
    
    // max payload that can be written with the given dimension
    public static int Max_Image_Pay_Load( Dimension _dimension ) {
        int answer = _dimension.height * _dimension.width;
        answer /= 8;
        answer -= ( BYTE_LENGTH_OF_INDICATOR );
        return answer;
    }
    
    // maximum pixels that can be encoded in the image
    public static int Max_Pixels_in_One_Bit( Dimension _dimension, int byte_size_of_file ) {
        int answer = _dimension.height * _dimension.width;
        answer /= ( ( byte_size_of_file + BYTE_LENGTH_OF_INDICATOR ) * BITS_IN_ONE_BYTE);
        return answer;
    }
    
    
    // makes all sensitive data zero. returns false if exception occurs
    public abstract boolean make_zero();
    
    // go through every pixel and find number of unique colours
    public static int Colour_palette_count( BufferedImage img ) {
        return Class_Abstract.fetch_palette_colour( img ).size(); //There is no faster way to do this
    }
    
    
    // Returns a TreeSet containing Integers which represent every unique RGBA value of the provided image.

    public static TreeSet<Integer> fetch_palette_colour( BufferedImage img ) {
       //TreeSets have only unique values.
        TreeSet<Integer> tempTree = new TreeSet<Integer>();
        for( int x = 0; x < img.getWidth(); x++ ) {
            for( int y = 0; y < img.getHeight(); y++ ) {
                tempTree.add( new Integer( img.getRGB( x, y ) ) );
            }
        }
        return tempTree;
    }
    
    
    
    
     // Returns the concatenated RGB values of the first several bytes 
     // returns byte array containing Concatenated RGB values 

    public byte[] fetch_Nonce( int number_of_bytes ) throws Weird_Exception {
        number_of_bytes += 2;
        number_of_bytes /= 3;
        
        Point[] reading_points = this._scatter.fetch_primer_points( number_of_bytes );
        
        byte[] result = new byte[ reading_points.length * 3 ];
        int colour_value = 0;
        int temp = 0;
        for( int i = 0; i < reading_points.length; i++ ) {
            colour_value = this._Img_Orignial.getRGB( reading_points[i].x, reading_points[i].y );
            
            temp = ( colour_value >> RED_SHIFT ) & MASK_EXCEPT_END_BYTE;
            temp -= 128;
            result[ 3*i ] = (byte)temp;
            
            temp = ( colour_value >> GREEN_SHIFT ) & MASK_EXCEPT_END_BYTE;
            temp -= 128;
            result[ (3*i)+1 ] = (byte)temp;
            
            temp = ( colour_value >> BLUE_SHIFT ) & MASK_EXCEPT_END_BYTE;
            temp -= 128;
            result[ (3*i)+2 ] = (byte)temp;
        }
        
        return result;
    }

    // provides method for selection of random pixels to calculate in case
    // scatter administrator has not provided one
    public final boolean make_random_scat( SecureRandom _random ) {
        boolean answer = this._scatter.secure_random_set( _random );
        
        if ( answer == true ) {
            this._signal = _random.nextLong();
        }
        
        return answer;
    }


    // returns a list to cautions generated
    public Collection<String> get_cautions() {
        return new LinkedList<String>( this._cautions );
    }
    
    public boolean has_cautions() {
        boolean answer = false;
        if ( this._cautions.size() != 0 ) {
            answer = true;
        }
        return answer;
    }
    
    
    // a deep copy of image is returned
    public BufferedImage fetch_original_image() {
        return Class_Abstract.deep_image_copy( this._Img_Orignial );
    }
    
    // evaluation of the point on the basis of pixel clarity
    public static byte points_evaluation( Point[] pArray, BufferedImage _buffered_image ) {

        boolean temp_result = false;
        
        //Loop through all points -- final result is XOR of all points
        for( Point _point : pArray ) {
            temp_result = temp_result ^ Class_Abstract.evaluation_of_integer( _buffered_image.getRGB( _point.x, _point.y ) );
        }
        
        byte answer;
        if ( temp_result == true ) { //odd number of ones
            answer = 0x01;
        } else {                            //even number of ones
            answer = 0x00;
        }
        return answer;
    }
    
    public static boolean evaluation_of_integer( int intToEvaluate ) {
        
        boolean answer = false;
        int _mask_value = 1;
        
        for( int j = 0; j < SIZE_OF_INT_IN_BIT; j++ ) {
            answer = answer ^ ( (intToEvaluate & _mask_value) != 0 );
            _mask_value = _mask_value << 1;
        }
        return answer;
    }
    
   // function to copy images
    public static BufferedImage deep_image_copy(BufferedImage _buffered_image) {
         ColorModel cm = _buffered_image.getColorModel();
         boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
         WritableRaster raster = _buffered_image.copyData(null);
         return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        }

<<<<<<< HEAD
}
=======
}
>>>>>>> eaabc0e7f4c06c6eed020ee9a784b1780673297b
