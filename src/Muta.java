public class Muta {
  public int sursa;
  public int dest;
  public int zar;

  public Muta(int sursa, int dest, int zar) {
    this.sursa = sursa;
    this.dest = dest;
    this.zar = zar;
  }

  public boolean maiMic(Muta m) {
    if(sursa > m.sursa)
      return false;
    else if(sursa < m.sursa)
      return  true;
    else
      return dest < m.dest;
  }

  public boolean equals(Muta m) {
    if(this == m)
      return true;
    else if (!(m instanceof Muta))
      return false;
    else
      return sursa == m.sursa && dest == m.dest;
  }
}
