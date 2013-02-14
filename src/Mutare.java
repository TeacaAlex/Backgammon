import java.util.*;
public class Mutare implements Cloneable{

  public class CompAscend implements Comparator {
    public int compare(Object o1, Object o2) {
      Muta m1 = (Muta)o1;
      Muta m2 = (Muta)o2;
      if(m1.maiMic(m2) == true)
        return -1;
      else if(m1.equals(m2) == true)
        return 0;
      else
        return 1;
    }
  }

  public class CompDescend implements Comparator {
    public int compare(Object o1, Object o2) {
      Muta m1 = (Muta)o1;
      Muta m2 = (Muta)o2;
      if(m1.maiMic(m2) == true)
        return 1;
      else if(m1.equals(m2) == true)
        return 0;
      else
        return -1;
    }
  }

  public ArrayList moves;
  
  protected Object clone() throws CloneNotSupportedException{
    Mutare m = null;
    m = (Mutare) super.clone();
    m.moves = (ArrayList)moves.clone();
    return m;
  }

  public Mutare() {
    moves = new ArrayList();
  }
  
  public Mutare(int s1, int d1) {
    moves = new ArrayList();
    moves.add(new Muta(s1, d1, 0));
  }

  public Muta getOneMove(int n) {
    if(n < moves.size())
      return (Muta)moves.get(n);
    else
      return null;
  }

  public int getSizeMove() {
    return moves.size();
  }

  public void addMutare(Muta om) {
    moves.add(om);
  }
  
  public void sortCrescator() {
    Collections.sort(moves, new CompAscend());
  }

  public void sortDescrescator() {
    Collections.sort(moves, new CompDescend());
  }
}
