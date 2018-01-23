
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class MultiClientServer
  extends Thread
{
  public static ServerSocket ss;
  public static ArrayList<Socket> clients = new ArrayList<Socket>();
  public static ArrayList<MultiClientServer> names=new ArrayList<MultiClientServer>();
  Socket s;
  String name;
  static int tempid = 0;
  int id;
  Object obj3;
  boolean running = true;
  
  MultiClientServer(int id2)
  {
    this.id = id2;
  }
  
  public static void broadcast(String message, Socket s2, String name)
  {
    try
    {
      for (Socket socket : clients) {
        if (socket != s2)
        {
          PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
          out.println(name + " : " + message);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void broadcast2(String message, Socket s2, String name)
  {
    try
    {
      for (Socket socket : clients) {
        if (socket != s2)
        {
          PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
          out.println(message);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args)
    throws Exception
  {
    ss = new ServerSocket(64000);
    for (;;)
    {
      tempid += 1;
      MultiClientServer obj = new MultiClientServer(tempid);
      obj.s = ss.accept();
      obj.AddObj(obj);
      obj.start();
    }
  }
  
  void AddObj(MultiClientServer obj2)
  {
	  obj3=obj2;
  }
  
  public void run()
  {
    try
    {
      Scanner sc = new Scanner(this.s.getInputStream());
      PrintStream p=new PrintStream(this.s.getOutputStream());
      String flag = sc.nextLine();
      if (flag.equals("public"))
      {
          this.name = sc.nextLine();
          names.add((MultiClientServer) obj3);
        clients.add(this.s);
        System.out.println("Client " + this.id + " Connected.");
        broadcast2(this.name + " Connected.", this.s, this.name);
        while (this.running)
        {
          String clientCommand = sc.nextLine();
          broadcast(clientCommand, this.s, this.name);
        }
      }
      if (flag.equals("private"))
      {
        PrivateChat obj = new PrivateChat(ss, this.s);
        obj.object(obj);
        obj.start();
      }
      if (flag.equals("one"))
      {
    	  for (int i=0;i<names.size();i++)
    	  {
    		  p.println(names.get(i).name);
    	  }
    	  p.println("stop");
      }
    }
    catch (Exception e)
    {
      this.running = false;
      if(this.name.equals(null))
      {
    	  
      }else
      {
    	  System.out.println("Client " + this.id + " Disonnected.");
      broadcast2(this.name + " Disconnected.", this.s, this.name);
      }
    }
  }
}


class PrivateChat
extends Thread
{
public static ServerSocket ss;
PrivateChat obj;
Socket s;
String name;
String gname;
String gkey;
boolean running = true;
public static ArrayList<PrivateChat> groups = new ArrayList<PrivateChat>();
public static ArrayList<Socket> clients = new ArrayList<Socket>();

public PrivateChat(ServerSocket ss2, Socket s2)
{
  ss = ss2;
  this.s = s2;
}

public void object(PrivateChat obj2)
{
  this.obj = obj2;
}

public void run()
{
  try
  {
    PrintStream p = new PrintStream(this.s.getOutputStream());
    Scanner sc = new Scanner(this.s.getInputStream());
    String flag = sc.nextLine();
    if (flag.equals("create"))
    {
      int flag2 = 0;
      while (flag2 == 0)
      {
        this.gname = sc.nextLine();
        this.gkey = sc.nextLine();
        flag2 = search(this.obj);
        p.println(flag2);
        if (flag2 == 1)
        {
          groups.add(this.obj);
          clients.add(this.s);
        }
      }
      this.name = sc.nextLine();
      broadcast2(this.name + " Connected.", this.s, this.name, this.obj);
      while (this.running)
      {
        String clientCommand = sc.nextLine();
        broadcast(clientCommand, this.s, this.name, this.obj);
      }
    }
    if (flag.equals("join"))
    {
      int flag2 = 0;
      while (flag2 == 0)
      {
        this.gname = sc.nextLine();
        this.gkey = sc.nextLine();
        flag2 = search2(this.obj);
        p.println(flag2);
        if (flag2 == 1)
        {
          groups.add(this.obj);
          clients.add(this.s);
        }
      }
      this.name = sc.nextLine();
      broadcast2(this.name + " Connected.", this.s, this.name, this.obj);
      while (this.running)
      {
        String clientCommand = sc.nextLine();
        broadcast(clientCommand, this.s, this.name, this.obj);
      }
    }
  }
  catch (Exception e)
  {
    if(this.name.equals(null))
    {
    	
    }else{
    broadcast2(this.name + " Disconnected.", this.s, this.name, this.obj);
    }
   } 
}

public static void broadcast(String message, Socket s2, String name, PrivateChat obj)
{
  try
  {
    for (int i = 0; i < groups.size(); i++) {
      if ((((PrivateChat)groups.get(i)).gname.equals(obj.gname)) && (((PrivateChat)groups.get(i)).s != s2))
      {
        PrintWriter out = new PrintWriter(((PrivateChat)groups.get(i)).s.getOutputStream(), true);
        out.println(name + " : " + message);
      }
    }
  }
  catch (Exception e)
  {
    e.printStackTrace();
  }
}

public static void broadcast2(String message, Socket s2, String name, PrivateChat obj)
{
  try
  {
    for (int i = 0; i < groups.size(); i++) {
      if ((((PrivateChat)groups.get(i)).gname.equals(obj.gname)) && (((PrivateChat)groups.get(i)).s != s2))
      {
        PrintWriter out = new PrintWriter(((PrivateChat)groups.get(i)).s.getOutputStream(), true);
        out.println(message);
      }
    }
  }
  catch (Exception e)
  {
    e.printStackTrace();
  }
}

public static int search(PrivateChat obj)
{
  int flag = 1;
  try
  {
    for (int i = 0; i < groups.size(); i++) {
      if (((PrivateChat)groups.get(i)).gname.equals(obj.gname))
      {
        flag = 0;
        break;
      }
    }
  }
  catch (Exception e)
  {
    e.printStackTrace();
  }
  return flag;
}

public static int search2(PrivateChat obj)
{
  int flag = 0;
  try
  {
    for (int i = 0; i < groups.size(); i++) {
      if (((PrivateChat)groups.get(i)).gname.equals(obj.gname)) {
        if (((PrivateChat)groups.get(i)).gkey.equals(obj.gkey))
        {
          flag = 1;
          break;
        }
      }
    }
  }
  catch (Exception e)
  {
    e.printStackTrace();
  }
  return flag;
}
}

