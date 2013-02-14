import java.util.*;
import java.io.*;
import java.math.*;

public class Tabla implements Cloneable{
  public int board[];//memorez tabla de joc
  public int bar[];
  public int home[];
  public int dice1;
  public int dice2;
  public static int CHECKERS = 15;
  public static int POINTS = 24;
  public static int LOC0 = 24;
  public static int LOC1 = -1;

  public Tabla() {
    board = new int[POINTS];
    bar = new int[2];
    home = new int[2];
    for(int i = 0 ; i < POINTS ; i ++)
      board[i] = 0;
    board[5] = -5;
    board[7] = -3;
    board[12] = -5;
    board[23] = -2;
    board[0] = 2;
    board[11] = 5;
    board[16] = 3;
    board[18] = 5;

    bar[0] = 0;
    bar[1] = 0;

    home[0] = 0;
    home[1] = 0;
  }

  protected Object clone() throws CloneNotSupportedException {
    Tabla b = null;
    b = (Tabla)super.clone();
    b.board = (int[])board.clone();
    b.bar = (int [])bar.clone();
    b.home = (int [])home.clone();
    return b;
  }

  /* aplica o un set de mutari ale jucatorului player */
  public void aplicaMutare(int player, Mutare m) {
    if(m != null) {
      if(player == 0)
        m.sortDescrescator();//sorteaza crescator mutarile
      else
        m.sortCrescator();//sorteaza descrescator mutarile
      for(int i = 0 ; i < m.getSizeMove() ; i ++) {
        Muta om = (Muta)m.moves.get(i);
        muta(player, om);//muta mutarile
      }
    }
  }


  /* muta o mutare a jucatorului player */
  public void muta(int player, Muta om) {
    if( !( om.sursa < -1 || om.sursa > 24)) {
      if(om.sursa == LOC0 || om.sursa == LOC1) {//muta de pe bara
        if(!(bar[player] < 1))
          bar[player] --;//scoate de pe bara un pul
      }
      else {
        int p_type = pType(player);
        if( p_type * board[om.sursa] >= p_type)
          board[om.sursa] -= p_type;
      }//ia un pull de la sursa
    }
    //si il pune la destinatie
    if(!(om.dest < -1 || om.dest > 24)) {
      int playerT = pType(player);
      if( om.dest <= -1 || om.dest >= 24)
        home[player] ++;//scoate puluri pe margine
      else if(playerT*board[om.dest] >= 0)
        board[om.dest] += playerT;//muta la destinatie
      else if(board[om.dest] == -playerT) {
        board[om.dest] = playerT;
        bar[(player+1)%2]++;//scoate adversar
      }
    }
  }

  public int pType(int player) {
    if(player == 0)
      return -1;
    else
      return 1;
  }

  /* intaorce toate mutarile disponibile unui jucator */
  public Mutari getValidMoves(int player) throws CloneNotSupportedException{
    Mutare move = new Mutare();
    Mutari mset = null;
    int []dice;
    int left;
    if( dice1 == dice2) { //daca e dubla
      dice = new int[4];
      left = 4;
      dice[0] = dice1;
      dice[1] = dice1;
      dice[2] = dice1;
      dice[3] = dice1;
      int s;
      if(player == 1)
        s = 0;
      else
        s = 23;


      mset = getMovesDoubles(move, player, left, dice, s);//intoarce mutarile asociate unei duble
      if(mset != null)
        mset.getMax();
    }
    else {//zar simplu
      left = 2;
      dice  = new int[2];
      dice[0] = dice1;
      dice[1] = dice2;
      mset = getMoves(move, player, left, dice);//intoarce mutarile asociate unui zar simplu
      dice[0] = dice2;
      dice[1] = dice1;
      mset.addSet(getMoves(move, player, left, dice));//si invers
      if(mset != null) {
        mset.getMax();
        mset.getBig(dice1, dice2);
      }
    }
    if(mset != null) {
      mset.getUnique();//las doar mutarile distincte
      if(player == 0) {
        for(int i = 0 ; i < mset.getSize() ; i++)
          ((Mutare)mset.getMove(i)).sortDescrescator();//sorteaza descrescator pentru player 0
      }
      else {
        for(int i = 0 ; i < mset.getSize() ; i++)
          ((Mutare)mset.getMove(i)).sortCrescator();//sorteaza crescator pentru player 1
      }
    }
    return mset;
  }

