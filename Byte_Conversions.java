import java.nio.ByteBuffer;

public class Byte_Conversions {

    public static char[] convert_byte_to_char( byte[] input ) {
        char[] output = null;
        if ( input != null ) {
            output = new char[ input.length ];
            
            for( int i = 0; i < input.length; i++ ) {
                output[i] = (char)input[i];
            }
        }
        return output;
    }


    public static byte[] convert_char_to_byte( char[] input ) {
        byte[] output = null;
        if ( input != null ) {
            output = new byte[ input.length ];
            
            for( int i = 0; i < input.length; i++ ) {
                output[i] = (byte)input[i];
            }
        }
        return output;
    }
    

    public static void make_zero( byte[] byte_array ) {
        for( int i = 0; i < byte_array.length; i++ ) {
            byte_array[i] = 0x00;
        }
    }
    
    public static void make_zero( char[] char_array ) {
        for( int i = 0; i < char_array.length; i++ ) {
            char_array[i] = (char) 0;
        }
    }
    
    public static String convert_byte_array_to_string( byte[] byte_array ) {
        String answer = "";
        for ( int i = 0; i < byte_array.length; i++ ) {
            answer += Byte_Conversions.convert_byte_to_string( byte_array[i] );
        }
        
        return answer;
    }
    

    public static String convert_byte_to_string( byte _byte ) {
        return Integer.toString(_byte &0xf0, 16).toUpperCase().charAt(0) +
                Integer.toString(_byte &0x0f, 16).toUpperCase();
    }
    

    public static boolean array_comparison( byte[] _byte_a, byte[] _byte_b ) {
        boolean answer = true;

        if ( _byte_a.length != _byte_b.length ) {
            answer = false;
        }
        
        for ( int i = 0; ( i < _byte_a.length ) & ( answer == true ); i++ ) {
            if ( _byte_a[i] != _byte_b[i] ) {
                answer = false;
            }
        }
        return answer;
    }
    
    
    public static boolean array_comparison( char[] _char_a, char[] _char_b ) {
        boolean answer = true;

        if ( _char_a.length != _char_b.length ) {
            answer = false;
        }
        
        for ( int i = 0; ( i < _char_a.length ) & ( answer == true ); i++ ) {
            if ( _char_a[i] != _char_b[i] ) {
                answer = false;
            }
        }
        
        return answer;
    }
    
   
    public static byte[] array_deep_copy( byte[] _byte_input ) {
        byte[] _byte_output = new byte[ _byte_input.length ];
        
        for ( int i = 0; i < _byte_input.length ; i++ ) {
            _byte_output[i] = _byte_input[i];
        }
        
        return _byte_output;
    }
    

    public static char[] array_deep_copy( char[] _char_input ) {
        char[] _char_output = new char[ _char_input.length ];
        
        for ( int i = 0; i < _char_input.length ; i++ ) {
            _char_output[i] = _char_input[i];
        }
        
        return _char_output;
    }
    
    
    public static int convert_byte_array_to_int( byte[] _byte_input ) {
        if ( _byte_input.length != 4 ) {
            throw new IllegalArgumentException( "to convert byte to integer byte array should be of length 4." );
        } else {
             return ByteBuffer.wrap(_byte_input).getInt();
        }
    }
    

