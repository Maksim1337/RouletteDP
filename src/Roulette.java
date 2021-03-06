import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Roulette implements Subject, ActionListener {

  // ~~~ BUTTONS ~~~
  private Button[] buttonsList;
  private final int buttonLimit = 37; // including the 0

  // ~~~ USERS ~~~
  private User user;
  private NpcPlayer npc;

  // ~~~ States ~~~
  private GameState currentState;

  // ~~~ ROULETTE ~~~
  private boolean isSpinning = false;
  private int winningNumber = -1; // hardcoded, needs to be random
  private boolean youWon = false;
  private boolean npcWon = false;
  private List<Observer> observers;

  // ~~~ TESTING
  private boolean testing = false;



  Roulette() {
    buttonsList = new Button[buttonLimit]; // Instantiate array, set the size of the button list
    makeButtons();
    user = new User(); // Create the user playing
    npc = new NpcPlayer(); // Create the NPC
    observers = new ArrayList<Observer>();

    // Register observers
    register(user); // let the user observe
    register(npc); // let the npc observe

    // Set state
    currentState = new SelectNumberState();

    // Testing
    if(testing) {
      for (Button element : buttonsList) {
        System.out.println(element);
      }
    }

  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ SETTERS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /* Increase bet */
  public void increaseUserBet(){
    user.getBetObject().increaseBet();
    notifyObservers();
  }

  /* Decrease bet */
  public void decreaseUserBet(){
    user.getBetObject().decreaseBet();
    notifyObservers();
  }

  /* Set winning number */
  public void generateRandomNumber() {
    Random random = new Random();
    winningNumber = random.nextInt(buttonLimit);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ GETTERS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /* Get buttons */
  public Button[] getButtonsList() {
    return buttonsList;
  }

  // Get the winning number
  public int getWinningNumber() {
    return winningNumber;
  }

  public boolean getIsSpinning(){return isSpinning;}

  // Find if you win or not
  public boolean didYouWin() {
    return youWon;
  }

  // Find if the NPC won or not
  public boolean didNPCWon() {
    return npcWon;
  }

  // Get user object
  public User getUser() {
    return user;
  }

  // Get NPC object
  public NpcPlayer getNpc() {
    return npc;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  // Make buttons
  public void makeButtons() {
    for(int i=0; i<buttonLimit; i++) {
      if(i==0) {
        buttonsList[0] = ButtonFactory.createButton("green", 0);
      }
      else if(i%2 == 0) {
        buttonsList[i] = ButtonFactory.createButton("black", i );
      }
      else {
        buttonsList[i] = ButtonFactory.createButton("red", i);
      }

      buttonsList[i].addListener(this);
    }
  }

  // Enable or disable all number buttons
  public void enableAllNumberButtons(boolean enable)
  {
    for (Button button : buttonsList) {
      JButton jButton = (JButton)button;
      jButton.setEnabled(enable);
    }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ OBSERVER PATTERN METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  @Override
  public void register(Observer observer) {
    if(observer != null) {
      this.observers.add(observer);
    }
    else {
      System.out.println("Null object passed");
    }
  }

  @Override
  public void unregister(Observer observer) {
    if(observer != null) {
      this.observers.remove(observer);
    }
  }

  public void unregisterAll()
  {
    observers = new ArrayList<Observer>();
  }

  @Override
  public void notifyObservers() {
    Iterator<Observer> it = observers.iterator();
    while (it.hasNext()) {
      Observer observer = it.next();
      observer.update(this);
    }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  @Override
  public void actionPerformed(ActionEvent e) {
    Button buttonPressed = (Button)e.getSource();
    this.selectNumber(buttonPressed.getNumber());
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ STATE HANDLERS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  public GameState getState(){ return currentState; }
  public void setState(GameState newState){
    currentState = newState;
  }

  /***
   * Select a number to bet on and go to the 'PlaceBet' state
   * @param number The number to place a bet on
   */
  public void selectNumber(int number){
    user.setBetNumber(number);
    currentState.selectNumber(this);

    // Notify the observers
    notifyObservers();
  }

  /***
   * Placed a bet and going to the spin state
   */
  public void placeBet(){

    currentState.placeBet(this);
    notifyObservers();
  }

  /***
   * The final step towards glory: Spin the roulette!
   * After this, go back to the SelectNumber state again
   */
  public void spinRoulette(){
    generateRandomNumber();
    youWon = user.getBetObject().getBetNumber() == getWinningNumber();
    npcWon = npc.getBetObject().getBetNumber() == getWinningNumber();
    isSpinning = true;
    notifyObservers();

    // Won the game?
    if (wonGame()){
      toYouWin();
    }
    // Lost the game?
    else if(lostGame())
    {
      toGameOver();
    }
    // Continue the game if not
    else{
      // Return to SelectNumberState
      currentState.spinRoulette(this);
      notifyObservers();
    }

    isSpinning = false;
  }

  /***
   * Go to the Game Over screen
   */
  public void toGameOver()
  {
    currentState.toGameOver(this);
    notifyObservers();
  }

  /***
   * Go to the You Win screen
   */
  public void toYouWin()
  {
    currentState.toYouWin(this);
    notifyObservers();
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ WIN/LOSE CONDITIONS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  public boolean wonGame()
  {
    return user.getPlayerCredits() >= 20000 || npc.getPlayerCredits() <= 0;
  }

  public boolean lostGame()
  {
    return user.getPlayerCredits() <= 0;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  public void restartGame()
  {
    user = new User(); // Create the user playing
    npc = new NpcPlayer(); // Create the NPC

    // Register observers
    register(user); // let the user observe
    register(npc); // let the npc observe

    winningNumber = -1;

    currentState.selectNumber(this);
    notifyObservers();
  }
}
