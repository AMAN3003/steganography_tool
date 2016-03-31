import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Point;


public class Analyze_Pixels {
    
    private static final int[] max_sameness_values_ = new int[]{ 1, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 66, 78, 91, 
                                                            105, 120, 136, 153, 171, 190, 210, 231, 253, 276, 300 };
    private static final int RED_INDEX_VALUE = 0;
    private static final int GREEN_VALUE_INDEX = 1;
    private static final int BLUE_VALUE_INDEX = 2;
    private static final int LUMINOSITY_VALUE_INDEX = 3; 
    

    // returns value in (0,1) representing how good point in given picture is good
    // for changing its value
    public static double usability_point_calculation( BufferedImage img, Point p ) {
        
        double _similarity_ = Analyze_Pixels.similarity_calculation( img, p );
        double _smoothness_ = Analyze_Pixels.Smoothness_Calculation( img, p );
        
        
        _similarity_ *= _similarity_;
        _similarity_ *= 1.5; 
        _smoothness_ *= _smoothness_;
        
        double answer = ( _similarity_ + _smoothness_ ) / 2.5;  
        answer = Math.sqrt( answer );
        
        return answer;
    }
    

    // compare RGB and luminosity values
    public static double similarity_calculation( BufferedImage img, Point p ) {
                
        int start_x = p.x - 2;
        int end_x = p.x + 2;
        int start_y = p.y - 2;
        int end_y = p.y + 2;
        
        
        int width_comparison;
        int height_comparison;
        int comparison_area_;
        
        int matches_of_red = 0;
        int matches_of_green = 0;
        int matches_of_blue = 0;
        int matches_of_luminosity = 0;
        int matches_of_distance = 0;
        
        Color[] colour_array;
        
        if ( start_x < 0 ) {
            start_x = 0;
        }
        
        if ( end_x >= img.getWidth() ) {
            end_x = img.getWidth() - 1;
        }
        
        if ( start_y < 0 ) {
            start_y = 0;
        }
        
        if ( end_y >= img.getHeight() ) {
            end_y = img.getHeight() - 1;
        }
        
        width_comparison = ( 1 + end_x - start_x );
        height_comparison = ( 1 + end_y - start_y );
        comparison_area_ =  width_comparison * height_comparison;  //Should be 25 unless we're near an edge
        colour_array = new Color[comparison_area_];
        
        int i = 0;
        for( int y = 0; ( y < height_comparison ) && ( i < comparison_area_ ); y++ ) {
            for( int x = 0; ( x < width_comparison ) && ( i < comparison_area_ ); x++ ) {
                colour_array[i] = new Color( img.getRGB( x + start_x, y + start_y ) );
                i++;
            }
        }
        

        for( int j = 0; j < comparison_area_; j++ ) {
            
            for( int k = j + 1; k < comparison_area_; k++ ) {
                
                if ( colour_array[j].getRed() == colour_array[k].getRed() ) {
                    matches_of_red++;
                }
                
                if ( colour_array[j].getGreen() == colour_array[k].getGreen() ) {
                    matches_of_green++;
                }
                
                if ( colour_array[j].getBlue() == colour_array[k].getBlue() ) {
                    matches_of_blue++;
                }
                
                if ( Analyze_Pixels.Colour_Distance_Calculation( colour_array[j], colour_array[k] ) < 2.1 ) {
                    matches_of_distance++;
                }
                
                double startLum = Analyze_Pixels.Luminosity_calculation( colour_array[j] );
                double endLum = Analyze_Pixels.Luminosity_calculation( colour_array[k] );
                if ( Math.abs( startLum - endLum ) < 1 ) {
                    matches_of_luminosity++;
                }
                
            } //End internal loop k
        } //End loop j
        
        //Each of following values is in range (0,1)
        double redMatchValue = ( (double) matches_of_red ) / ( (double) max_sameness_values_[comparison_area_ - 1] );
        double greenMatchValue = ( (double) matches_of_green ) / ( (double) max_sameness_values_[comparison_area_ - 1] );
        double blueMatchValue = ( (double) matches_of_blue ) / ( (double) max_sameness_values_[comparison_area_ - 1] );
        double luminosityMatchValue = ( (double) matches_of_luminosity ) / ( (double) max_sameness_values_[comparison_area_ - 1] );
        double distanceMatchValue = ( (double) matches_of_distance ) / ( (double) max_sameness_values_[comparison_area_ - 1] );
        
        
        //Take the mean square average
        redMatchValue *= redMatchValue;
        greenMatchValue *= greenMatchValue; 
        blueMatchValue *= blueMatchValue;
        luminosityMatchValue *= luminosityMatchValue;
        distanceMatchValue *= distanceMatchValue;
        
        //DistanceMatchValue is weighted double
        double answer = redMatchValue + greenMatchValue + blueMatchValue + luminosityMatchValue + distanceMatchValue + distanceMatchValue;
        answer /= 6;
        answer = Math.sqrt( answer );
        return answer;
    }


