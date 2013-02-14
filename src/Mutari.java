import java.util.*;

public class Mutari {
  private ArrayList moves;
  
  public Mutari() {
    moves = new ArrayList();
  }
  
  public int getSize() {
    return moves.size();
  }

  public Mutare getMove(int n) {
    if(n<moves.size())
      return (Mutare)moves.get(n);
    else
      return null;
  }

  public void  add(Mutare m) {
    moves.add(m);
  }

  public void addSet(Mutari mset) {
    moves.addAll(mset.moves);
  }

  public void getUnique() {
    for(int i = 0 ; i < moves.size() ; i ++) {
      for(int j = moves.size() -1 ; j > i ; j --) {
        if(((Mutare)moves.get(i)).equals(moves.get(j))) {
          moves.remove(j);
        }
      }
    }
  }

  public void getMax() {
    int max_dice = 0;
    for(int i = 0 ; i < moves.size() ; i ++) {
      int this_move_d = ((Mutare)moves.get(i)).getSizeMove();
      if(this_move_d > max_dice)
        max_dice = this_move_d;
    }
    for(int i = moves.size() - 1 ; i >= 0 ; i --) {
      int this_move_d = ((Mutare)moves.get(i)).getSizeMove();
      if(this_move_d < max_dice)
        moves.remove(i);
    }
  }

  public void getBig(int dc1, int dc2) {
    if(moves.size() > 0) {
      if( ((Mutare)moves.get(0)).getSizeMove() == 1) {
        int big_die = 0;
        for(int i = 0 ; i < moves.size() ; i ++) {
          Muta om = ((Mutare)moves.get(i)).getOneMove(0);
          int this_die = Math.abs(om.sursa - om.dest);
          if(this_die > big_die) {
            big_die = this_die;
          }
        }
        for(int i = moves.size() -1; i >= 0 ; i --) {
          Muta om = ((Mutare)moves.get(i)).getOneMove(0);
          int this_die = Math.abs(om.sursa - om.dest);
          if(this_die < big_die) {
            moves.remove(i);
          }
        }
      }
    }
  }
}
