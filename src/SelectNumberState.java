import javax.swing.*;

public class SelectNumberState implements GameState
{

    @Override
    public void selectNumber(Roulette roulette) {
        roulette.setState(new PlaceBetState());
        JOptionPane.showMessageDialog(null, "Entered bet state!");
    }

    @Override
    public void placeBet(Roulette roulette) {
        JOptionPane.showMessageDialog(null, "Please select a number first");
    }

    @Override
    public void spinRoulette(Roulette roulette) {
        JOptionPane.showMessageDialog(null, "Please select a number first");
    }
}