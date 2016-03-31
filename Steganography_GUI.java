import java.awt.Color;
import javax.swing.event.DocumentListener;
import java.awt.EventQueue;
import java.awt.Desktop;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import java.io.InputStream;
import javax.swing.JMenuItem;
import javax.imageio.ImageIO;
import javax.swing.JTextField;
import javax.swing.JMenu;
import javax.swing.border.EmptyBorder;
import java.io.FileNotFoundException;           
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JTextArea;
import javax.swing.JFileChooser;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileOutputStream;
import java.awt.Dimension;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class Steganography_GUI extends JFrame {
    
    private File input_file_selected;
    private File image_file_selected = null;
    private byte[] pay_load_bytes;
    private String extension_of_pay_load_file;
    private BufferedImage image_selected = null;
    private BufferedImage encoded_image;
   
    private volatile boolean image_is_there_to_save = false;
    private volatile boolean file_is_there_to_save = false;
    
    private volatile boolean flag_for_password_validation = true;
    private volatile boolean flag_for_selected_text_validation = true;
    private volatile boolean flag_for_selected_image_validation = true;
    
    private volatile boolean flag_for_password_validation_ok = false;
    private volatile boolean flag_for_password_length_zero = true;
    private volatile boolean flag_for_selected_text_validation_ok = false;
    private volatile boolean flag_for_selected_image_validation_ok = false;
    
    //Global Strings relating to validation information
    private String password_validation_string = "This has never been validated.";
    private String selected_text_validation_string = "This has never been validated.";
    private String selected_image_validation_string = "This has never been validated.";
    
    //Icons relating to validation and help information
    private final ImageIcon help_for_icon = new ImageIcon(Steganography_GUI.class.getResource("/Media/QuestionMark.png"));
    private final ImageIcon positive_validation_for_icon = new ImageIcon(Steganography_GUI.class.getResource("/Media/GreenCheck.png"));
    private final ImageIcon negative_validation_for_icon = new ImageIcon(Steganography_GUI.class.getResource("/Media/RedX.png"));
    private final ImageIcon warning_validation_for_icon = new ImageIcon(Steganography_GUI.class.getResource("/Media/YellowExclamation.png"));
    
    //Containers
    private JPanel content_pane;
    private JScrollPane _scroll_pane_;
    
    //Menu bar and menus
    private JMenuBar menu_bar_;
    private JMenu menu_file_;
    private JMenu mn_help_;
    
    //For the File Menu
    private JMenuItem menu_item_save_encoded_image_;
    private JMenuItem menu_item_save_decoded_text_;
    private JMenuItem menu_item_exit;
    
    private JMenuItem menu_item_instructions_;
    private JMenuItem menu_item_version_info_;
    
    
     Image_Panel _image_panel_;
    
    //Action Listeners
    private Help_Button_Listener help_button_listener_;
    private Action_Button_Listener action_button_listener_;
    private Menu_Listener menu_listener_;
    private Steg_Document_Listener sted_document_listener_; //For comparing passwords
        
    //Text Fields
    private JTextField text_field_for_image_file;     
    private JTextField text_field_for_input_file;
    private JPasswordField text_field_for_password_;
    private JPasswordField text_field_for_password_confirm_;

    //Default window dimension
    private Dimension preferred_window_dimension_;
    private JTextArea text_output_area_;
    private JTextField status_field_;

    //Status JButtons
    private JButton button_for_password_validation_;   
    private JButton button_for_text_validation_;
    private JButton button_for_image_validation_;

    //JButtons
    private JButton button_for_selecting_image;
    private JButton button_for_selecting_text_file_;
    private JButton button_for_encoding_file;
    private JButton button_for_decoding_file;

    //Help JButtons
    private JButton button_for_password_help_;
    private JButton button_for_input_help_;
    private JButton button_for_image_help_;
    private JButton button_for_encode_help_;
    
    //File Choosers & Filter
    private FileFilter image_file_filter_ = new FileNameExtensionFilter("*.png", "png" );
        
        

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Steganography_GUI _frame_ = new Steganography_GUI();
                    _frame_.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Steganography_GUI() {
            
        this.setResizable(false);
        this.setTitle(" Steganographic Based Encoder/Decoder");  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(100, 100, 700, 550);
        this.preferred_window_dimension_ = this.getSize( this.preferred_window_dimension_ );
        
        this.help_button_listener_ = new Help_Button_Listener();
        this.action_button_listener_ = new Action_Button_Listener();
        this.menu_listener_ = new Menu_Listener();
        this.sted_document_listener_ = new Steg_Document_Listener();
        
        //Initialize the menus
        this.menu_bar_ = new JMenuBar();
        this.setJMenuBar( this.menu_bar_);
        
        this.menu_file_ = new JMenu("File");
        this.menu_bar_.add(this.menu_file_);
        
        this.menu_item_save_encoded_image_ = new JMenuItem("Save Encoded Image");
        this.menu_file_.add(this.menu_item_save_encoded_image_);
        this.menu_item_save_encoded_image_.addActionListener( this.menu_listener_ );
        
        this.menu_item_save_decoded_text_ = new JMenuItem("Save Decoded File");
        this.menu_file_.add(this.menu_item_save_decoded_text_);
        this.menu_item_save_decoded_text_.addActionListener( this.menu_listener_ );
        
        this.menu_item_exit = new JMenuItem("Exit");
        this.menu_file_.add(this.menu_item_exit);
        this.menu_item_exit.addActionListener( this.menu_listener_ );
        
        this.mn_help_ = new JMenu("Help");
        this.menu_bar_.add(this.mn_help_);
        
        this.menu_item_instructions_ = new JMenuItem("Instructions");
        this.mn_help_.add(this.menu_item_instructions_);
        this.menu_item_instructions_.addActionListener( this.menu_listener_ );
        
        this.menu_item_version_info_ = new JMenuItem("Version Info");
        this.mn_help_.add(this.menu_item_version_info_);
        this.menu_item_version_info_.addActionListener( this.menu_listener_ );
        //Finished with menus
        
        this.content_pane = new JPanel();
        this.content_pane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(this.content_pane);
        this.content_pane.setLayout(null);
        
        this._scroll_pane_ = new JScrollPane();
        this._scroll_pane_.setBounds(10, 15, 670, 300);
        this._scroll_pane_.setBackground( Color.WHITE );
        this._scroll_pane_.setForeground( Color.WHITE );
        this.content_pane.add(this._scroll_pane_);
        
        this._image_panel_ = new Image_Panel();
        
        try {          
            this._image_panel_.Set_Image( ImageIO.read( Steganography_GUI.class.getResource( "/Media/Steganography.png" ) ) );
        } catch (IOException e) {
             //TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        this._scroll_pane_.setViewportView( this._image_panel_ );
        
        this.text_field_for_image_file = new JTextField();
        this.text_field_for_image_file.setEditable(false);
        this.text_field_for_image_file.setBounds(322, 322, 175, 20);
        this.content_pane.add(this.text_field_for_image_file);
        this.text_field_for_image_file.setColumns(10);
        
        this.button_for_selecting_image = new JButton("Select Image");
        this.button_for_selecting_image.setBounds(507, 321, 110, 23);
        this.content_pane.add(this.button_for_selecting_image);
        this.button_for_selecting_image.addActionListener( this.action_button_listener_ );
        
        this.button_for_image_validation_ = new JButton("");
        this.button_for_image_validation_.setBounds(660, 322, 20, 20);
        this.content_pane.add(this.button_for_image_validation_);
        this.button_for_image_validation_.setIcon(this.negative_validation_for_icon);
        this.button_for_image_validation_.addActionListener( this.help_button_listener_ );
        
        this.button_for_image_help_ = new JButton("");
        this.button_for_image_help_.setBounds(630, 323, 20, 20);
        this.content_pane.add(this.button_for_image_help_);
        this.button_for_image_help_.setIcon(this.help_for_icon);
        this.button_for_image_help_.addActionListener( this.help_button_listener_ );
        
        this.button_for_encoding_file = new JButton("Encode");
        this.button_for_encoding_file.setBounds(455, 442, 80, 23);
        this.content_pane.add(this.button_for_encoding_file);
        this.button_for_encoding_file.addActionListener( this.action_button_listener_ );
        
        this.button_for_decoding_file = new JButton("Decode");
        this.button_for_decoding_file.setBounds(537, 442, 80, 23);
        this.content_pane.add(this.button_for_decoding_file);
        this.button_for_decoding_file.addActionListener( this.action_button_listener_ );
        
        this.text_field_for_input_file = new JTextField();
        this.text_field_for_input_file.setBounds(322, 353, 175, 20);
        this.content_pane.add(this.text_field_for_input_file);
        this.text_field_for_input_file.setEditable(false);
        this.text_field_for_input_file.setColumns(10);
        
        this.button_for_selecting_text_file_ = new JButton("Select File");
        this.button_for_selecting_text_file_.setBounds(507, 352, 110, 23);
        this.content_pane.add(this.button_for_selecting_text_file_);
        
        this.button_for_text_validation_ = new JButton("");
        this.button_for_text_validation_.setBounds(660, 353, 20, 20);
        this.content_pane.add(this.button_for_text_validation_);
        this.button_for_text_validation_.setIcon(this.negative_validation_for_icon);
        
        this.button_for_input_help_ = new JButton("");
        this.button_for_input_help_.setBounds(630, 353, 20, 20);
        this.content_pane.add(this.button_for_input_help_);
        this.button_for_input_help_.setIcon(this.help_for_icon);
        
        this.button_for_password_help_ = new JButton("");
        this.button_for_password_help_.setBounds(630, 384, 20, 20);
        this.content_pane.add(this.button_for_password_help_);
        this.button_for_password_help_.setIcon(this.help_for_icon);
        
        this.button_for_encode_help_ = new JButton("");
        this.button_for_encode_help_.setBounds(630, 442, 20, 20);
        this.content_pane.add(this.button_for_encode_help_);
        this.button_for_encode_help_.setIcon(this.help_for_icon);
                
        this.button_for_password_validation_ = new JButton("");
        this.button_for_password_validation_.setBounds(660, 384, 20, 20);
        this.content_pane.add(this.button_for_password_validation_);
        this.button_for_password_validation_.setIcon(this.warning_validation_for_icon);
                
        this.text_field_for_password_ = new JPasswordField();
        this.text_field_for_password_.setBounds(455, 384, 162, 20);
        this.content_pane.add(this.text_field_for_password_);
        this.text_field_for_password_.setColumns(10);
                
        this.text_field_for_password_confirm_ = new JPasswordField();
        this.text_field_for_password_confirm_.setBounds(455, 414, 162, 20);
        this.content_pane.add(this.text_field_for_password_confirm_);
        this.text_field_for_password_confirm_.setColumns(10);
                
        JLabel label_password_2_ = new JLabel("Confirm Password:");
        label_password_2_.setBounds(322, 415, 125, 14);
        this.content_pane.add(label_password_2_);
        label_password_2_.setFont(new Font("Tahoma", Font.PLAIN, 14));
                
        JLabel label_password_ = new JLabel("Password:");
        label_password_.setBounds(322, 384, 65, 14);
        this.content_pane.add(label_password_);
        label_password_.setFont(new Font("Tahoma", Font.PLAIN, 14));
        
        JScrollPane scroll_pane_ = new JScrollPane();
        scroll_pane_.setBounds(10, 322, 306, 174);
        content_pane.add(scroll_pane_);
        
        text_output_area_ = new JTextArea();
        text_output_area_.setEditable(false);
        scroll_pane_.setViewportView(text_output_area_);
        text_output_area_.setLineWrap(true);
        text_output_area_.setWrapStyleWord(true);
        text_output_area_.setText("Status Info:");
        
        this.status_field_ = new JTextField();
        this.status_field_.setText("No actions has been performed yet");
        this.status_field_.setEditable(false);
        this.status_field_.setBounds(322, 476, 358, 20);
        this.content_pane.add(status_field_);
        this.status_field_.setColumns(10);
        
        this.text_field_for_password_.getDocument().addDocumentListener( this.sted_document_listener_ );
        this.text_field_for_password_confirm_.getDocument().addDocumentListener( this.sted_document_listener_ );
        this.button_for_password_validation_.addActionListener( this.help_button_listener_ );
        this.button_for_password_help_.addActionListener( this.help_button_listener_ );
        this.button_for_input_help_.addActionListener( this.help_button_listener_ );
        this.button_for_encode_help_.addActionListener( this.help_button_listener_ );
        this.button_for_text_validation_.addActionListener( this.help_button_listener_ );
        this.button_for_selecting_text_file_.addActionListener( this.action_button_listener_ );
              
        this.Validate_User_Input_and_Refresh();
        
    }
    

    private boolean Validate_User_Input_and_Refresh() {
        boolean bool_answer_ = true;

        boolean temp = this.Validate_Image_File();
        bool_answer_ = bool_answer_ && temp;

        temp = this.Validate_Input_File();
        bool_answer_ = bool_answer_ && temp;
        
        temp = this.Validate_Password();
        bool_answer_ = bool_answer_ && temp;
                    
        this.Refresh_Display_Options();
        return bool_answer_;
    }
    
    // flag_for_selected_image_validation_ok must be updated before 
    // executing this method
    private boolean Validate_Input_File() {
        
        if ( input_file_selected == null ) {
            this.selected_text_validation_string = "Input file hasn't been selected.";
            this.flag_for_selected_text_validation_ok = false;
            
        } else { //Not null!
            
            if ( input_file_selected.canRead() == false ) {
                this.selected_text_validation_string = "Unable to read from the selected input file (" + 
                                                       input_file_selected.getName() + ").";
                this.flag_for_selected_text_validation_ok = false;
                
            } else if ( input_file_selected.length() > Integer.MAX_VALUE ) { //Can read, but too long
                this.selected_text_validation_string = "Input file can't be handled using java.\n" +
                        "Size of selected file must be smaller than 2GB.";
                this.flag_for_selected_text_validation_ok = false;
            } else {  //Can read, but not too long
                
                if ( ( flag_for_selected_image_validation_ok == true ) && ( image_selected != null ) ) {
                    
                    Dimension _dimension = new Dimension( image_selected.getWidth(), image_selected.getHeight() );
                    int max_pay_load_size_ = Abstract_Administrator.Max_Image_Pay_Load( _dimension );
                    long file_size_ = input_file_selected.length();
                    if ( file_size_ > max_pay_load_size_ ) { //Won't fit!
                        this.selected_text_validation_string = "The input file cannot be encoded in the selected image.\n" +
                                                              "Select a larger image or a smaller input file.";
                        this.flag_for_selected_text_validation_ok = false;
                        
                    } else { //Can fit!
                        
                        this.selected_text_validation_string = "Input file is valid and will fit into the image at " +
                                                              Abstract_Administrator.Max_Pixels_in_One_Bit( _dimension, (int)file_size_ ) +
                                                              " pixels per bit.";
                        this.flag_for_selected_text_validation_ok = true;
                    }
                    
                } else {
                    
                    this.selected_text_validation_string = "Input file appears valid, but its size will be checked when an image file is selected.";
                    this.flag_for_selected_text_validation_ok = true;
                    
                } //End else-okay
            } //End else-can read
        } // End else-not null
        
        return this.flag_for_selected_text_validation_ok;
        
    }
    
    private boolean Validate_Image_File() {
        this.selected_image_validation_string = "Image successfully validated.";
        
        if ( image_selected == null || image_file_selected == null ) {
            
            this.selected_image_validation_string = "Selected image not found.";
            this.flag_for_selected_image_validation_ok = false;
            
        } else {
            
            this.flag_for_selected_image_validation_ok = Abstract_Administrator.Is_Image_Valid( image_selected );
        
            if ( this.flag_for_selected_image_validation_ok == true ) {
                this.selected_image_validation_string = "Selected image is valid.";
            } else {
                this.selected_image_validation_string = "Selected image is not valid.\n Might be of incompatible type.";
            }
        
        }
        
        return this.flag_for_selected_image_validation_ok;
    }
        
    private boolean Validate_Password() {

        char[] password_chars_ = this.text_field_for_password_.getPassword();
        char[] confirm_chars_ = this.text_field_for_password_confirm_.getPassword();
        
        if ( password_chars_.length == 0 ) {
            this.flag_for_password_length_zero = true;
        } else {
            this.flag_for_password_length_zero = false;
        }
            
        this.flag_for_password_validation_ok = Byte_Conversions.array_comparison( password_chars_, confirm_chars_ );
        Byte_Conversions.make_zero( password_chars_ );
        Byte_Conversions.make_zero( confirm_chars_ );
        
        if ( this.flag_for_password_validation_ok == true ) {
            if ( this.flag_for_password_length_zero == true ) {
                this.password_validation_string = "If you do not enter a password, then anyone with the " +
                                                      "application can easily detect your hidden message.";
            } else {
                this.password_validation_string = "Passwords match.";
            }
        } else {
            this.password_validation_string = "Passwords do not match; please retype password.";
        }
        
        return this.flag_for_password_validation_ok;
    }
        
    private void Refresh_Display_Options() {
        
        _scroll_pane_.setViewportView( _image_panel_ );
              
        //File options (save image/output)
        this.menu_item_save_decoded_text_.setEnabled( this.file_is_there_to_save );
        this.menu_item_save_encoded_image_.setEnabled( this.image_is_there_to_save );
        
        //Action buttons
        this.button_for_encoding_file.setEnabled( this.flag_for_password_validation_ok && 
                                   this.flag_for_selected_image_validation_ok &&
                                   this.flag_for_selected_text_validation_ok == true );
        
        this.button_for_decoding_file.setEnabled( this.flag_for_password_validation_ok && 
                                   this.flag_for_selected_image_validation_ok == true );

    
    //Image Validation Starting
    
        if ( this.flag_for_selected_image_validation_ok == true ) {
            this.button_for_image_validation_.setIcon( this.positive_validation_for_icon );
        } else {
            this.button_for_image_validation_.setIcon( this.negative_validation_for_icon );
        }
        
        if ( this.flag_for_selected_text_validation_ok == true ) {
            this.button_for_text_validation_.setIcon( this.positive_validation_for_icon );
        } else {
            this.button_for_text_validation_.setIcon( this.negative_validation_for_icon );
        }
        
        if ( this.flag_for_password_validation_ok == true ) {
            if ( this.flag_for_password_length_zero == true ) {
                this.button_for_password_validation_.setIcon( this.warning_validation_for_icon );
            } else {
                this.button_for_password_validation_.setIcon( this.positive_validation_for_icon );
            }
        } else {
            this.button_for_password_validation_.setIcon( this.negative_validation_for_icon );
        }
                
        this.button_for_image_validation_.setVisible( this.flag_for_selected_image_validation );
        this.button_for_text_validation_.setVisible( this.flag_for_selected_text_validation );
        this.button_for_password_validation_.setVisible( this.flag_for_password_validation );
        
    //image validation ending

    }
    
    private void Set_All_Show_Validation_Flags( boolean visible ) {
        this.flag_for_password_validation = visible;
        this.flag_for_selected_text_validation = visible;
        this.flag_for_selected_image_validation = visible;
    }
    
    private class Help_Button_Listener implements ActionListener {

        @Override
        public void actionPerformed( ActionEvent ae ) {
            String str_help_message_ = "Unexpected action event source: " + ae.getSource().toString();
            String str_help_title_ = "Error";
            int message_type = JOptionPane.INFORMATION_MESSAGE;
            boolean bool_show_message = true;
            
            Validate_User_Input_and_Refresh();    
            
            if ( ae.getSource() == button_for_password_help_ ) {
                str_help_message_ = "Encryption Password is for the security of your file to be encoded.\n" +
                                 "Type your password in the \"Password\" box, and then re-type it in the \"Confirm Password\" box.";
                str_help_title_ = "Password Feature Info";
            } else if ( ae.getSource() == button_for_input_help_ ) {
                str_help_message_ = "Select your file which needs to be encoded by clicking \"Select File\".\n" +
                                 "The text file will be encoded within the image you select.";
                str_help_title_ = "Input File Selection Info";
            } else if ( ae.getSource() == button_for_image_help_ ) {
                str_help_message_ = "Select an image by clicking \"Select Image\".\n" +
                                 "The text file is encoded within the image.";
                str_help_title_ = "Image File Selection Info";
            } else if ( ae.getSource() == button_for_encode_help_ ) {
                str_help_message_ = "Encoding is embedding of file to be hidden in an image.\n" + 
                                 "In order to encode your file you must select (1) an image, (2) a file, and (3) a password.\n\n" +
                        
                                 "Decoding is extraction of the file encoded inside an image.\n" +
                                 "In order to decode you must select (1) an encoded image, and (2) enter encryption password.";
                
                str_help_title_ = "Encoding/Decoding Info";
            } else if ( ( ae.getSource() == button_for_password_validation_ ) || ( ae.getSource() == button_for_text_validation_ ) ||  
                        ( ae.getSource() == button_for_image_validation_ ) ) {
                
                if ( ae.getSource() == button_for_password_validation_ ) {
                    str_help_message_ = password_validation_string;
                    if ( flag_for_password_validation_ok == false ) {
                        str_help_title_ = "Password Invalid";
                        message_type = JOptionPane.ERROR_MESSAGE;
                    } else {
                        if ( flag_for_password_length_zero == true ) {
                            str_help_title_ = "No Password Entered";
                            message_type = JOptionPane.WARNING_MESSAGE;
                        } else {
                            str_help_title_ = "Password Valid";
                        }
                    }
                } else if ( ae.getSource() == button_for_text_validation_ ) {
                    str_help_message_ = selected_text_validation_string;
                    
                    if ( flag_for_selected_text_validation_ok == false ) {
                        str_help_title_ = "Input File Selection Invalid";
                        message_type = JOptionPane.ERROR_MESSAGE;
                    } else {
                        str_help_title_ = "Input File Selection Valid"; 
                    }
                } else if ( ae.getSource() == button_for_image_validation_ ) {
                    
                    str_help_message_ = selected_image_validation_string;
                    if ( flag_for_selected_image_validation_ok == false ) {
                        str_help_title_ = "Image Selection Invalid";
                        message_type = JOptionPane.ERROR_MESSAGE;
                    } else {
                        str_help_title_ = "Image Selection Valid";
                    }
                }
            }
            
            if ( bool_show_message == true ) {
                JOptionPane.showMessageDialog( null, str_help_message_, str_help_title_, message_type );
            }
        }
    }
    
    private class Action_Button_Listener implements ActionListener {

        @Override
        public void actionPerformed( ActionEvent ae ) {

            String operation_description_ = "Other";
            long nano_start_time_ = System.nanoTime();
            long nano_total_time_ = 0;
            
            boolean bool_is_everything_valid = Validate_User_Input_and_Refresh();    //That function validates input and refreshes the screen
            if ( ( ae.getSource() == button_for_decoding_file ) || ( ae.getSource() == button_for_encoding_file ) ) {
                if ( bool_is_everything_valid == false ) {
                    Set_All_Show_Validation_Flags( true );    //Show validations information if there is a problem
                }
            }
            
            if (ae.getSource() == button_for_decoding_file) {
                operation_description_ = "Decode";
                String text_output_ = "\n**  Starting Decoding Operation  **\n";
                //Zeroize previous decoded payload (if applicable)
                if ( pay_load_bytes != null ) {
                    Byte_Conversions.make_zero( pay_load_bytes );
                }
                extension_of_pay_load_file = "";
                file_is_there_to_save = false; //Anticipate failure
                
                char[] password = new char[1]; //Just so it's initialize
                
                try {
                    if ( image_selected == null) {
                        JOptionPane.showMessageDialog( null, "An image must be selected before decoding!", "Error", JOptionPane.ERROR_MESSAGE );
                    } else if ( Validate_Image_File() == false ) {
                        JOptionPane.showMessageDialog( null, "Selected image file is invalid.\n" +
                                                             "Refer to output window for more information.", "Error", JOptionPane.ERROR_MESSAGE );
                    } else if ( Validate_Password() == false ) {
                        JOptionPane.showMessageDialog( null, "Given passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE ); 
                    } else {
                        password = text_field_for_password_.getPassword();
                        //Attempt to decode the file
                        File_Decoder_Administrator dm = null;
                        try {
                            dm = new File_Decoder_Administrator(image_selected);
                            dm.decode(password);
                            Byte_Conversions.make_zero( password );
                            if ( dm.has_completed_successfully() == true ) {
                                pay_load_bytes = dm.fetch_pay_load();    //error somehow..
                                extension_of_pay_load_file = dm.fetch_file_extension();
                                file_is_there_to_save = true;
                                text_output_ += "Decode Successful\n" +
                                              "File extension: " + dm.fetch_file_extension() + "\n";
                            }

                        } catch ( Not_Able_To_Synchronize_Exception utse ) {
                            Byte_Conversions.make_zero( password ); //The JOptionPane could prevent the finally clause (make_zero password) from running indefinitely
                            if ( dm != null ) {
                                dm.make_zero();
                                dm = null;
                            }
                            utse.printStackTrace();
                            JOptionPane.showMessageDialog( null, "The Decoder was unable to find a payload in the image file.\n" +
                                                                 "Confirm that the entered password is correct\n" +
                                                                 "Sometimes applying different effects to file like " +
                                                                 "cropping, color changes, etc. may make the payload unrecoverable.", 
                                                                 "Error", JOptionPane.ERROR_MESSAGE ); 
                        } catch ( Weird_Exception se ) {
                            Byte_Conversions.make_zero( password ); 
                            if ( dm != null ) {
                                dm.make_zero();
                                dm = null;
                            }
                            se.printStackTrace();
                            JOptionPane.showMessageDialog( null, "The Decoder encountered an error while decoding the file.\n" +
                                                                 "Erro Message :\n" + se.getMessage(), 
                                                                 "Error", JOptionPane.ERROR_MESSAGE ); 
                        } finally {
                            Byte_Conversions.make_zero( password );
                            text_output_area_.append( text_output_ );
                            if ( dm != null ) {
                                dm.make_zero();
                                dm = null;
                            }
                        }
                        
                        //Check in time before the JOptionPane
                        nano_total_time_ = System.nanoTime() - nano_start_time_;
                        if ( file_is_there_to_save == true ) {
                            JOptionPane.showMessageDialog(Steganography_GUI.this, "File Decoded Successfully.\n" +
                                                                        "Use file menu to save the file to your hard drive.", 
                                                                        "Success", JOptionPane.INFORMATION_MESSAGE );
                        }
                    }
                    
                } finally {
                    Byte_Conversions.make_zero( password );
                }
                
            } else if ( ae.getSource() == button_for_encoding_file) {
                operation_description_ = "Encode";
                
                char[] password = new char[1]; //Just to ensure it's been started
                String text_output_ = "\n** Starting Encoding Operation **\n";
                try {
                    if ( input_file_selected == null ) {
                       JOptionPane.showMessageDialog( null, "A file must be selected before encoding!", "ERROR", JOptionPane.ERROR_MESSAGE );
                    }
                    else if ( Validate_Input_File() == false ) {
                        JOptionPane.showMessageDialog( null, "Selected input file is invalid", "ERROR", JOptionPane.ERROR_MESSAGE );
                    }
                    else if( image_selected == null ){
                       JOptionPane.showMessageDialog( null, "Image must be selected before encoding", "ERROR", JOptionPane.ERROR_MESSAGE );
                    }
                    else if( Validate_Image_File()== false ) {
                       JOptionPane.showMessageDialog( null, "The selected image file is invalid.\n" +
                                                             "For more info refer to the output window.", "Error", JOptionPane.ERROR_MESSAGE ); 
                    }
                    else if ( Validate_Password()== false ) {
                       JOptionPane.showMessageDialog(null, "The provided passwords do not match!","Error", JOptionPane.ERROR_MESSAGE); 
                    } else {
                        password = text_field_for_password_.getPassword();
                        image_is_there_to_save = false; //Anticipate failure
                        
                        File_Encoder_Administrator em = null;
                        File_Decoder_Administrator dm = null;
                        try {
                            
                            em = new File_Encoder_Administrator( image_selected, input_file_selected );
                            em.encode( password );
                            
                            if( em.has_completed_successfully() == true ) {
                                
                                BufferedImage tempEncodedImage = em.get_required_image();
                                text_output_ += "Encoding successful\n";
                                em.make_zero();
                                em = null;
                                
                                
                                try {
                                    dm = new File_Decoder_Administrator( tempEncodedImage );
                                    dm.decode( password );
                                    Byte_Conversions.make_zero( password );
                                    
                                    if ( dm.has_completed_successfully() == true ) {
                                        image_is_there_to_save = true;
                                        encoded_image = tempEncodedImage;
                                        tempEncodedImage = null;
                                    }
                                    text_output_ += "Test decode succeeded";
                                } catch ( Weird_Exception se ) {
                                    Byte_Conversions.make_zero( password );
                                    text_output_ += "Test decode failed\n";
                                    se.printStackTrace();
                                }
                                
                                if ( dm != null ) {
                                    dm.make_zero();
                                }
                            } else {
                                text_output_ += "Encoding failed\n";
                            }
                        } catch (UnsupportedOperationException uoe) {
                            Byte_Conversions.make_zero( password );
                            if ( em != null ) {
                                em.make_zero();
                                em = null;
                            }
                          //DM is not yet created in the case of expect unsupported operation exceptions
                            JOptionPane.showMessageDialog( null, "An internal error occurred:\n" + uoe.getMessage(),"Error", JOptionPane.ERROR_MESSAGE ); 
         
                        } catch (Weird_Exception se) {
                            Byte_Conversions.make_zero( password );
                            if ( em != null ) {
                                em.make_zero();
                                em = null;
                            }
                            //DM is not yet created in the case of a Weird_Exception
                            JOptionPane.showMessageDialog( null, "The File_Encoder encountered an error while encoding the file.\n" +
                                                                 "Error Message:\n\t" + se.getMessage(), 
                                                                 "Error", JOptionPane.ERROR_MESSAGE );
                        } finally {
                            Byte_Conversions.make_zero( password );
                            if ( em != null ) {
                                em.make_zero();
                                em = null;
                            }
                            if ( dm != null ) {
                                dm.make_zero();
                                dm = null;
                            }
                        }
                        //Check in time before the JOptionPane
                        nano_total_time_ = System.nanoTime() - nano_start_time_;
                        if ( image_is_there_to_save == true ) {
                            JOptionPane.showMessageDialog(Steganography_GUI.this, "Image encoded successfully!\n" +
                                                                        "Use file menu to save it to your hard drive.", 
                                                                        "Success", JOptionPane.INFORMATION_MESSAGE );   
                        }
                    }
                } finally {
                    Byte_Conversions.make_zero(password);
                    text_output_area_.append( text_output_ );
                }
                
            } else if ( ae.getSource() == button_for_selecting_text_file_ ) {
                operation_description_ = "Select Payload";          
                JFileChooser file_chooser_ = new JFileChooser();  
                
                int return_value_  = file_chooser_.showOpenDialog(Steganography_GUI.this);
                                
                if (return_value_ == JFileChooser.APPROVE_OPTION){
                    
                    input_file_selected = file_chooser_.getSelectedFile();   
                    text_field_for_input_file.setText(input_file_selected.getName());
                    
                    String text_output_ = "\n** Payload File Loaded **\n";
                    if ( input_file_selected.length() > Integer.MAX_VALUE ) {
                        text_output_ += "Input file is too long.\n";
                    } else {
                        text_output_ += "File length: " + input_file_selected.length() + " bytes";
                        
                        if ( Validate_Image_File() == true ) {
                            text_output_ += "\n** Payload File and Image File Detected **\n";
                            
                            if ( input_file_selected.length() > Integer.MAX_VALUE ) {
                                text_output_ += "Input File is too long.\n";
                            } else {
                                int pixels_per_bit_ = Abstract_Administrator.Max_Pixels_in_One_Bit( new Dimension( image_selected.getWidth(), image_selected.getHeight() ), (int)input_file_selected.length() );
                                
                                if ( pixels_per_bit_ > 0 ) {
                                    text_output_ += "Expected pixels per bit: " + pixels_per_bit_ + "\n" +
                                                  "(Recommended : 20+)";
                                } else {
                                    text_output_ += "Input File is too long for image.\n";
                                }
                            }
                        }
                    } //End else file length okay
                    text_output_area_.append( text_output_ );
                } //End JFileChoose.APPROVE_OPTION

                Validate_User_Input_and_Refresh();
            
            } else if ( ae.getSource() == button_for_selecting_image ) {
                operation_description_ = "Select Image";
                JFileChooser file_chooser_ = new JFileChooser();
                
                try {
                    file_chooser_ = new JFileChooser();  
                    file_chooser_.removeChoosableFileFilter(file_chooser_.getAcceptAllFileFilter());
                    file_chooser_.addChoosableFileFilter(image_file_filter_); //assign file filter for image files
                                
                    int return_value_  = file_chooser_.showOpenDialog(Steganography_GUI.this);
                                
                    if (return_value_ == JFileChooser.APPROVE_OPTION){
                                    
                        image_file_selected = file_chooser_.getSelectedFile();
                        image_selected = ImageIO.read(image_file_selected);   
                        _image_panel_.Set_Image(image_selected);  
                        text_field_for_image_file.setText(image_file_selected.getName());
                        System.out.println( "Image format is: " + Class_Abstract.Check_Image_Format( image_selected ) );
                        
                        String text_output_ = "\n** Image File Loaded **\n";
                        if ( Class_Abstract.Is_Image_Valid( image_selected ) == true ) {
                            int int_image_size_ = ( 100 * ( image_selected.getWidth() * image_selected.getHeight() ) );
                            int_image_size_ = int_image_size_/1024/1024;
                            double dblImageSize = int_image_size_;
                            dblImageSize /= 100;
                            
                            
                            text_output_ += "Image Format: " + Class_Abstract.Check_Image_Format( image_selected ) + "\n" +
                                          "Image Dimensions: " + image_selected.getWidth() + " x " + image_selected.getHeight() + "\n" +
                                          "Image Size: " + dblImageSize + " megapixels\n" +
                                          "Image Unique Colors: " + Class_Abstract.Colour_palette_count( image_selected ) + "\n" ;
                            
                            if ( Validate_Input_File() == true ) {
                                
                                text_output_ += "\n** Payload File and Image File Detected **\n";
                                
                                if ( input_file_selected.length() > Integer.MAX_VALUE ) {
                                    text_output_ += "Input File is too long.\n";
                                } else {
                                    int pixels_per_bit_ = Abstract_Administrator.Max_Pixels_in_One_Bit( new Dimension( image_selected.getWidth(), image_selected.getHeight() ), (int)input_file_selected.length() );
                                    
                                    if ( pixels_per_bit_ > 0 ) {
                                        text_output_ += "Expected pixels per bit: " + pixels_per_bit_ + "\n" +
                                                      "(Recommended : 20+)";
                                    } else {
                                        text_output_ += "Input File is too long for image.\n";
                                    }
                                    
                                }
                            }
                            
                        } else {
                            text_output_ += "Image type is not valid\n" +
                                          "Selected Image tpe is:\n" +
                                          Class_Abstract.Check_Image_Format( image_selected ) + "\n" ;                        }
                        
                        text_output_area_.append( text_output_ );

                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                  }
            } 

            Validate_User_Input_and_Refresh();
            
            if ( nano_total_time_ > 0 ) {
                nano_total_time_ /= 10000000;
                double seconds = nano_total_time_;
                seconds /= 100;
                
                status_field_.setText( "Operation Finished " + operation_description_ + " in " + seconds + " seconds." );
            }
            
        }
        
    }
    
    private class Menu_Listener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            String str_help_message_ = "Unexpected action event source:\n" + ae.getSource().toString();
            String str_help_title_ = "Error";
            boolean bool_show_message = true;
                                
            
 
            if ( ae.getSource() == menu_item_save_encoded_image_ ) {
                
                JFileChooser file_chooser_ = new JFileChooser();
                try {                   
                    File encoded_image_file_ = new File("encoded_image.png");
                    file_chooser_.setSelectedFile(encoded_image_file_);
                    int return_value_ = file_chooser_.showSaveDialog(Steganography_GUI.this);
                    
                    if ( return_value_ == JFileChooser.APPROVE_OPTION ) {
                        
                        if( file_chooser_.getSelectedFile().exists() ) {
                            JOptionPane.showMessageDialog( null, "File already exists!", "Error", JOptionPane.CANCEL_OPTION );
                        }
                        else {  
                            ImageIO.write(encoded_image, "png", file_chooser_.getSelectedFile() );
                            
                            if ( encoded_image_file_.exists() ) {
                               JOptionPane.showMessageDialog(null,"Image Saved!","Info",JOptionPane.INFORMATION_MESSAGE); 
                            }
                        } 
                    }
                    
                    bool_show_message = false;
                    str_help_message_ = "Save image was hit.";
                    str_help_title_ = "Menu Action Event";
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if ( ae.getSource() == menu_item_save_decoded_text_ ) {
                JFileChooser file_chooser_ = new JFileChooser();
                try {
                    
                    File recoveredPayloadFile = new File( "recoveredPayload" + extension_of_pay_load_file );
                    file_chooser_.setSelectedFile( recoveredPayloadFile );
                    
                    int return_value_ = file_chooser_.showSaveDialog(Steganography_GUI.this);
                   
                    
                    if (return_value_ == JFileChooser.APPROVE_OPTION){
                        //error message if file exists
                        File destinationFile = file_chooser_.getSelectedFile();
                        if ( destinationFile.exists() ) {
                            JOptionPane.showMessageDialog(null, "File already exists!\nProgram will not overwrite a file.", "Error", JOptionPane.CANCEL_OPTION );
                        } else {
                            
                            //Save pay_load_bytes to the destination specified by destinationFile
                            FileOutputStream fos = new FileOutputStream( destinationFile );
                            fos.write( pay_load_bytes );
                            fos.close();
                            
                            //Check existence, and display proper message
                            if (destinationFile.exists() == true) {
                                JOptionPane.showMessageDialog(null, "File saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Error occurred while saving the file.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }       
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bool_show_message = false;
                str_help_message_ = "Save text was hit.";
                str_help_title_ = "Menu Action Event";
            } else if ( ae.getSource() == menu_item_exit ) {
                bool_show_message = false;
                dispose();   
            } else if( ae.getSource() == menu_item_instructions_ ) {
               
                String[] file_names_ = { "Help" };
                String[] file_extensions_ = { ".pdf" };
                bool_show_message = false;
                try {
                    int buffer_size_ = 4096;
                    byte[] file_bytes_ = new byte[buffer_size_];
                    
                    for( int i = 0; i < file_names_.length; i++ ) {
                        File temp_file_ = File.createTempFile( file_names_[i], file_extensions_[i] );
                        InputStream is = Steganography_GUI.class.getResourceAsStream( "Help/" + file_names_[i] + file_extensions_[i] );
                        FileOutputStream fos = new FileOutputStream( temp_file_ );
                        int number_of_bytes = 0;
                        
                        do {
                            number_of_bytes = is.read( file_bytes_ );
                            if ( number_of_bytes > 0 ) {
                                fos.write( file_bytes_, 0, number_of_bytes );
                            }
                        } while ( number_of_bytes > 0 );
                        
                        is.close();
                        fos.close();
                        
                        if ( i + 1 == file_names_.length ) {
                            Desktop.getDesktop().open(temp_file_);
                        }
                        
                    }
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                        

                
            } else if ( ae.getSource() == menu_item_version_info_ ) {
                str_help_message_ = "Steganography Encoder/Decoder\n" + 
                                 "Version = 1.0";
                
                str_help_title_ = "Version Info";
            }
    
            Validate_User_Input_and_Refresh();
    
            if ( bool_show_message == true ) {
                    JOptionPane.showMessageDialog( null, str_help_message_, str_help_title_, JOptionPane.INFORMATION_MESSAGE );
            }
        }
    }
    
    private class Steg_Document_Listener implements DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent ae ) {
        }

        @Override
        public void insertUpdate(DocumentEvent arg0) {
            // TODO Auto-generated method stub
            this.validate();
        }

        @Override
        public void removeUpdate(DocumentEvent arg0) {
            // TODO Auto-generated method stub
            this.validate();
        }
        
        private void validate() {
            Validate_Password();
            Refresh_Display_Options();
        }
    }
}
