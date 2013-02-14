public class AlphaBeta {
  
  public class Data {
    private int index;
    private float value;

    public Data() {
    }

    public Data(int index, float value) {
      this.index = index;
      this.value = value;
    }

    public Data(Data d) {
      this.index = d.index;
      this.value = d.value;
    }

    public int getIndex() {
      return index;
    }

    public float getValue() {
      return value;
    }

    public void setTo(int index, float value) {
      this.index = index;
      this.value = value;
    }

    public void setTo(Data d) {
      this.index = d.index;
      this.value = d.value;
    }
  }

  private Evaluare evaluation ;
  private int oponent;
  private int player;
  final int depth = 3;//nivelul maxim in arborele de simulari
  final int MAXNODE = 0;
  final int MINCHANCENODE = 1;
  final int MINNODE = 2;
  final int MAXCHANCENODE = 3;
  final Float p1 = (new Float(2.0F/(new Float(6*6))));//probabilitate unui zar simplu
  final Float p2 = (new Float(1.0F/(new Float(6*6))));//probabilitatea unei duble

  public AlphaBeta(int my_player_num ) {
    this.player = my_player_num;
    oponent = 1 - my_player_num;
    evaluation = new  Evaluare();//functia de evaluarea a tablei
  }

  /* alege cea mai buna mutare folosinf AlphaBetaPruning */
  public Mutare chooseMove(Tabla b) throws CloneNotSupportedException {
    Mutari ms= b.getValidMoves(player);//din toate mutarile valide
    int index = alphabeta(b, depth);//muta ce returneaza AlphaBeta
    if(index == -1)
      return ms.getMove(0);
    Mutare m = ms.getMove(index);
    return m;
  }

  /* alphabeta pe tabla b si nivelul depth */
  private int alphabeta(Tabla b, int depth) throws CloneNotSupportedException {
    Data d = new Data(alphabetanode(b, new Float(evaluation.MIN_SCORE), new Float(evaluation.MAX_SCORE), depth, MAXNODE, -1));
    return d.getIndex();
  }

  /* intoarce ce alege max pe tabla b */
  private Data alphabetanode(Tabla b, Float alpha, Float beta, int depth, int nodeType, int index)
                                                              throws CloneNotSupportedException{
    if( (depth == 0) || (b.aCastigat(player))) {  //daca am ajuns in nod frunza
      Data rVal = new Data(index, evaluation.boardScore(b, player));//intorc scorul tablei
      return rVal;
    }
  
    if(nodeType == MAXCHANCENODE) {//nod sansa pentru max
      int k = 1;
      float v = 0f;//valoarea totala
      float p = 1f;//probabilitatea
      Tabla newBoard;//pentru o tabla noua
      for(int i = 1 ; i <= 6 ;i ++) {//generez toate posibilitatile de a da cu zarul
        for(int j = i ; j <= 6 ; j ++) {
          if(i!=j) {//zaruri diferite
            newBoard = (Tabla)b.clone();//clonez tabla
            newBoard.dice1 = i;
            newBoard.dice2 = j;
            v = v + alphabetanode(newBoard, alpha/p1, beta/p1, depth-1, MAXNODE, k-1).getValue()*p1;//ce alege max pentru fiecare zar
            p = p - p1;
            if(p*evaluation.MIN_SCORE >= alpha - v) {//reducere pruning
              return new Data(-1, (v+0.0001f));//intorc v plus o valoare foarte mica
            }
            k++;
          }
        }
        //dubla
        newBoard = (Tabla)b.clone();
        newBoard.dice1 = i;
        newBoard.dice2 = i;
        v = v +alphabetanode(newBoard, alpha/p2, beta/p2, depth-1, MAXNODE, k-1).getValue()*p2;//ce alege max pentru fiecare dubla
        p = p -p2;
        //reducere pruning
        if((p*evaluation.MIN_SCORE >= alpha - v) && !(i == 6)) {
          return new Data(-1, (v + new Float(0.0001f)));//intorc v plus o valoare foarte mica
        }
        k++;
      }
      Data rval = new Data(-1, v);
      return rval;
    }
    else if(nodeType == MAXNODE) {//MAXNODE
      Mutari ms = b.getValidMoves(this.player);//toate mutarile valide pentru un zar
      Data v = new Data(-1, evaluation.MIN_SCORE);
      Tabla newBoard;
      Data data;
      Data tmp;
      for(int i = 0 ; i < ms.getSize() ; i ++) {
        newBoard = (Tabla)b.clone();//clonez tabla
        newBoard.aplicaMutare(player, ms.getMove(i));//aplica mutarea
        if(newBoard.aCastigat(player))//daca s-a terminat simularea
          v.setTo(new Data(i, evaluation.boardScore(b, player)));//intorc scorul tablei cu mutarea i
        else {
          tmp = alphabetanode(newBoard, alpha, beta, depth-1, MINCHANCENODE, i);//aleg maximul din ce imi pune la dispozitie min
          if(tmp.getValue() > v.getValue())
            v.setTo(i, tmp.getValue());
          else
            v.setTo(v.getIndex(), v.getValue());
        }
        //reducere pruning
        if(v.getValue()>=beta) {
          v.setTo(i, v.getValue());
          return v;
        }
        alpha = max(alpha, v);
      }
      return v;
    }
    else if(nodeType == MINCHANCENODE) {//nod sansa pentru min
      int k = 1;
      Tabla newBoard;//tabla noua
      float v = 0f;
      float p = 1f;
      for(int i = 1; i <=6  ; i ++) {//pentru toate posibilitatile de a da cu zarul
        for(int j = i ; j <=6 ;j++) {
          if(i!=j) {//zar simplu
            newBoard = (Tabla)b.clone();//clonez o noua tabla
            newBoard.dice1 = i;
            newBoard.dice2 = j;
            v = v + alphabetanode(newBoard, alpha/p1, beta/p1, depth-1, MINNODE, k-1).getValue()*p1;//intorc ce returneaza min pentru toate zarurile
            p = p -p1;
            //reducere pruning
            if(p*evaluation.MAX_SCORE <= alpha-v) {
              return new Data(k-1, (v-new Float(0.0001f)));
            }
            k++;
          }
        }
        //dubla
        newBoard = (Tabla)b.clone();
        newBoard.dice1 = i;
        newBoard.dice2 = i;
        v = v + alphabetanode(newBoard, alpha/p2, beta/p2, depth-1, MINNODE, k-1).getValue()*p2;//intorc ce returneaza min pentru toate zarurile
        p = p - p2;
        //reducere pruning
        if( (p*evaluation.MAX_SCORE <= alpha - v) && (i!= 6)) {
          return new Data(k, (v-new Float(0.0001f)));
        }
        k++;
      }
      Data rVal = new Data(-1, v);
      return rVal;
    }
    else {//if (nodeType == MINNODE) {//MINNODE
      Mutari ms = b.getValidMoves(this.oponent);//toate mutarile valide
      Tabla newBoard;//tabla noua
      Data v = new Data(-1, evaluation.MAX_SCORE);
      Data tmp;
      for(int i = 0 ; i < ms.getSize() ; i++) {
        newBoard = (Tabla)b.clone();//clonez o tabla noua
        newBoard.aplicaMutare(oponent, ms.getMove(i));//aplica o mutare
        if(newBoard.aCastigat(oponent)) //daca nod frunza intorc scorul tablei
          v.setTo(new Data(i, evaluation.boardScore(b, oponent)));
        else {//altfel aleg minimul din ce returneaza max
          tmp = alphabetanode(newBoard, alpha, beta, depth-1, MAXCHANCENODE, i);
          if(tmp.getValue() < v.getValue())
            v.setTo(i, tmp.getValue());
          else
            v.setTo(v.getIndex(), v.getValue());
        }
        //reducere pruning
        if(v.getValue() <= alpha) {
          return v;
        }
        beta = max(beta, v);
      }
      return v;
    }
  }

  public Float max(Float a, Data b) {
    if(a > b.getValue())
      return a;
    return b.getValue();
  }

  public Float min(Float a, Data b) {
    if( a < b.getValue())
      return a;
    return b.getValue();
  }
}