    // compares all the pixels to see variation of values in a paritcular 
    // direction. Examines in all the four directions
    public static double Smoothness_Calculation( BufferedImage img, Point p ) {
        
        int start_x = p.x - 2;
        int end_x = p.x + 2;
        int start_y = p.y - 2;
        int end_y = p.y + 2;
        
        int width_comparison;
        int height_comparison;
        
        int[] tally_for_one_direction = new int[4];
        double[] total_runnings = new double[4];
        
        int num_of_directions_compared = 0;
        
        if ( start_x < 0 ) {
            start_x = 0;
        }
        
        if ( end_x >= img.getWidth() ) {
            end_x = img.getWidth() - 1;
        }
        
        if ( start_y < 0 ) {
            start_y = 0;
        }
        
        if ( end_y >= img.getHeight() ) {
            end_y = img.getHeight() - 1;
        }
        
        width_comparison = ( 1 + end_x - start_x );
        height_comparison = ( 1 + end_y - start_y );
        Color[][] colour_array = new Color[width_comparison][height_comparison];
        
        //Feed values in the colour array

        for( int y = 0; ( y < height_comparison ); y++ ) {
            for( int x = 0; ( x < width_comparison ); x++ ) {
                colour_array[x][y] = new Color( img.getRGB( x + start_x, y + start_y ) );
            }
        }
        
        if ( width_comparison > 2 ) {
            // horizontal comparison
            for( int y = 0; y < height_comparison; y++ ) {
                for ( int x = 0; ( x + 1 ) < width_comparison; x++ ) {
                    Analyze_Pixels.Compare_Two_Pixels_For_smoothness( tally_for_one_direction, colour_array[x][y], colour_array[x+1][y] );

                }
            }
            // log the results
            Analyze_Pixels.Average_smoothness_and_add_squares( total_runnings, tally_for_one_direction, ( width_comparison - 1 ) * height_comparison );
            num_of_directions_compared++;
        }
        
        if ( height_comparison > 2 ) {
            // Vertical Comparison
            for( int x = 0; x < width_comparison; x++ ) {
                for ( int y = 0; ( y + 1 ) < height_comparison; y++ ) {
                    Analyze_Pixels.Compare_Two_Pixels_For_smoothness( tally_for_one_direction, colour_array[x][y], colour_array[x][y+1] );
                }
            }
            //Log results
            Analyze_Pixels.Average_smoothness_and_add_squares( total_runnings, tally_for_one_direction, ( height_comparison - 1 ) * width_comparison );
            num_of_directions_compared++;
        }
        
        if ( height_comparison > 3 && width_comparison > 3 ) {
            // diagonal comparisons

            int num_of_comparisons = 0; //number of comparisons for averaging purposes
            for( int x = 0; ( x + 2 ) < width_comparison; x++ ) {
               
                //Down-right comparisons on the top horizontal
                int temp_x = x;
                for ( int y = 0; ( y + 1 ) < height_comparison && ( temp_x + 1 ) < width_comparison; ) {
                    Analyze_Pixels.Compare_Two_Pixels_For_smoothness( tally_for_one_direction, colour_array[temp_x][y], colour_array[temp_x+1][y+1] );
                    y++; //down
                    temp_x++; //right
                    num_of_comparisons++;
                }
            }
            
          //Down-right comparisons on the left vertical, but not on the top horizontal
            for( int y = 1; ( y + 2 ) < height_comparison; y++ ) {
                int tempY = y;
                int temp_x = 0;
                for( ; (temp_x+1) < width_comparison && (tempY+1) < height_comparison; ) {
                    Analyze_Pixels.Compare_Two_Pixels_For_smoothness( tally_for_one_direction, colour_array[temp_x][tempY], colour_array[temp_x+1][tempY+1] );
                    //System.out.print( "Compare ( " + temp_x + ", " + tempY + " ):( " + (temp_x+1) + ", " + (tempY+1) + " )\t" );
                    num_of_comparisons++;
                    tempY++;
                    temp_x++;
                }
            }
            
            Analyze_Pixels.Average_smoothness_and_add_squares( total_runnings, tally_for_one_direction, num_of_comparisons );
            num_of_directions_compared++;
            
            num_of_comparisons = 0; 
            
            for( int x = 0; ( x + 2 ) < width_comparison; x++ ) {
                int temp_x = x;

                
                for ( int y = height_comparison - 1; y > 0 && ( temp_x + 1 ) < width_comparison; ) {
                    Analyze_Pixels.Compare_Two_Pixels_For_smoothness( tally_for_one_direction, colour_array[temp_x][y], colour_array[temp_x+1][y-1] );
                    y--; 
                    temp_x++; 
                    num_of_comparisons++;
                }
            }
            
            //Up-right comparisons on the left vertical, but not on the top horizontal
            for( int y = height_comparison -2; ( y - 2 ) >= 0; y-- ) {
                int tempY = y;
                int temp_x = 0;
                for( ; (temp_x+1) < width_comparison && (tempY-1) >= 0; ) {
                    Analyze_Pixels.Compare_Two_Pixels_For_smoothness( tally_for_one_direction, colour_array[temp_x][tempY], colour_array[temp_x+1][tempY-1] );
                    num_of_comparisons++;
                    tempY--;
                    temp_x++;
                }
            }
            
            Analyze_Pixels.Average_smoothness_and_add_squares( total_runnings, tally_for_one_direction, num_of_comparisons );
            num_of_directions_compared++;
        }
        
        //average the values then square root 
        
        total_runnings[RED_INDEX_VALUE] /= num_of_directions_compared; 
        total_runnings[GREEN_VALUE_INDEX] /= num_of_directions_compared; 
        total_runnings[BLUE_VALUE_INDEX] /= num_of_directions_compared; 
        total_runnings[LUMINOSITY_VALUE_INDEX] /= num_of_directions_compared;
        
        double finalAnswer = total_runnings[RED_INDEX_VALUE] + total_runnings[GREEN_VALUE_INDEX] +
                             total_runnings[BLUE_VALUE_INDEX] + total_runnings[LUMINOSITY_VALUE_INDEX];
        finalAnswer /= 4;
        finalAnswer = Math.sqrt( finalAnswer );

        return finalAnswer;
    }
    

