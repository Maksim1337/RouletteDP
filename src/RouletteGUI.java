import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

public class RouletteGUI implements Observer{
  private Roulette roulette;
  private JFrame frame;

  // ~~ Labels
  private JLabel rouletteLabel;
  private JLabel yourAmountLbl;
  private JLabel npcAmountLbl;
  private JLabel betAmountLbl;
  private JLabel selectNumberLbl;
  private JLabel lastWinningNumberLbl;

  // ~~~ Buttons ~~~
  private JButton increaseBetBtn;
  private JButton decreaseBetBtn;
  private JButton lockNumberBtn;
  private JButton spinRouletteBtn;
  private JButton playAgainButton;

  RouletteGUI() {
    roulette = new Roulette();
    roulette.register(this);

    // ~~~ Labels ~~~
    rouletteLabel = new JLabel("Spin the roulette!");
    rouletteLabel.setHorizontalAlignment(SwingConstants.CENTER);
    rouletteLabel.setFont(new Font("Helvetica", Font.BOLD, 25));
    rouletteLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

    yourAmountLbl = new JLabel("|| Your amount: " + roulette.getUser().getPlayerCredits() + "||");
    npcAmountLbl = new JLabel("|| NPC amount: " + roulette.getNpc().getPlayerCredits() + "||");
    betAmountLbl = new JLabel("|| Bet: " + roulette.getUser().getBetObject().getBet() + "||");
    selectNumberLbl = new JLabel("|| Your selected number: Unknown ||");
    lastWinningNumberLbl = new JLabel("Last winning number: -");
    lastWinningNumberLbl.setHorizontalAlignment(SwingConstants.CENTER);
    lastWinningNumberLbl.setFont(new Font("Helvetica", Font.BOLD, 18));

    // ~~~ Buttons ~~
    increaseBetBtn = new JButton("+");
    increaseBetBtn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
    increaseBetBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        roulette.increaseUserBet();
      }
    });

    decreaseBetBtn = new JButton("-");
    decreaseBetBtn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
    decreaseBetBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        roulette.decreaseUserBet();
      }
    });

    lockNumberBtn = new JButton("Lock");
    lockNumberBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        roulette.getUser().getBetObject().confirmBet();
        roulette.placeBet();
      }
    });

    spinRouletteBtn = new JButton("Spin roulette!");
    spinRouletteBtn.setFont(new Font("Helvetica", Font.BOLD, 18));
    spinRouletteBtn.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
    spinRouletteBtn.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        roulette.spinRoulette();
      }
    });

    playAgainButton = new JButton("Play Again");
    playAgainButton.setFont(new Font("Helvetica", Font.BOLD, 18));
    playAgainButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        roulette.unregisterAll();
        paintMainScreen();
        roulette.restartGame();

      }
    });

    // Disable all buttons at first
    increaseBetBtn.setEnabled(false);
    decreaseBetBtn.setEnabled(false);
    lockNumberBtn.setEnabled(false);

    makeFrame(); // don't move upwards
  }

  private void makeFrame() {
    frame = new JFrame("Roulette - the game");

    // set the initial frame size
    frame.setSize(1100, 600);

    // center the frame and make it visible
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation(d.width/2 - frame.getWidth()/2, d.height/2 - frame.getHeight()/2);

    Container contentPane = frame.getContentPane();

    // Border layout for container
    contentPane.setLayout(new BorderLayout());

    // cosmetic section
    contentPane.setBackground(Color.LIGHT_GRAY);

    // Spin the roulette title
    contentPane.add(rouletteLabel, BorderLayout.PAGE_START);

    // Center (number buttons and amounts/bets)
    JPanel centerPanel = new JPanel(new BorderLayout());
    makeButtonGrid(centerPanel);
    makeAmountAndBet(centerPanel);
    contentPane.add(centerPanel, BorderLayout.CENTER);

    // Spin it button
    contentPane.add(spinRouletteBtn, BorderLayout.PAGE_END);

    frame.setVisible(true);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ GETTERS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  // Get roulette
  public Roulette getRoulette() {
    return roulette;
  }

  @Override
  public void update(Object obj) {

    // Update these labels ALWAYS
    yourAmountLbl.setText("|| Your amount: " + roulette.getUser().getPlayerCredits() + "||");
    betAmountLbl.setText("|| Bet: " + roulette.getUser().getBetObject().getBet() + "||");
    npcAmountLbl.setText("|| Npc amount: " + roulette.getNpc().getPlayerCredits() + "||");

    if (roulette.getWinningNumber() >= 0)
    {
      lastWinningNumberLbl.setText("Last winning number: " + roulette.getWinningNumber());
    }
    else{
      lastWinningNumberLbl.setText("Last winning number: -");
    }


    // SelectNumberState specific GUI updates
    if (roulette.getState() instanceof  SelectNumberState)
    {
      selectNumberLbl.setText("|| Your selected number: unknown ||");
      roulette.enableAllNumberButtons(true);
      increaseBetBtn.setEnabled(false);
      decreaseBetBtn.setEnabled(false);
      lockNumberBtn.setEnabled(false);
    }

    // PlaceBetState specific GUI updates
    if (roulette.getState() instanceof PlaceBetState)
    {
      selectNumberLbl.setText("|| Your selected number: " + roulette.getUser().getBetObject().getBetNumber() + "||");
      roulette.enableAllNumberButtons(false);
      increaseBetBtn.setEnabled(true);
      decreaseBetBtn.setEnabled(true);
      lockNumberBtn.setEnabled(true);
    }

    // SpinRouletteState specific GUI updates
    if (roulette.getState() instanceof SpinRouletteState)
    {
      selectNumberLbl.setText("|| Your selected number: " + roulette.getUser().getBetObject().getBetNumber() + "||");
      roulette.enableAllNumberButtons(false);
      increaseBetBtn.setEnabled(false);
      decreaseBetBtn.setEnabled(false);
      lockNumberBtn.setEnabled(false);
    }

    // You Win screen
    if (roulette.getState() instanceof YouWinState)
    {
      paintResultScreen("You win!");
    }

    // Game Over screen
    if (roulette.getState() instanceof GameOverState)
    {
      paintResultScreen("Game Over");
    }
  }

  /***
   * Creates the grid with the number buttons
   * @param panel The total container to store the grid in
   */
  private void makeButtonGrid(JPanel panel)
  {
    JPanel buttonsPanel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);
    c.weightx = 0.5;
    c.ipady = 30;

    // TODO Make rows and columns dynamic?
    int x = 0;
    int y = 0;
    for(Button element : roulette.getButtonsList()) {
      if (element.getNumber() == 0)
      {
        c.gridheight = 3;
        c.fill = GridBagConstraints.VERTICAL;
      }
      else
      {
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
      }

      c.gridx = x;
      c.gridy = y;

      buttonsPanel.add((JButton)element, c);

      x++;
      if (element.getNumber() % 12 == 0 && element.getNumber() > 1)
      {
        x = 1;
        y++;
      }
    }

    panel.add(buttonsPanel, BorderLayout.PAGE_START);
  }

  /***
   * Creates the amount and bet section (for the player and the NPC)
   * @param panel The total container to store the section in
   */
  private void makeAmountAndBet(JPanel panel)
  {
    JPanel amountAndBetPanel = new JPanel(new BorderLayout());
    amountAndBetPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));

    JPanel yourSelectedNumberAmountAndBet = new JPanel(new BorderLayout());
    JPanel yourAmountAndBet = new JPanel(new BorderLayout());
    JPanel npcAmountAndBet = new JPanel(new BorderLayout());

    // Your selected number
    yourSelectedNumberAmountAndBet.add(selectNumberLbl, BorderLayout.PAGE_START);

    // Your amount and bet
    yourAmountAndBet.add(yourAmountLbl, BorderLayout.PAGE_START);
    JPanel yourBetPanel = new JPanel(new BorderLayout());
    yourBetPanel.add(increaseBetBtn, BorderLayout.LINE_START);
    betAmountLbl.setHorizontalAlignment(SwingConstants.CENTER);
    yourBetPanel.add(betAmountLbl, BorderLayout.CENTER);
    yourBetPanel.add(decreaseBetBtn, BorderLayout.LINE_END);
    yourBetPanel.add(lockNumberBtn, BorderLayout.PAGE_END);
    yourAmountAndBet.add(yourBetPanel, BorderLayout.PAGE_END);

    yourSelectedNumberAmountAndBet.add(yourAmountAndBet, BorderLayout.PAGE_END);

    // NPC amount and bet
    npcAmountAndBet.add(npcAmountLbl, BorderLayout.PAGE_START);
    npcAmountAndBet.add(new JLabel("A very nice bet"), BorderLayout.PAGE_END);

    amountAndBetPanel.add(yourSelectedNumberAmountAndBet, BorderLayout.LINE_START);
    amountAndBetPanel.add(lastWinningNumberLbl, BorderLayout.CENTER);
    amountAndBetPanel.add(npcAmountAndBet, BorderLayout.LINE_END);

    panel.add(amountAndBetPanel, BorderLayout.PAGE_END);
  }


  /***
   * Paint the 'You Win' or 'Game Over' screen
   */
  private void paintResultScreen(String result)
  {
    // Using a GridBagLayout to center the elements in the screen (only GridBagLayout does that)
    Container container = frame.getContentPane();
    container.removeAll();
    container.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(10, 10, 10, 10);

    JLabel youLoseLabel = new JLabel(result);
    youLoseLabel.setHorizontalAlignment(SwingConstants.CENTER);
    youLoseLabel.setFont(new Font("Helvetica", Font.BOLD, 40));

    container.add(youLoseLabel, c);
    c.gridy = 1;
    container.add(playAgainButton, c);

    container.repaint();
  }

  private void paintMainScreen()
  {
    Container container = frame.getContentPane();
    container.removeAll();
    container.setLayout(new BorderLayout());

    // Spin the roulette title
    container.add(rouletteLabel, BorderLayout.PAGE_START);

    // Center (number buttons and amounts/bets)
    JPanel centerPanel = new JPanel(new BorderLayout());
    makeButtonGrid(centerPanel);
    makeAmountAndBet(centerPanel);
    container.add(centerPanel, BorderLayout.CENTER);

    // Spin it button
    container.add(spinRouletteBtn, BorderLayout.PAGE_END);

    container.repaint();
    frame.setVisible(true);

    roulette.register(this);
  }
}