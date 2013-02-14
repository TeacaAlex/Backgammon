public class Evaluare {

  protected float MAX_SCORE;
  protected float MIN_SCORE;

  public Evaluare() {
    MAX_SCORE = 20*24;
    MIN_SCORE = -20*24;
  }

  public float getMAX_SCORE() {
    return MAX_SCORE;
  }

  public float getMIN_SCORE() {
    return MIN_SCORE;
  }

  public float boardScore(Tabla b, int player) {//scorul tablei din punctul de vedere al player
    int tempEval = 0;
    int myEval = evaluate(b, player);//scor pozitional al meu
    int opEval = evaluate(b, 1-player);//scor pozitional al adversar
    tempEval = tempEval + myEval + (myEval - opEval);
    tempEval = tempEval -b.bar[player] + 3*b.bar[1-player];//la care adaug piesele de pe bara ale
                                                    //adeversarului si scad pe ale mele
    if(inMyHome(b, player) == true)//daca adversarul se afla in casa mea
      tempEval = tempEval + 3*ports(b, player);//joc acoperit
    tempEval = tempEval - 3*gauri(b,player);//penalizez pentru piesele fara acoperire
    if(!inMyHome(b, player))//daca nu mai este in casa mea
      tempEval = tempEval + 24*b.home[player];//scot cat mai multe
    tempEval = tempEval - 24*b.home[1-player];//scad piesele pe care le scoate adversarul
    return (float)tempEval;
  }

  /* anunta daca adversarul este in casa lui player */

  public boolean inMyHome(Tabla b, int player) {
    if(player == 1) {
      for(int i = 18 ; i < 24 ; i ++)
        if(b.board[i] < 0)
          return true;
    }
    else {
      for(int i = 0 ; i < 6 ; i ++)
        if(b.board[i] > 0)
          return true;
    }
    return false;
  }

  /* numarul de porti ale player-ului din casa lui */
  private int ports(Tabla b, int player) {
    int ports = 0;
    if(player == 1) {
      for(int i = 16 ; i < 24 ; i ++)
         if(b.board[i] == 2 || b.board[i] == 3)
           ports++;
    }
    else {
      for(int i = 0 ; i < 8 ; i ++)
        if(b.board[i] == -2 || b.board[i] == -3)
          ports++;
    }
    return ports;
  }

  /* numarul de piese neacoperite */
  private int gauri(Tabla b, int player) {
    int number = 0;
    if(player == 1) {
      for(int i = 16 ; i < 24 ; i++) {
        if(b.board[i] == 1)
          number ++;
      }
    }
    else {
      for(int i = 0 ; i < 8 ; i++) {
        if(b.board[i] == -1)
          number ++;
      }
    }
    return number;
  }

  /*evalueaza tabla din punct de vedere al pozitiei pulurilor fata de casa */
  public int evaluate(Tabla b, int player) {
    int value = 0;
    if(player == 0)
      for(int i = 0 ; i < 24 ; i ++) {
          if(b.board[i] < 0)
            value = value + b.board[i]*(i-23);
      }
    else
      for(int i = 0 ; i < 24 ; i ++) {
        if(b.board[i] > 0)
          value = value + b.board[i]*(23-i);
      }
    return value;
  }
}