    // returns true if pixel is close to white or black
    public static boolean Pixel_colour_is_near_extreme( int intColor ) {
        Color _colour_ = new Color( intColor );
        boolean answer = false;
        
        if ( _colour_.getRed() >= 250 && _colour_.getGreen() >= 250 && _colour_.getBlue() >= 250 ) {
            if ( Colour_Distance_Calculation( _colour_, Color.WHITE ) <= 5 ) {
                answer = true;
            }
        } else if ( _colour_.getRed() <= 5 && _colour_.getGreen() <= 5 && _colour_.getBlue() <= 5 ) { //If we're near black
            if ( Colour_Distance_Calculation( _colour_, Color.BLACK ) <= 5 ) {
                answer = true;
            }
        }
        
        return answer;
    }

    // calculates the luminosity of given colour range is [0,255]
    public static double Luminosity_calculation( Color c ) {
        double answer = 0;
        //Values taken from http://jscience.org/experimental/javadoc/org/jscience/computing/ai/vision/GreyscaleFilter.html
        answer += 0.2125 * ( (double) c.getRed() );
        answer += 0.7154 * ( (double) c.getGreen() );
        answer += 0.0721 * ( (double) c.getBlue() );
        return answer;
    }
    
    
    // use eucledian distance to calculate distance b/w RGB values
    public static double Colour_Distance_Calculation( Color a, Color b ) {
        
        double red = a.getRed() - b.getRed();
        double green = a.getGreen() - b.getGreen();
        double blue = a.getBlue() - b.getBlue();
        
        return Math.sqrt( (red*red) + (green*green) + (blue*blue) );
    }

