
import javax.swing.*;
import javax.swing.border.*;  // for empty border 143 line number.
import java.awt.*;
import java.awt.event.*;  // for ActionListner
import java.util.*;
import java.text.*;
import java.net.*;
import java.io.*;

public class Server implements ActionListener
{


    JTextField text1;
    JPanel p2 ;
     static Box vertical = Box.createVerticalBox();
     static JFrame mainframe = new JFrame();
     static DataOutputStream dout ;

    Server()
    {
        mainframe.setLayout(null);
        
        JPanel p1 = new JPanel();   //Name panel.
        p1.setBackground(new Color(7,94,84));
        p1.setBounds(0,0,450 ,70);
        p1.setLayout(null);
        mainframe.add(p1);


    
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/3.png"));
        Image i2 = i1.getImage().getScaledInstance(25,25,Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel lobj = new JLabel(i3);
        lobj.setBounds(5,20,25,25);  
        p1.add(lobj);

          lobj.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent obj)
            {
                System.exit(0);
            }
        });


        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("icons/ServerLogo.jpeg"));
        Image i5 = i4.getImage().getScaledInstance(50,50,Image.SCALE_DEFAULT);
        ImageIcon i6 = new ImageIcon(i5);
        JLabel Profile = new JLabel(i6);
        Profile.setBounds(40,10,50,50);  
        p1.add(Profile);

        ImageIcon i7 = new ImageIcon(ClassLoader.getSystemResource("icons/video.png"));
        Image i8 = i7.getImage().getScaledInstance(30,30,Image.SCALE_DEFAULT);
        ImageIcon i9 = new ImageIcon(i8);
        JLabel Video = new JLabel(i9);
        Video.setBounds(300,20,30,30);  
        p1.add(Video);


        ImageIcon i10 = new ImageIcon(ClassLoader.getSystemResource("icons/phone.png"));
        Image i11 = i10.getImage().getScaledInstance(35,30,Image.SCALE_DEFAULT);
        ImageIcon i12 = new ImageIcon(i11);
        JLabel Phone = new JLabel(i12);
        Phone.setBounds(360,20,35,30);  
        p1.add(Phone);


        ImageIcon i13 = new ImageIcon(ClassLoader.getSystemResource("icons/3icon.png"));
        Image i14 = i13.getImage().getScaledInstance(30,30,Image.SCALE_DEFAULT);
        ImageIcon i15 = new ImageIcon(i14);
        JLabel morevert= new JLabel(i15);
        morevert.setBounds(420,23,5,25);  
        p1.add(morevert);

        JLabel name = new JLabel("Server");
        name.setBounds(110,15,100,18);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN_SERIF", Font.BOLD,18)); 
        p1.add(name); 

        JLabel status = new JLabel("Active Now");
        status.setBounds(110,35,100,18);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SAN_SERIF", Font.BOLD,14)); 
        p1.add(status); 

        p2 = new JPanel();   //Actual frame 
        p2.setBounds(5 ,75,440,570);
        mainframe.add(p2);

        text1 = new JTextField();
        text1.setBounds(6,655,310,40);
        text1.setFont(new Font("SAN_SERIF",Font.PLAIN, 16));
        mainframe.add(text1);

        JButton send = new JButton("Send");
        send.setBounds(320,655,123,40);
        send.setBackground(new Color(7,94,84));
        send.setForeground(Color.WHITE);
        send.addActionListener(this);
        send.setFont(new Font("SAN_SERIF",Font.PLAIN, 16));
        mainframe.add(send);


        
        mainframe.setSize(450, 700);
        mainframe.setLocation(200, 50 );
        mainframe.setUndecorated(true);
        mainframe.getContentPane().setBackground(Color.WHITE);

        mainframe.setVisible(true);
        
    }
    
    public void actionPerformed(ActionEvent obj)   //abstrct method of ActionListener is over-ridden
    {
        try
        {

            String out = text1.getText();

            JPanel p3 = formatLable(out);

            p2.setLayout(new BorderLayout());  // actual panel frame.

            JPanel right = new JPanel(new BorderLayout());  // messege right side align
            right.add(p3 , BorderLayout.LINE_END);
            vertical.add(right);   // vertical allign of messenge .
            vertical.add(Box.createVerticalStrut(15));  // 15 is space between two messenges .

            p2.add(vertical, BorderLayout.PAGE_START);

            dout.writeUTF(out);    // to send messege to the client 
            text1.setText("");

            mainframe.repaint();
            mainframe.validate();
            mainframe.invalidate();

        }
        catch(Exception e)
        {
            e.printStackTrace(); 
        }
    }

    public static JPanel formatLable(String out)  // here JPanel is return type 
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

       // JLabel output = new JLabel("<html><p style =\"width : 150px\">" + out+ "</p></html");
        JLabel output = new JLabel(out);
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(37,211,102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15,15,15,50));

        panel.add(output);

        Calendar cal = Calendar.getInstance();  // for time .
        SimpleDateFormat sdf = new SimpleDateFormat("HH: mm");
        
        JLabel time = new JLabel();
        time.setText(sdf.format(cal.getTime()));
        panel.add(time);

        return panel;

    }

    public static void main(String arr[])
{
        Server s = new Server();   //class object 

         try
        {  
            ServerSocket ss = new ServerSocket(2100);
    
            while(true)
            {
                Socket Socket = ss.accept();
                DataInputStream din = new DataInputStream(Socket.getInputStream());
                dout = new DataOutputStream(Socket.getOutputStream());

                while(true)
                {
                    String msg = din.readUTF();
                    JPanel panel = formatLable(msg);

                    JPanel  left =  new JPanel( new BorderLayout());
                    left.add(panel, BorderLayout.LINE_START);
                    vertical.add(left); // to display msg one by one.
                    mainframe.validate(); // for color 
                }
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();   
        }

    }
}