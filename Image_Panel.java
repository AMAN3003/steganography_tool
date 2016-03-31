import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Image_Panel extends JPanel
{
    private boolean image_initialized = false;
    private BufferedImage img = null;
    protected static final Dimension DEFAULT_DIMENSION = new Dimension( 100, 100 );
  
    public Image_Panel()
    {
        this.setPreferredSize( Image_Panel.DEFAULT_DIMENSION ); 
    }
    
    public void Set_Image( BufferedImage new_image )
    {
        if ( new_image != null ) {
            img = new_image;
            image_initialized = true;
            this.setPreferredSize( new Dimension(this.img.getWidth(), this.img.getHeight() ) );
        } else {
            image_initialized = false;
            this.setPreferredSize( Image_Panel.DEFAULT_DIMENSION ); 
        }
                
        repaint();
    }
    
    // // this function is never called

    // public void Draw_Again()
    // {
    //     repaint();
    // }
    

    // // this function is never called

    // @Override
    // public void paintComponent(Graphics g)
    // {
    //     super.paintComponent(g);
        
    //     if ( image_initialized ) {
    //         g.drawImage(img, 0, 0, null );
    //     } else {
    //         g.drawString("No image selected", (int)(this.getWidth()/2-50), (int)(this.getHeight()/2-10) );
    //     }
    // }

}