import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientStub {
  public static byte[] readMessage(DataInputStream in) {
    byte[] message = null;
    try {
      byte size = in.readByte();
      message = new byte[size];
      in.readFully(message);
    } catch (EOFException e) {
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return message;
  }

  public static void sendMessage(final byte[] message, DataOutputStream out) {
    byte size = (byte) message.length;
    try {
      out.writeByte(size);
      out.write(message);
    } catch (SocketException e) {
      return;
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
  }

  public static void main(String args[]) {
    if (args.length < 4) {
      System.out.println("Usage: ./client server_hostname server_port opponent_level(1=dumb, 5, 7, 8) own_level(1=dumb, 5, 7, 8)");
      return;
    }
    Socket socket = null;
    DataOutputStream out = null;
    DataInputStream in = null;
    try {
      socket = new Socket(args[0], Integer.parseInt(args[1]));
      out = new DataOutputStream(socket.getOutputStream());
      in = new DataInputStream(socket.getInputStream());
      byte[] message = new byte[1];
      message[0] = Byte.parseByte(args[2]);
      ClientStub.sendMessage(message, out);
      int player = 2;
      Tabla b = new Tabla();
      message = ClientStub.readMessage(in);
      if (message[0] == 1) {
        player = 1;
      } else if (message[0] == 0) {
        player = 0;
      } else {
      }
      AlphaBeta alphabeta = new AlphaBeta(player);  //decide ce muta
      int p_type = b.pType(player);
      int o_type = b.pType((player+1)%2);
      while (true) {
        message = ClientStub.readMessage(in);
        if(message[0] == 76) {
          System.out.println("am pierdut\n");
          break;
        }
        if(message[0] == 87) {
          System.out.println("am castigat\n");
          break;
        }
        int k = message.length;//dimensiune mesaj
        b.dice1 = message[k-2];//zar1
        b.dice2 = message[k-1];//zar2
        for(int i = 0 ; i < k - 2 ; i=i+2) {
          int source = message[i]-1;
          int dest;
          if(source == 29) {//mutare dupa bara
            source =  (player+1)%2 == 0?b.LOC0 : b.LOC1;
            dest = source + o_type*message[i+1] ;
          }
          else
            dest = source + o_type*message[i+1];
          b.muta((player+1)%2, new Muta(source, dest, 0));//actualizez tabla
        }
        Mutare m = alphabeta.chooseMove(b);//aleg o mutare cu AlphaBeta
        if(m != null) {
          int nr = m.getSizeMove();
          byte[] yourResponse = new byte[2*nr];
          int j = 0;
          b.aplicaMutare(player, m);//actualizez tabla
          for(int i = 0 ; i < nr ; i ++) {
            int source = m.getOneMove(i).sursa+1;
            int dest = m.getOneMove(i).zar;
            if(source == 25 || source == 0)//mut dupa tabla
              source = 30;
            yourResponse[j] = (byte)source;
            yourResponse[j+1] = (byte)dest;
            j = j +2;
          }
          ClientStub.sendMessage(yourResponse, out);//trimit mesaj cu mutari
        }
        else {//daca nu am mutari
          byte[] yourResponse  = new byte[0];//trimit nimic
          ClientStub.sendMessage(yourResponse, out);
        }
      }
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }
}