  /* returneaza mutarile disponibile unui jucator in cazul in care da o dubla */
  private Mutari getMovesDoubles(Mutare current_move, int player, int left, int [] dice, int s)
                                                                  throws CloneNotSupportedException{
    Mutari mset = new Mutari();
    int p_type = pType(player);
    if(left == 0) //daca am terminat toate zarurile
      mset.add(current_move);
    else {//mai am zaruri
      int the_die = dice[left-1];
      boolean moved = false;
      for(int b = s ; b < 24 && b >= 0 ; b = b + p_type) {
        if( board[b]*p_type > 0 && eValida(b, the_die, player)) {//daca mutarea e valida
          moved = true;
          Mutare rec_move = (Mutare)current_move.clone();//clonez mutarea
          int to_loc = b + p_type*the_die;//destinatie
          if(to_loc >24)
            to_loc = 24;
          else if(to_loc < -1)
            to_loc = -1;
          Muta om = new Muta(b, to_loc, the_die);//mutare noua
          rec_move.addMutare(om);//o adaug
          Tabla rec_board = (Tabla)this.clone();//la tabla noua
          rec_board.muta(player, om);
          Mutari rec_mset = rec_board.getMovesDoubles(rec_move, player, left-1, dice, b);//si fac la
                                                                        //fel pentru restul de zaruri
          mset.addSet(rec_mset);
        }
      }
      int bar_loc = player==0 ? LOC0 : LOC1;
      if( bar[player] > 0 && eValida(bar_loc, the_die, player) ) {//scot dupa bara
        moved = true;
        Mutare rec_move = (Mutare)current_move.clone();
        Muta om = new Muta(bar_loc, bar_loc + p_type*the_die, the_die);
        rec_move.addMutare(om);
        Tabla rec_board = (Tabla)this.clone();
        rec_board.muta(player, om);
        Mutari rec_mset = rec_board.getMovesDoubles(rec_move, player, left-1, dice, s);
        mset.addSet(rec_mset);
      }
      if(!moved && current_move.getSizeMove() >0)
        mset.add(current_move);
    }
    return mset;
  }

  public Mutari getMoves(Mutare current_move, int player, int dice_left, int []dice) 
                                                    throws CloneNotSupportedException{
    Mutari mset = new Mutari();
    int p_type = pType(player);
    if(dice_left == 0 ) {
      mset.add(current_move);
    }
    else {
      int the_die = dice[dice_left - 1];
      boolean moved = false;
      if(player == 1) {
        for(int b = 0 ; b < 24 ; b ++) {
          if( board[b]*p_type > 0 && eValida(b, the_die, player)) {
            moved = true;
            Mutare rec_move = (Mutare) current_move.clone();
            int to_loc = b + p_type*the_die;
            if(to_loc > 24)
              to_loc = 24;
            else if(to_loc < -1)
              to_loc = -1;
            Muta om = new Muta(b, to_loc, the_die);
            rec_move.addMutare(om);
            Tabla rec_board = (Tabla)this.clone();
            rec_board.muta(player, om);
            Mutari rec_mset = rec_board.getMoves(rec_move, player, dice_left-1, dice);
            mset.addSet(rec_mset);
          }
        }
      }
      else {
        for(int b = 23 ; b >=0 ; b --) {
          if( board[b]*p_type > 0 && eValida(b, the_die, player)) {
            moved = true;
            Mutare rec_move = (Mutare) current_move.clone();
            int to_loc = b + p_type*the_die;
            if(to_loc > 24)
              to_loc = 24;
            else if(to_loc < -1)
              to_loc = -1;
            Muta om = new Muta(b, to_loc, the_die);
            rec_move.addMutare(om);
            Tabla rec_board = (Tabla)this.clone();
            rec_board.muta(player, om);
            Mutari rec_mset = rec_board.getMoves(rec_move, player, dice_left-1, dice);
            mset.addSet(rec_mset);
          }
        }
      }

      int bar_loc = player == 0?LOC0 : LOC1;
      if(bar[player] > 0 && eValida(bar_loc, the_die, player)) {
        moved = true;
        Mutare rec_move = (Mutare)current_move.clone();
        Muta om = new Muta(bar_loc, bar_loc + p_type*the_die, the_die);
        rec_move.addMutare(om);
        Tabla rec_board = (Tabla)this.clone();
        rec_board.muta(player, om);
        Mutari rec_mset = rec_board.getMoves(rec_move, player, dice_left-1, dice);
        mset.addSet(rec_mset);
      }
      if(!moved ) {
        if(current_move.getSizeMove()!=0) {
          mset.add(current_move);
        }
      }
    }
    return mset;
  }

  /* anunta daca player a castigat */
  public boolean aCastigat(int player) {
    return home[player] == 24;
  }

  /* anunta daca player poate sa inceapa sa scoata piese */
  public boolean potScoate(int player) {
    boolean pot = true;
    if(player == 0) {
      for(int loc = 6 ; loc < 24 ; loc ++)//daca mai am piese in afara casei = false
        if(board[loc] < 0)
          pot = false;
    }
    else {
      for(int loc = 0 ; loc < 24 -6 ; loc ++) 
        if(board[loc] > 0)
          pot = false;
    }
    return pot;
  }

  /*anunta daca o mutare este valida */
  public boolean eValida(int space, int steps, int player) {
    boolean is_valid = true;
    int p_type = pType(player);
    if(bar[player] > 0 && ((space != LOC0 && player == 0) || (space != LOC1 && player == 1))) {
      return false;//daca player mai are piese pe bara
    }
    int move_to = space + steps*p_type;
    if(move_to == -1 || move_to == 24)//scot afata piese
      is_valid = is_valid & potScoate(player);//doar daca pot scoate piese
    else if(move_to < -1 || move_to > 24) {
      boolean valid = true;
      if(player == 0) {
        for(int i = space+1 ; i <= 5 ; i ++)
          if(board[i] < 0)
            valid = false;
      }
      else {
        for(int i = 18 ; i < space ; i ++)
          if(board[i] > 0)
            valid = false;
      }
      is_valid = is_valid & potScoate(player);
      is_valid = is_valid & valid;
    }
    else {//daca adversarul are mai mult de 2 piese
      if(board[move_to]*(-p_type) >= 2)//nu am voie sa mut
        is_valid = false;
    }
    return is_valid;
  }
}