    public static byte[] convert_int_to_byte_array( int input ) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(input).array();
        return bytes;
    }
    

    public static long convert_byte_array_to_long( byte[] _byte_input ) {
        if ( _byte_input.length != 8 ) {
            throw new IllegalArgumentException( "to convert byte to long byte array should be of length 8." );
        } else {
             return ByteBuffer.wrap(_byte_input).getLong();
        }
    }
    
    public static byte[] convert_long_to_byte_array( long input ) {
        byte[] bytes = ByteBuffer.allocate(8).putLong(input).array();
        return bytes;
    }

    // returns length no. of bytes from original array starting at startOffset
    public static byte[] fetch_sub_array( byte[] original, int startOffset, int length ) {
        if ( original == null ) {
            throw new IllegalArgumentException( "input array given to fetch sub array was null" );
        } else if ( length <= 0 ) {
            throw new IllegalArgumentException( "Length given fetch sub array was not a positive integer" );
        } else if ( (startOffset + length) > original.length ) {
            throw new IllegalArgumentException( "Start Offset + length given to fetch subarray  " +
                                                "is going out of bound of input byte array ");
        }
        
        byte[] answer = new byte[ length ];
        for( int i = 0; i < length; i++ ) {
            answer[i] = original[ startOffset+i];
        }
        
        return answer;
    }
    
    
    public static char[] fetch_sub_array( char[] original, int startOffset, int length ) {
        if ( original == null ) {
            throw new IllegalArgumentException( "Input array given to fetch sub array was null." );
        } else if ( length <= 0 ) {
            throw new IllegalArgumentException( "Length given fetch sub array was not a positive integer." );
        } else if ( (startOffset + length) > original.length ) {
            throw new IllegalArgumentException( "Start Offset + length given to fetch subarray  " +
                                                "is going out of bound of input byte array ");
        }
        
        char[] answer = new char[ length ];
        for( int i = 0; i < length; i++ ) {
            answer[i] = original[ startOffset+i];
        }
        
        return answer;
    }
    

    // concatenated two arrays given as input
    public static byte[] concatenation_of_arrays( byte[] _byte_a, byte[] _byte_b ) {
        if ( _byte_a == null || _byte_b == null ) {
            throw new IllegalArgumentException( "Unable to concatenate the arrays as one or both of " +
                                                "them are null" );
        }
        
        byte[] answer = new byte[ _byte_a.length + _byte_b.length ];
        
        for( int i = 0; i < _byte_a.length; i++ ) {
            answer[i] = _byte_a[i];
        }
        
        for( int j = 0; j < _byte_b.length; j++ ) {
            answer[ _byte_a.length + j ] = _byte_b[j];
        }
        
        return answer;
    }
    
    public static byte[] combine_four_characters( String input ) {
        byte[] answer = { 0x00, 0x00, 0x00 };
        
        if ( input != null & input.length() != 0 ) {
            
            if ( input.length() > 4 ) {
                throw new IllegalArgumentException( "The input given to combine four characters method is : " + input + " which is too long");
            }
            
            input = input.toUpperCase();
            //System.out.println( input );
            int[] work_space = { 0x00, 0x00, 0x00, 0x00 };
            
            for( int i = 0; i < input.length(); i++ ) {
                work_space[i] = (int)input.charAt(i) - (int)' ' + 1;
                if ( (work_space[i] < (int)' ') || (work_space[i] > ( (int)' ' - 1 + 63 ) ) ) {
                    throw new IllegalArgumentException( "Input given to combine four characters: " + input +
                                                        "\nCharacter " + (char)work_space[i] + " is not in permissible range" );
                }
            }
                
            if ( input.length() >= 1 ) {
                work_space[0] = work_space[0] << 2;
            }
            
            if ( input.length() >= 2 ) {
                int temp = work_space[1];
                temp = temp & 0x30;  
                temp = temp >> 4;    
                work_space[0] = work_space[0] | temp;  
                work_space[1] = ( work_space[1] << 4 ) & 0xFF;
            }

            if ( input.length() >= 3 ) {
                int temp = work_space[2];
                temp = temp & 0x3C;  
                temp = temp >> 2;    
                work_space[1] = work_space[1] | temp;  
                work_space[2] = ( work_space[2] << 6 ) & 0xFF;
            }
            
            if ( input.length() == 4 ) {
                int temp = work_space[3] & 0x3F; 
                work_space[2] = work_space[2] | temp;
            }
            
            
            answer[0] = (byte) (work_space[0] & 0xFF );
            answer[1] = (byte) (work_space[1] & 0xFF );
            answer[2] = (byte) (work_space[2] & 0xFF );
                
        }
        
        return answer;
    }
    
    public static String divide_three_bytes( byte[] _byte_array ) {
        String answer = "";
        
        if ( _byte_array != null ) {
            if ( _byte_array.length != 3) {
                throw new IllegalArgumentException( "method divide three bytes can only divide with length of byte array = 3 only." );
            }
            int[] work_space = new int[4];
            int temp;
            
            work_space[0] = _byte_array[0] & 0xFC;
            work_space[0] = work_space[0] >> 2;
            work_space[0] = ( work_space[0] + (int)' ' - 1 );
            
            work_space[1] = _byte_array[0] & 0x03;
            work_space[1] = ( work_space[1] << 4 ) & 0xFF;
            temp = _byte_array[1] & 0xF0;
            temp = temp >> 4;
            work_space[1] = work_space[1] | temp;
            work_space[1] = ( work_space[1] + (int)' ' - 1 );

            
            work_space[2] = _byte_array[1] & 0x0F;
            work_space[2] = work_space[2] << 2;
            temp = _byte_array[2] & 0xC0;
            temp = temp >> 6;
            work_space[2] = work_space[2] | temp;
            work_space[2] = ( work_space[2] + (int)' ' - 1 );
            
            work_space[3] = _byte_array[2] & 0x3F;
            work_space[3] = ( work_space[3] + (int)' ' - 1 );


            char[] intermediateAnswer = new char[work_space.length];
            int outputLength = 0;
            boolean noNullsFound = true;        
            for( int i = 0; i < intermediateAnswer.length && noNullsFound == true; i++ ) {
                if ( work_space[i] == (int)' ' - 1 ) {
                    noNullsFound = false;
                } else {
                    intermediateAnswer[i] = (char)work_space[i];
                    outputLength++;
                }
            }
            
            answer = new String( intermediateAnswer );
            answer = answer.substring( 0, outputLength );
            answer = answer.toLowerCase();

        }
        
        return answer;
    }
    
}