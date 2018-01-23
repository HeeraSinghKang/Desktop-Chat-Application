package client;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;

class Client
  extends Thread
  implements ActionListener
{
  static Socket s;
  boolean running = true;
  JTextPane chatbox;
  StyledDocument doc;
  JTextField message;
  JButton b;
  JButton publicch;
  JButton privatech;

  JButton join;
  JButton create;
  JButton joingroup;
  JButton creategroup;
  JButton list;
  JFrame jf2;
  JFrame jf;
  JLabel l;
  JLabel gname;
  JLabel gpass;
  JTextField tf;
  JPasswordField tf2;
  String str;
  String flag;
  
  public static void main(String[] argc)
    throws InterruptedException, IOException, BadLocationException
  {
    Client obj = new Client();
    obj.GUI();
    obj.chatServer();
  }
  
  static String name()
  {
    String name = "";
    try
    {
      while (name.equals(""))
      {
        name = JOptionPane.showInputDialog("Enter your name", null);
        if (name.equals("")) {
          JOptionPane.showMessageDialog(null, "Enter a valid name");
        }
      }
      System.out.println(name);
    }
    catch (Exception e)
    {
      System.exit(0);
    }
    return name;
  }
  
  void GUI()
  {
    this.jf = new JFrame("Chat Client");
    this.jf.setSize(500, 500);
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    this.jf.setLocation(dim.width / 2 - this.jf.getSize().width / 2, dim.height / 2 - this.jf.getSize().height / 2);
    this.jf.setResizable(false);
    this.jf.setDefaultCloseOperation(3);
    this.jf.setLayout(new BorderLayout());
    
    JLabel label = new JLabel("CLIENT CHAT", 0);
    label.setFont(new Font("Courier", 1, 36));
    this.jf.add(label, "North");
    
    JPanel jp2 = new JPanel(new BorderLayout());
    
    this.chatbox = new JTextPane();
    this.doc = this.chatbox.getStyledDocument();
    this.chatbox.setEditable(false);
    JScrollPane scroll = new JScrollPane(this.chatbox);
    scroll.setVerticalScrollBarPolicy(22);
    
    jp2.add(scroll, "Center");
    this.jf.add(jp2, "Center");
    
    JPanel jp = new JPanel();
    jp.setLayout(new FlowLayout());
    
    this.message = new JTextField(30);
    jp.add(this.message);
    
    this.b = new JButton("SEND");
    jp.add(this.b);
    this.b.addActionListener(this);
    
    this.jf.getRootPane().setDefaultButton(this.b);
    
    this.jf.add(jp, "South");
    
    Border border = BorderFactory.createLineBorder(Color.BLACK);
    this.chatbox.setBorder(border);
  }
  
  void chatServer()
    throws BadLocationException, InterruptedException, IOException
  {
    try
    {
      s = new Socket("127.0.0.1", 64000);
    }
    catch (Exception e)
    {
      this.jf.setVisible(true);
      this.doc.insertString(this.doc.getLength(), "Server not connected.", null);
      this.doc.insertString(this.doc.getLength(), "\nExit and try again.", null);
      Thread.sleep(1000L);
      System.exit(0);
    }
    this.doc.insertString(this.doc.getLength(), "Connection established Successfully.", null);
    choice();
  }
  
  void starter()
    throws IOException, BadLocationException
  {
    this.jf.setVisible(true);
    PrintStream p = new PrintStream(s.getOutputStream());
    String name = name();
    p.println(name);
    this.doc.insertString(this.doc.getLength(), "\nStart Chatting : \n", null);
    this.read.start();
  }
  
  Thread read = new Thread()
  {
    public void run()
    {
      while (Client.this.running) {
        try
        {
          Scanner sc = new Scanner(Client.s.getInputStream());
          String str = sc.nextLine();
          if (str.indexOf(':') < 0)
          {
            if (str.indexOf("Connected.") >= 0)
            {
              SimpleAttributeSet keyword = new SimpleAttributeSet();
              StyleConstants.setForeground(keyword, Color.GREEN);
              Client.this.doc.insertString(Client.this.doc.getLength(), str + "\n", keyword);
            }
            else
            {
              SimpleAttributeSet keyword = new SimpleAttributeSet();
              StyleConstants.setForeground(keyword, Color.RED);
              Client.this.doc.insertString(Client.this.doc.getLength(), str + "\n", keyword);
            }
          }
          else {
            Client.this.doc.insertString(Client.this.doc.getLength(), str + "\n", null);
          }
          Client.this.chatbox.setCaretPosition(Client.this.chatbox.getDocument().getLength());
        }
        catch (Exception e)
        {
          Client.this.running = false;
          try
          {
            SimpleAttributeSet keyword = new SimpleAttributeSet();
            StyleConstants.setForeground(keyword, Color.RED);
            Client.this.doc.insertString(Client.this.doc.getLength(), "Server Disconnected.\n", keyword);
          }
          catch (BadLocationException e1)
          {
            e1.printStackTrace();
          }
          Client.this.chatbox.setCaretPosition(Client.this.chatbox.getDocument().getLength());
        }
      }
    }
  };
  
  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource() == this.b)
    {
      this.str = this.message.getText();
      try
      {
        PrintStream p = new PrintStream(s.getOutputStream());
        if (!this.str.equals(""))
        {
          p.println(this.str);
          SimpleAttributeSet keyword = new SimpleAttributeSet();
          StyleConstants.setForeground(keyword, Color.BLUE);
          this.doc.insertString(this.doc.getLength(), "You : " + this.str + "\n", keyword);
          this.chatbox.setCaretPosition(this.chatbox.getDocument().getLength());
        }
        this.message.setText("");
      }
      catch (Exception e1)
      {
        try
        {
          SimpleAttributeSet keyword = new SimpleAttributeSet();
          StyleConstants.setForeground(keyword, Color.RED);
          this.doc.insertString(this.doc.getLength(), "Server Disconnected.\n", keyword);
        }
        catch (BadLocationException e2)
        {
          e2.printStackTrace();
        }
        this.chatbox.setCaretPosition(this.chatbox.getDocument().getLength());
      }
    }
    if (e.getSource() == this.publicch)
    {
      this.jf2.setVisible(false);
      this.flag = "public";
      try
      {
        PrintStream p = new PrintStream(s.getOutputStream());
        p.println(this.flag);
        starter();
      }
      catch (Exception e1)
      {
        JOptionPane.showMessageDialog(null, "Server Disconnected !!");
        System.exit(0);
      }
    }
    if (e.getSource() == this.privatech)
    {
      this.flag = "private";
      this.jf2.setVisible(false);
      try
      {
        PrintStream p = new PrintStream(s.getOutputStream());
        p.println(this.flag);
        privateGUI();
      }
      catch (Exception e1)
      {
        JOptionPane.showMessageDialog(null, "Server Disconnected !!");
        System.exit(0);
      }
    }
    if (e.getSource() == this.join)
    {
      this.flag = "join";
      this.jf2.setVisible(false);
      try
      {
        PrintStream p = new PrintStream(s.getOutputStream());
        p.println(this.flag);
        joinGUI();
      }
      catch (Exception e1)
      {
        JOptionPane.showMessageDialog(null, "Server Disconnected !!");
        System.exit(0);
      }
    }
    if (e.getSource() == this.create)
    {
      this.flag = "create";
      this.jf2.setVisible(false);
      try
      {
        PrintStream p = new PrintStream(s.getOutputStream());
        p.println(this.flag);
        createGUI();
      }
      catch (Exception e1)
      {
        JOptionPane.showMessageDialog(null, "Server Disconnected !!");
        System.exit(0);
      }
    }
    if (e.getSource() == this.joingroup) {
      if ((this.tf.getText().equals("")) || (this.tf2.getPassword().length == 0)) {
        JOptionPane.showMessageDialog(null, "Fill all entries !!");
      } else {
        try
        {
          PrintStream p = new PrintStream(s.getOutputStream());
          Scanner sc = new Scanner(s.getInputStream());
          p.println(this.tf.getText());
          p.println(this.tf2.getText());
          int ms = sc.nextInt();
          if (ms == 0) {
            JOptionPane.showMessageDialog(null, "Incorrect Group name or Password !!");
          }
          if (ms == 1)
          {
            JOptionPane.showMessageDialog(null, "Group joined successfully");
            this.jf2.setVisible(false);
            starter();
          }
        }
        catch (Exception e1)
        {
          JOptionPane.showMessageDialog(null, "Server Disconnected !!");
          System.exit(0);
        }
      }
    }
    if (e.getSource() == this.creategroup) {
      if ((this.tf.getText().equals("")) || (this.tf2.getPassword().length == 0)) {
        JOptionPane.showMessageDialog(null, "Fill all entries !!");
      } else {
        try
        {
          PrintStream p = new PrintStream(s.getOutputStream());
          Scanner sc = new Scanner(s.getInputStream());
          p.println(this.tf.getText());
          p.println(this.tf2.getText());
          int ms = sc.nextInt();
          if (ms == 0)
          {
            JOptionPane.showMessageDialog(null, "Group name not available");
          }
          else
          {
            JOptionPane.showMessageDialog(null, "Group created successfully");
            this.jf2.setVisible(false);
            starter();
          }
        }
        catch (Exception e1)
        {
          JOptionPane.showMessageDialog(null, "Server Disconnected !!");
          System.exit(0);
        }
      }
    }
    
   
  }
  
  void choice()
  {
    this.jf2 = new JFrame("Chat Choice");
    this.jf2.setSize(300, 300);
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    this.jf2.setLocation(dim.width / 2 - this.jf2.getSize().width / 2, dim.height / 2 - this.jf2.getSize().height / 2);
    this.jf2.setResizable(false);
    this.jf2.setDefaultCloseOperation(3);
    this.jf2.setLayout(new GridLayout(4, 1));
    this.publicch = new JButton("PUBLIC CHAT");
    this.publicch.addActionListener(this);
    this.privatech = new JButton("PRIVATE CHAT");
    this.privatech.addActionListener(this);
    
    this.l = new JLabel("CLICK YOUR CHOICE", 0);
    this.l.setFont(new Font("Courier", 1, 20));
    
    this.jf2.add(this.l);
    this.jf2.add(this.publicch);
    this.jf2.add(this.privatech);
    this.jf2.setVisible(true);
  }
  
  void privateGUI()
  {
    this.create = new JButton("CREATE");
    this.create.addActionListener(this);
    this.join = new JButton("JOIN");
    this.join.addActionListener(this);
    this.jf2.remove(this.publicch);
    this.jf2.remove(this.privatech);
    this.jf2.add(this.create);
    this.jf2.add(this.join);
    this.jf2.setDefaultCloseOperation(3);
    this.jf2.setVisible(true);
  }
  
  void joinGUI()
  {
    this.jf2.remove(this.create);
    this.jf2.remove(this.join);
    this.jf2.remove(this.l);
    this.jf2.setLayout(new FlowLayout());
    this.jf2.setSize(300, 150);
    this.gname = new JLabel("Enter Group Name : ");
    this.gpass = new JLabel("Enter Group Key : ");
    this.tf = new JTextField(15);
    this.tf2 = new JPasswordField(15);
    this.joingroup = new JButton("Join Group");
    this.joingroup.addActionListener(this);
    this.jf2.add(this.gname);
    this.jf2.add(this.tf);
    this.jf2.add(this.gpass);
    this.jf2.add(this.tf2);
    this.jf2.add(this.joingroup);
    this.jf2.getRootPane().setDefaultButton(this.joingroup);
    this.jf2.setDefaultCloseOperation(3);
    this.jf2.setVisible(true);
  }
  
  void createGUI()
  {
    this.jf2.remove(this.create);
    this.jf2.remove(this.join);
    this.jf2.remove(this.l);
    this.jf2.setLayout(new FlowLayout());
    this.jf2.setSize(300, 150);
    this.gname = new JLabel("Enter Group Name : ");
    this.gpass = new JLabel("Enter Group Key : ");
    this.tf = new JTextField(15);
    this.tf2 = new JPasswordField(15);
    this.creategroup = new JButton("Create Group");
    this.creategroup.addActionListener(this);
    this.jf2.add(this.gname);
    this.jf2.add(this.tf);
    this.jf2.add(this.gpass);
    this.jf2.add(this.tf2);
    this.jf2.add(this.creategroup);
    this.jf2.getRootPane().setDefaultButton(this.creategroup);
    this.jf2.setDefaultCloseOperation(3);
    this.jf2.setVisible(true);
  }
 
}
