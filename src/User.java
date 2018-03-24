public class User implements Player{
  private int playerCredits; // react to changes, observer
  private Bet bet;

  User() {
    playerCredits = 10000; // initial credits
    bet = new Bet();
  }

  @Override
  /* Get player credits */
  public int getPlayerCredits() {
    return playerCredits;
  }

  /* Set player credits */
  public void adjustPlayerCredits(int byHowMuch, boolean increase) {
    if(increase) {
      this.playerCredits += byHowMuch;
    }
    // Decrease
    else {
      this.playerCredits -= byHowMuch;
    }
  }

  @Override
  public Bet getBetObject() {
    return bet;
  }

  @Override
  public void update(Object obj) {

    // Roulette is pushing the notifications
    if(obj instanceof Roulette) {
      User user = ((Roulette) obj).getUser();

      // Check if you have won from that spin
      if(((Roulette) obj).didYouWin()) {
        // You won, increase credits
        adjustPlayerCredits(user.getBetObject().getBet(), true);
      }
      else {
        // You lost, deduct credits
        adjustPlayerCredits(user.getBetObject().getBet(), false);
      }
    }
  }

}