    // compare RGB and luminosity values of two pixels and changes counter accordingly
    private static void Compare_Two_Pixels_For_smoothness( int[] tally_for_one_direction, Color a, Color b  ) {
        
        if ( a.getRed() < b.getRed() ) { //Red channel increasing
            tally_for_one_direction[RED_INDEX_VALUE]++;
        } else if ( a.getRed() > b.getRed() ) { //Red channel decreasing
            tally_for_one_direction[RED_INDEX_VALUE]--;
        }
        
        if ( a.getGreen() < b.getGreen() ) { //Green channel increasing
            tally_for_one_direction[GREEN_VALUE_INDEX]++;
        } else if ( a.getGreen() > b.getGreen() ) { //Green channel decreasing
            tally_for_one_direction[GREEN_VALUE_INDEX]--;
        }
        
        if ( a.getBlue() < b.getBlue() ) { //Blue channel increasing
            tally_for_one_direction[BLUE_VALUE_INDEX]++;
        } else if ( a.getBlue() > b.getBlue() ) { //Blue channel decreasing
            tally_for_one_direction[BLUE_VALUE_INDEX]--;
        }
        
        double luminosity_start = Analyze_Pixels.Luminosity_calculation( a );
        double luminosity_end = Analyze_Pixels.Luminosity_calculation( b );
        if ( luminosity_start < luminosity_end ) { //Luminosity increasing
            tally_for_one_direction[LUMINOSITY_VALUE_INDEX]++;
        } else if ( luminosity_start > luminosity_end ) { //Luminosity channel decreasing
            tally_for_one_direction[LUMINOSITY_VALUE_INDEX]--;
        }
        
    }


    // average the squares then adds into running total
    private static void Average_smoothness_and_add_squares( double[] total_runnings, int[] tally_for_one_direction, int num_of_comparisons ) {
        total_runnings[RED_INDEX_VALUE] += Math.pow( ( ( (double) tally_for_one_direction[RED_INDEX_VALUE] ) / ( (double) num_of_comparisons ) ), 2 );
        total_runnings[GREEN_VALUE_INDEX] += Math.pow( ( ( (double) tally_for_one_direction[GREEN_VALUE_INDEX] ) / ( (double) num_of_comparisons ) ), 2 );
        total_runnings[BLUE_VALUE_INDEX] += Math.pow( ( ( (double) tally_for_one_direction[BLUE_VALUE_INDEX] ) / ( (double) num_of_comparisons ) ), 2 );
        total_runnings[LUMINOSITY_VALUE_INDEX] += Math.pow( ( ( (double) tally_for_one_direction[LUMINOSITY_VALUE_INDEX] ) / ( (double) num_of_comparisons ) ), 2 );
        tally_for_one_direction[RED_INDEX_VALUE] = 0;
        tally_for_one_direction[GREEN_VALUE_INDEX] = 0;
        tally_for_one_direction[BLUE_VALUE_INDEX] = 0;
        tally_for_one_direction[LUMINOSITY_VALUE_INDEX] = 0;
    }
}